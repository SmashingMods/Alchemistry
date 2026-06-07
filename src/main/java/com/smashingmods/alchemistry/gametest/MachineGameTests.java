package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * In-world behaviour tests for the Alchemistry machines, starting with the dissolver. These are the machine
 * half of P4.alchemistry.5 -- the reactor half lands separately (.5b). Every test here is {@code required=false}
 * so it can never gate the {@code gameTestServer} exit code; the lone {@code required=true} gate stays the
 * full-chain load-smoke in {@link AlchemistryGameTests}. A {@code required=false} failure still prints noise to
 * the log, so each assertion is written to pass reliably rather than chase an exact probabilistic roll.
 *
 * <p>Like {@link AlchemistryGameTests} this class lives in {@code src/main} so the mod scan registers it for the
 * {@code gameTestServer} run, but the {@code jar} task excludes the {@code gametest} package so it never ships.
 * Each test pins {@code template = "loadsemptytemplate"} (the staged 3x3x3 air structure) with
 * {@code @PrefixGameTestTemplate(false)} so the id resolves un-prefixed to {@code alchemistry:loadsemptytemplate};
 * a single machine block plus a mock player fits inside it. Bodies stay as thin plain helpers -- forward-compat
 * for the Phase-10 (1.21.5) gametest rewrite.</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class MachineGameTests {

    // Centre of the 3x3x3 structure, well clear of the structure block, so the placed machine ticks in isolation.
    private static final BlockPos MACHINE_POS = new BlockPos(1, 1, 1);

    // A vanilla item with a deterministic dissolver recipe: minecraft:iron_ingot -> chemlib:iron x16, a single
    // non-weighted group at probability 100. Because the group is non-weighted and totals 100 the builder adds no
    // "nothing" group, so calculateOutput always yields chemlib:iron -- the output handler is guaranteed non-empty
    // within the operation's tick budget, keeping the processing test non-flaky.
    private static final Item DISSOLVABLE = Items.IRON_INGOT;

    /**
     * Drives one full dissolve operation end-to-end: place the dissolver, seed it with energy and a dissolvable
     * input stack, let the block-entity tick (its block's server ticker calls {@link DissolverBlockEntity#tick()}
     * every tick), then assert the output handler becomes non-empty and every output is a member of the resolved
     * recipe's declared possible outputs. Membership -- not an exact roll -- because the dissolver output is
     * probabilistic. The default 50-tick operation plus buffer transfer completes inside the 100-tick timeout.
     */
    @GameTest(required = false, template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void dissolverProcessing(GameTestHelper helper) {
        DissolverBlockEntity dissolver = placeDissolver(helper);

        // Plenty of energy for the whole operation (default 100 FE/tick x 50 ticks = 5000 FE, capacity 100k).
        dissolver.getEnergyHandler().setEnergy(Integer.MAX_VALUE);
        dissolver.getInputHandler().setStackInSlot(0, new ItemStack(DISSOLVABLE));

        Set<Item> allowedOutputs = possibleOutputs(helper, new ItemStack(DISSOLVABLE));

        // succeedWhen re-runs the criterion each tick until it passes or the test times out, so it naturally waits
        // for the operation to finish without manual tick driving.
        helper.succeedWhen(() -> {
            ItemStack produced = firstOutput(dissolver);
            helper.assertFalse(produced.isEmpty(), "dissolver produced no output");
            assertOutputsAreMembers(helper, dissolver, allowedOutputs);
        });
    }

    /**
     * Asserts the dissolver recipes actually loaded. Measures the real recipe count -- the size of the
     * dissolver-type recipe list from {@link RecipeRegistry} -- not the server's "Loaded N recipes" log line, which
     * counts recipe types rather than entries and would be misleading here.
     */
    @GameTest(required = false, template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void recipeResolution(GameTestHelper helper) {
        int count = RecipeRegistry.getDissolverRecipes(helper.getLevel()).size();
        helper.assertTrue(count > 0, "expected dissolver recipes to load, found " + count);
        helper.succeed();
    }

    /**
     * Asserts the dissolver block-entity exposes a working item handler capability. NeoForge 20.2 still uses the
     * legacy capability system ({@link Capabilities#ITEM_HANDLER} resolved through
     * {@code BlockEntity#getCapability} returning a {@code LazyOptional}); the newer
     * {@code level.getCapability(pos, ...)} object-capability API does not exist on this version. The handler must
     * be present and expose at least one slot.
     */
    @GameTest(required = false, template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void machineCapability(GameTestHelper helper) {
        DissolverBlockEntity dissolver = placeDissolver(helper);

        IItemHandler handler = dissolver.getCapability(Capabilities.ITEM_HANDLER, null)
                .resolve()
                .orElse(null);

        helper.assertTrue(handler != null, "dissolver did not expose an item handler capability");
        helper.assertTrue(handler.getSlots() > 0, "dissolver item handler exposed no slots");
        helper.succeed();
    }

    /**
     * Proves the dissolver yields its container menu. Opening a full container needs a real {@code ServerPlayer}
     * and network connection, which is impractical in a gametest, so this asserts coarsely-but-really against the
     * production menu-provider path: the block-entity is a {@code MenuProvider} whose
     * {@link DissolverBlockEntity#createMenu(int, net.minecraft.world.entity.player.Inventory, Player)} returns a
     * {@link DissolverMenu}. A mock player supplies the inventory the menu constructor needs.
     */
    @GameTest(required = false, template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void menuOpens(GameTestHelper helper) {
        DissolverBlockEntity dissolver = placeDissolver(helper);

        Player player = helper.makeMockPlayer();
        AbstractContainerMenu menu = dissolver.createMenu(0, player.getInventory(), player);

        helper.assertTrue(menu instanceof DissolverMenu,
                "dissolver did not produce a DissolverMenu, got " + (menu == null ? "null" : menu.getClass().getSimpleName()));
        helper.succeed();
    }

    // Places a dissolver at the structure centre and returns its block-entity, failing the test if either the
    // block or its block-entity is missing. Block-entity positions are structure-relative; getBlockEntity converts
    // to the absolute world position for us.
    private static DissolverBlockEntity placeDissolver(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.DISSOLVER.get());
        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof DissolverBlockEntity dissolver)) {
            helper.fail("expected a DissolverBlockEntity at " + MACHINE_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return dissolver;
    }

    // The set of every item the resolved recipe can output, flattened across its probability groups. Used as the
    // membership oracle for the probabilistic processing output.
    private static Set<Item> possibleOutputs(GameTestHelper helper, ItemStack input) {
        DissolverRecipe recipe = RecipeRegistry.getDissolverRecipe(r -> r.matches(input), helper.getLevel())
                .orElseThrow(() -> new AssertionError("no dissolver recipe matched " + input));
        Set<Item> outputs = new HashSet<>();
        for (ProbabilityGroup group : recipe.getOutput().getProbabilityGroups()) {
            for (ItemStack stack : group.getOutput()) {
                if (!stack.isEmpty()) {
                    outputs.add(stack.getItem());
                }
            }
        }
        return outputs;
    }

    // First non-empty stack in the output handler, or EMPTY if the operation has not produced anything yet.
    private static ItemStack firstOutput(DissolverBlockEntity dissolver) {
        IItemHandler output = dissolver.getOutputHandler();
        for (int slot = 0; slot < output.getSlots(); slot++) {
            ItemStack stack = output.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void assertOutputsAreMembers(GameTestHelper helper, DissolverBlockEntity dissolver, Set<Item> allowed) {
        IItemHandler output = dissolver.getOutputHandler();
        for (int slot = 0; slot < output.getSlots(); slot++) {
            ItemStack stack = output.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                helper.assertTrue(allowed.contains(stack.getItem()),
                        "dissolver output " + stack.getItem() + " is not a declared recipe output");
            }
        }
    }
}
