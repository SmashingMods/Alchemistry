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
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * In-world behaviour tests for the Alchemistry machines, starting with the dissolver ({@code ReactorGameTests}
 * covers the reactor multiblock). Every test here is {@code required=true}
 * (the {@code @GameTest} default), so a failure fails the {@code gameTestServer} gate alongside the full-chain
 * load-smoke in {@link AlchemistryGameTests}. Because a failure is a gate failure, each assertion is written
 * to pass reliably -- membership in the resolved recipe's outputs, or a deterministic non-weighted recipe --
 * rather than chase an exact probabilistic roll.
 *
 * <p>Like {@link AlchemistryGameTests} this class lives in {@code src/main} so the mod scan registers it for the
 * {@code gameTestServer} run, but the {@code jar} task excludes the {@code gametest} package so it never ships.
 * Each test pins {@code template = "loadsemptytemplate"} (the staged 3x3x3 air structure) with
 * {@code @PrefixGameTestTemplate(false)} so the id resolves un-prefixed to {@code alchemistry:loadsemptytemplate};
 * a single machine block plus a mock player fits inside it. Bodies stay as thin plain helpers so the
 * {@code @GameTest} methods stay thin.</p>
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

    // A vanilla item whose deterministic dissolver recipe yields a single oversized output stack: minecraft:iron_block
    // -> chemlib:iron x144 (16 * 9), one non-weighted group at probability 100. The 144 count exceeds a slot's 64-stack
    // limit, so the recipe data stores it as one >64 stack and the block-entity's buffer drains it across multiple
    // output slots. 144 fits comfortably inside the 12-slot (768-item) output handler, so a single operation settles
    // fully in one drain -- the deterministic no-loss case for an oversized >64 stack.
    private static final Item DISSOLVABLE_BULK = Items.IRON_BLOCK;

    /**
     * Drives one full dissolve operation end-to-end: place the dissolver, seed it with energy and a dissolvable
     * input stack, let the block-entity tick (its block's server ticker calls {@link DissolverBlockEntity#tick()}
     * every tick), then assert the output handler becomes non-empty and every output is a member of the resolved
     * recipe's declared possible outputs. Membership -- not an exact roll -- because the dissolver output is
     * probabilistic. The default 50-tick operation plus buffer transfer completes inside the 100-tick timeout.
     */
    @GameTest(template = "loadsemptytemplate")
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
     * No-loss guarantee for the dissolver buffer: an output exceeding a single 64-stack slot must be
     * delivered in full, never silently truncated. Seeds one {@link #DISSOLVABLE_BULK} (iron_block -> chemlib:iron
     * x144), runs the operation, then asserts the output handler holds the full 144 summed across its slots and that
     * every non-empty slot is the expected output item -- i.e. nothing was dropped on the >64 buffer transfer.
     *
     * <p>The recipe is deterministic (one non-weighted group at probability 100, a single result item), so the
     * expected count is exactly the group's stack count; this is asserted to be {@code > 64} so the test genuinely
     * exercises the oversized-stack path rather than a single ordinary stack. A single iron_block (input count 1) is
     * seeded, and {@code canProcessRecipe} blocks a second operation once the input is consumed and while the buffer
     * is non-empty, so the total settles at exactly one operation's output. 144 fits inside the 12-slot (768-item)
     * output, so it drains in one pass; a recipe larger than the output (e.g. diamond_block -> graphite x1152) would
     * instead buffer the remainder and need the output drained across several ticks (also supported, but omitted
     * here to keep the assertion deterministic).</p>
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void dissolverProcessingNoLoss(GameTestHelper helper) {
        DissolverBlockEntity dissolver = placeDissolver(helper);

        dissolver.getEnergyHandler().setEnergy(Integer.MAX_VALUE);
        dissolver.getInputHandler().setStackInSlot(0, new ItemStack(DISSOLVABLE_BULK));

        Set<Item> allowedOutputs = possibleOutputs(helper, new ItemStack(DISSOLVABLE_BULK));
        int expected = expectedDeterministicOutputCount(helper, new ItemStack(DISSOLVABLE_BULK));
        helper.assertTrue(expected > 64,
                "no-loss test must use a >64 output to exercise the oversized-stack path, got " + expected);

        helper.succeedWhen(() -> {
            assertOutputsAreMembers(helper, dissolver, allowedOutputs);
            int total = totalOutputCount(dissolver);
            helper.assertTrue(total == expected,
                    "dissolver dropped output on the >64 buffer transfer: expected " + expected + ", found " + total);
        });
    }

    /**
     * Asserts the dissolver recipes actually loaded. Measures the real recipe count -- the size of the
     * dissolver-type recipe list from {@link RecipeRegistry} -- not the server's "Loaded N recipes" log line, which
     * counts recipe types rather than entries and would be misleading here.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void recipeResolution(GameTestHelper helper) {
        int count = RecipeRegistry.getDissolverRecipes(helper.getLevel()).size();
        helper.assertTrue(count > 0, "expected dissolver recipes to load, found " + count);
        helper.succeed();
    }

    /**
     * Asserts the dissolver block-entity exposes a working item handler capability. NeoForge 20.4 uses the
     * object-capability system: the handler is queried off the level with
     * {@code level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side)}, which returns a plain nullable
     * {@link IItemHandler} rather than a wrapped optional. The handler must be present and expose at least one slot.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void machineCapability(GameTestHelper helper) {
        DissolverBlockEntity dissolver = placeDissolver(helper);

        IItemHandler handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, dissolver.getBlockPos(), null);

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
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void menuOpens(GameTestHelper helper) {
        DissolverBlockEntity dissolver = placeDissolver(helper);

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        AbstractContainerMenu menu = dissolver.createMenu(0, player.getInventory(), player);

        helper.assertTrue(menu instanceof DissolverMenu,
                "dissolver did not produce a DissolverMenu, got " + (menu == null ? "null" : menu.getClass().getSimpleName()));
        helper.succeed();
    }

    // The fusion-transfer test (driving FusionTransferPacket's server handler) is gated on the JEI source: the
    // transfer packet lives in common/network/jei/, which is excluded from compilation while JEI has no 1.21.3
    // build (see build.gradle's sourceSets.main.java exclude). It is restored at 1.21.4 alongside JEI.

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

    // Total item count held across every output slot. A >64 recipe output spans several 64-stack slots, so the
    // no-loss check sums them rather than reading a single slot.
    private static int totalOutputCount(DissolverBlockEntity dissolver) {
        IItemHandler output = dissolver.getOutputHandler();
        int total = 0;
        for (int slot = 0; slot < output.getSlots(); slot++) {
            total += output.getStackInSlot(slot).getCount();
        }
        return total;
    }

    // Expected item count for a deterministic recipe -- the sum of every result-stack count across the recipe's
    // probability groups. Only meaningful for a non-weighted, probability-100 recipe with no "nothing" group (every
    // group always rolls), which is the case for the bulk no-loss input; for a probabilistic recipe this would not
    // be a fixed expectation.
    private static int expectedDeterministicOutputCount(GameTestHelper helper, ItemStack input) {
        DissolverRecipe recipe = RecipeRegistry.getDissolverRecipe(r -> r.matches(input), helper.getLevel())
                .orElseThrow(() -> new AssertionError("no dissolver recipe matched " + input));
        int total = 0;
        for (ProbabilityGroup group : recipe.getOutput().getProbabilityGroups()) {
            for (ItemStack stack : group.getOutput()) {
                if (!stack.isEmpty()) {
                    total += stack.getCount();
                }
            }
        }
        return total;
    }
}
