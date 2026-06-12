package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * In-world behaviour tests for the recipe-selection system. The selector machines (combiner and compactor)
 * resolve their recipe each tick from ambiguous inputs -- oxygen + cellulose is shared by every sapling
 * recipe, cellulose by every log recipe -- so these tests pin the two halves of the contract: with no
 * selection the auto-pick deterministically takes the first matching recipe in sorted order, and a player's
 * selector choice sticks (through the insert, through ticking, through a whole operation) instead of
 * reverting to the auto-pick. Every selection here is driven through
 * {@link SetRecipePacket#applyRecipeSelection}, the same single server-side entry point the packet handler
 * delegates to, so the tests exercise the real selection path rather than a mock.
 *
 * <p>Like {@link MachineGameTests} the bodies stay here as {@code static} methods and the registration lives
 * in {@link AlchemistryGameTestRegistry}; the package is compiled into {@code src/main} so the mod scan
 * discovers it, but the {@code jar} task excludes it so it never ships. Each test runs against the staged
 * 3x3x3 air structure ({@code alchemistry:loadsemptytemplate}). Both machines default to 50 ticks per
 * operation, well inside the default 100-tick test budget.</p>
 */
public class RecipeSelectionGameTests {

    private RecipeSelectionGameTests() {}

    // Centre of the 3x3x3 structure, well clear of the structure block, so the placed machine ticks in isolation.
    private static final BlockPos MACHINE_POS = new BlockPos(1, 1, 1);

    // Combiner sapling recipes sharing the same inputs (1x chemlib:oxygen + 2x chemlib:cellulose).
    // acacia_sapling sorts first by recipe id, so the auto-pick must resolve to it; jungle and oak are
    // deliberately non-first recipes a player would have to select.
    private static final ResourceLocation FIRST_SAPLING_RECIPE = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "combiner/acacia_sapling");
    private static final ResourceLocation JUNGLE_SAPLING_RECIPE = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "combiner/jungle_sapling");
    private static final ResourceLocation OAK_SAPLING_RECIPE = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "combiner/oak_sapling");

    // A compactor log recipe (1x chemlib:cellulose -> jungle log) that is not the first cellulose match
    // (acacia_log sorts before it), so only a sticking selection can produce it.
    private static final ResourceLocation JUNGLE_LOG_RECIPE = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "compactor/jungle_log");

    /**
     * With no selection ever made, ambiguous inputs must auto-pick deterministically: the first matching
     * recipe in sorted order. Seeds oxygen + cellulose (asserting more than one recipe matches them, the
     * precondition that makes the pick ambiguous at all) and expects the combiner to settle on
     * {@link #FIRST_SAPLING_RECIPE}.
     */
    public static void combinerAutoPickDeterministic(GameTestHelper helper) {
        CombinerBlockEntity combiner = placeCombiner(helper);
        seedInputs(combiner, combinerRecipe(helper, FIRST_SAPLING_RECIPE));
        assertAmbiguousCombinerInputs(helper, combiner);

        helper.succeedWhen(() -> {
            CombinerRecipe recipe = combiner.getRecipe();
            helper.assertTrue(recipe != null, Component.literal("combiner never auto-picked a recipe"));
            helper.assertTrue(FIRST_SAPLING_RECIPE.equals(recipe.getId()),
                    Component.literal("auto-pick expected " + FIRST_SAPLING_RECIPE + ", got " + recipe.getId()));
        });
    }

    /**
     * The combiner half of the core regression: a selected recipe must survive inputs arriving and a full
     * operation. Selects jungle sapling through the real selection entry point, seeds the (ambiguous)
     * inputs, and asserts the current recipe is still jungle once the operation completes AND -- the
     * strongest assertion -- that the produced output stack is the jungle sapling. Before the fix the first
     * tick with inputs present replaced the selection with acacia (first in sorted order) and produced
     * acacia saplings.
     */
    public static void combinerSelectionSticksThroughProcessing(GameTestHelper helper) {
        CombinerBlockEntity combiner = placeCombiner(helper);
        CombinerRecipe selected = combinerRecipe(helper, JUNGLE_SAPLING_RECIPE);

        // Plenty of energy for the whole operation (default 200 FE/tick x 50 ticks = 10k FE, capacity 100k).
        combiner.getEnergyHandler().setEnergy(Integer.MAX_VALUE);

        SetRecipePacket.applyRecipeSelection(helper.getLevel(), combiner.getBlockPos(), selected.getGroup(), selected.getId());
        CombinerRecipe applied = combiner.getRecipe();
        helper.assertTrue(applied != null && JUNGLE_SAPLING_RECIPE.equals(applied.getId()),
                Component.literal("selection did not apply: current recipe is " + (applied == null ? "null" : applied.getId())));

        seedInputs(combiner, selected);
        assertAmbiguousCombinerInputs(helper, combiner);

        ItemStack expected = selected.getOutput();
        helper.succeedWhen(() -> {
            CombinerRecipe recipe = combiner.getRecipe();
            helper.assertTrue(recipe != null && JUNGLE_SAPLING_RECIPE.equals(recipe.getId()),
                    Component.literal("selection was replaced by " + (recipe == null ? "null" : recipe.getId())));
            ItemStack output = combiner.getOutputHandler().getStackInSlot(0);
            helper.assertFalse(output.isEmpty(), Component.literal("combiner has not produced output yet"));
            helper.assertTrue(ItemStack.isSameItemSameComponents(output, expected),
                    Component.literal("selection was not honoured: expected " + expected.getItem() + ", produced " + output.getItem()));
        });
    }

    /**
     * The compactor half of the core regression. The compactor lost selections on the very insert -- its
     * input handler's onContentsChanged calls updateRecipe directly -- so this selects jungle log through
     * the real entry point, asserts the selection applied at all (pinning the group+id lookup path), then
     * inserts cellulose and asserts the operation completes on the selection rather than on acacia log,
     * the first cellulose match.
     */
    public static void compactorSelectionSticksThroughProcessing(GameTestHelper helper) {
        CompactorBlockEntity compactor = placeCompactor(helper);
        CompactorRecipe selected = compactorRecipe(helper, JUNGLE_LOG_RECIPE);

        // Plenty of energy for the whole operation (default 50 FE/tick x 50 ticks = 2.5k FE, capacity 100k).
        compactor.getEnergyHandler().setEnergy(Integer.MAX_VALUE);

        SetRecipePacket.applyRecipeSelection(helper.getLevel(), compactor.getBlockPos(), selected.getGroup(), selected.getId());
        CompactorRecipe applied = compactor.getRecipe();
        helper.assertTrue(applied != null && JUNGLE_LOG_RECIPE.equals(applied.getId()),
                Component.literal("selection did not apply: current recipe is " + (applied == null ? "null" : applied.getId())));

        // The insert itself runs updateRecipe (onContentsChanged), the hop that used to drop the selection.
        ItemStack input = selected.getInput().toStacks().get(0);
        compactor.getInputHandler().setStackInSlot(0, input);
        long matching = RecipeRegistry.getCompactorRecipes(helper.getLevel()).stream()
                .filter(recipe -> recipe.getInput().matches(input))
                .count();
        helper.assertTrue(matching > 1,
                Component.literal("ambiguous-input precondition failed: only " + matching + " compactor recipe(s) match " + input.getItem()));

        ItemStack expected = selected.getOutput();
        helper.succeedWhen(() -> {
            CompactorRecipe recipe = compactor.getRecipe();
            helper.assertTrue(recipe != null && JUNGLE_LOG_RECIPE.equals(recipe.getId()),
                    Component.literal("selection was replaced by " + (recipe == null ? "null" : recipe.getId())));
            ItemStack output = compactor.getOutputHandler().getStackInSlot(0);
            helper.assertFalse(output.isEmpty(), Component.literal("compactor has not produced output yet"));
            helper.assertTrue(ItemStack.isSameItemSameComponents(output, expected),
                    Component.literal("selection was not honoured: expected " + expected.getItem() + ", produced " + output.getItem()));
        });
    }

    /**
     * Changing the selection mid-operation. Encodes the restored contract: a new selection takes effect
     * immediately -- the current recipe switches, progress restarts (the selection path has always reset
     * progress for a new recipe), and the machine completes the NEW recipe -- rather than flipping back to
     * the auto-pick within a tick, which is what the regression did to a mid-processing selector click.
     * Starts a jungle-sapling operation, re-selects oak sapling at tick 10 (mid-operation), and asserts the
     * machine finishes with the oak sapling output.
     */
    public static void combinerReselectMidProcessingTakesEffect(GameTestHelper helper) {
        CombinerBlockEntity combiner = placeCombiner(helper);
        CombinerRecipe initial = combinerRecipe(helper, JUNGLE_SAPLING_RECIPE);
        CombinerRecipe replacement = combinerRecipe(helper, OAK_SAPLING_RECIPE);

        combiner.getEnergyHandler().setEnergy(Integer.MAX_VALUE);
        SetRecipePacket.applyRecipeSelection(helper.getLevel(), combiner.getBlockPos(), initial.getGroup(), initial.getId());
        seedInputs(combiner, initial);

        // Both sapling recipes consume the same inputs, so the seeded stacks satisfy the replacement too.
        helper.runAfterDelay(10, () -> {
            helper.assertTrue(combiner.getProgress() > 0, Component.literal("machine should be mid-operation before re-selecting"));
            SetRecipePacket.applyRecipeSelection(helper.getLevel(), combiner.getBlockPos(), replacement.getGroup(), replacement.getId());
            CombinerRecipe recipe = combiner.getRecipe();
            helper.assertTrue(recipe != null && OAK_SAPLING_RECIPE.equals(recipe.getId()),
                    Component.literal("re-selection did not take effect: current recipe is " + (recipe == null ? "null" : recipe.getId())));
            helper.assertTrue(combiner.getProgress() == 0, Component.literal("re-selection must restart the operation"));
        });

        ItemStack expected = replacement.getOutput();
        helper.succeedWhen(() -> {
            CombinerRecipe recipe = combiner.getRecipe();
            helper.assertTrue(recipe != null && OAK_SAPLING_RECIPE.equals(recipe.getId()),
                    Component.literal("re-selection was replaced by " + (recipe == null ? "null" : recipe.getId())));
            ItemStack output = combiner.getOutputHandler().getStackInSlot(0);
            helper.assertFalse(output.isEmpty(), Component.literal("combiner has not produced output yet"));
            helper.assertTrue(ItemStack.isSameItemSameComponents(output, expected),
                    Component.literal("re-selection was not honoured: expected " + expected.getItem() + ", produced " + output.getItem()));
        });
    }

    // Places a combiner at the structure centre and returns its block-entity, failing the test if either the
    // block or its block-entity is missing.
    private static CombinerBlockEntity placeCombiner(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.COMBINER.get());
        CombinerBlockEntity combiner = helper.getBlockEntity(MACHINE_POS, CombinerBlockEntity.class);
        if (combiner == null) {
            helper.fail(Component.literal("expected a CombinerBlockEntity at " + MACHINE_POS), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return combiner;
    }

    // Places a compactor at the structure centre and returns its block-entity, failing the test if either the
    // block or its block-entity is missing.
    private static CompactorBlockEntity placeCompactor(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.COMPACTOR.get());
        CompactorBlockEntity compactor = helper.getBlockEntity(MACHINE_POS, CompactorBlockEntity.class);
        if (compactor == null) {
            helper.fail(Component.literal("expected a CompactorBlockEntity at " + MACHINE_POS), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return compactor;
    }

    // Resolves a combiner recipe by its datapack id, failing loudly if the datapack no longer carries it.
    private static CombinerRecipe combinerRecipe(GameTestHelper helper, ResourceLocation pRecipeId) {
        return RecipeRegistry.getCombinerRecipe(recipe -> pRecipeId.equals(recipe.getId()), helper.getLevel())
                .orElseThrow(() -> new AssertionError("expected combiner recipe " + pRecipeId + " in the datapack"));
    }

    // Resolves a compactor recipe by its datapack id, failing loudly if the datapack no longer carries it.
    private static CompactorRecipe compactorRecipe(GameTestHelper helper, ResourceLocation pRecipeId) {
        return RecipeRegistry.getCompactorRecipe(recipe -> pRecipeId.equals(recipe.getId()), helper.getLevel())
                .orElseThrow(() -> new AssertionError("expected compactor recipe " + pRecipeId + " in the datapack"));
    }

    // Fills the combiner's input slots with exactly one operation's worth of the recipe's ingredients
    // (slot i = ingredient i), the layout the recipe-locked slot validation also assumes.
    private static void seedInputs(CombinerBlockEntity combiner, CombinerRecipe recipe) {
        List<IngredientStack> inputs = recipe.getInput();
        for (int slot = 0; slot < inputs.size(); slot++) {
            combiner.getInputHandler().setStackInSlot(slot, inputs.get(slot).toStacks().get(0));
        }
    }

    // Asserts the seeded inputs are genuinely ambiguous -- matched by more than one combiner recipe -- which
    // is the precondition that makes auto-pick determinism and selection stickiness worth testing at all.
    private static void assertAmbiguousCombinerInputs(GameTestHelper helper, CombinerBlockEntity combiner) {
        long matching = RecipeRegistry.getCombinerRecipes(helper.getLevel()).stream()
                .filter(recipe -> recipe.matchInputs(combiner.getInputHandler().getStacks()))
                .count();
        helper.assertTrue(matching > 1,
                Component.literal("ambiguous-inputs precondition failed: only " + matching + " combiner recipe(s) match"));
    }
}
