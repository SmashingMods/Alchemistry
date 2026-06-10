package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

/**
 * In-world tests for the two fluid machines -- the liquifier (item in -> fluid out) and the atomizer
 * (fluid in -> item out). {@code MachineGameTests} drives the standalone item-only machines through the
 * dissolver, and {@code ReactorGameTests} covers the reactor multiblock; neither exercises the fluid I/O seam
 * these two share via {@link com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity}.
 * Both tests here are registered as required by {@link AlchemistryGameTestRegistry}, so a failure fails the
 * {@code gameTestServer} gate alongside the full-chain load-smoke in {@link AlchemistryGameTests}.
 *
 * <p>Like the other test classes the bodies stay here as {@code static} methods and the registration lives in
 * {@link AlchemistryGameTestRegistry}; the package is compiled into {@code src/main} so the mod scan discovers it,
 * but the {@code jar} task excludes it so it never ships. Each test runs against the staged 3x3x3 air structure
 * ({@code alchemistry:loadsemptytemplate}); a single machine block fits inside it.</p>
 *
 * <p>Both tests are data-driven against a loaded recipe rather than a hand-picked input: each resolves any loaded
 * recipe of its type, seeds that recipe's exact input, and asserts that recipe's exact output. Each seeds the input
 * at exactly one operation's worth so the machine settles after a single operation -- {@code canProcessRecipe()}
 * then blocks a second because the input is fully consumed -- which makes the output an exact equality
 * (fluid type + amount, or item + count) rather than a {@code > 0} lower bound.</p>
 */
public class FluidMachineGameTests {

    private FluidMachineGameTests() {}

    // Centre of the 3x3x3 structure, well clear of the structure block, so the placed machine ticks in isolation.
    private static final BlockPos MACHINE_POS = new BlockPos(1, 1, 1);

    /**
     * Drives one full liquify operation end-to-end across the item-in -> fluid-out seam: place the liquifier, resolve
     * a loaded {@link LiquifierRecipe}, seed it with energy and exactly the recipe's item input, let the block-entity
     * tick (its block's server ticker calls {@link LiquifierBlockEntity#tick()} every tick), then assert the fluid
     * storage holds exactly the recipe's output fluid -- correct type and exact amount.
     *
     * <p>The input slot is seeded at exactly {@code recipe.getInput().getCount()}, so the single operation consumes
     * it entirely and {@link LiquifierBlockEntity#canProcessRecipe()} blocks a second (input no longer matches at the
     * required count). The fluid storage therefore settles at exactly one operation's output, making
     * {@code getFluidAmount() == recipe.getOutput().getAmount()} an exact expectation rather than a lower bound.</p>
     *
     * <p>The timeout is raised above the default 100 ticks (see {@link AlchemistryGameTestRegistry}): the liquifier's
     * default operation length is 100 ticks ({@code Config.Common.liquifierTicksPerOperation}), which equals the
     * standard 100-tick timeout, so the fill lands on the operation's final tick -- on the edge of, or just past, the
     * default window. A 200-tick timeout restores headroom so the produced fluid is observed well before the test
     * gives up, without making the wait unbounded.</p>
     */
    public static void liquifierProcessesItemToFluid(GameTestHelper helper) {
        LiquifierBlockEntity liquifier = placeLiquifier(helper);

        LiquifierRecipe recipe = RecipeRegistry.getLiquifierRecipe(r -> true, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no liquifier recipe loaded"));

        // One concrete seedable stack from the recipe's ingredient, already carried at the ingredient's required
        // count by toStacks(). An ingredient resolves to at least one item, so the list is non-empty; assert it
        // rather than risk an opaque index error, which would also mask a recipe that resolved to nothing.
        List<ItemStack> inputStacks = recipe.getInput().toStacks();
        helper.assertFalse(inputStacks.isEmpty(), Component.literal("liquifier recipe input resolved to no item stacks"));
        ItemStack input = inputStacks.get(0);

        // Plenty of energy for the whole operation; the fill capacity comfortably holds one operation's output.
        liquifier.getEnergyHandler().setEnergy(Integer.MAX_VALUE);
        liquifier.getInputHandler().setStackInSlot(0, input);

        // succeedWhen re-runs the criterion each tick until it passes or the test times out, so it naturally waits
        // for the operation to finish without manual tick driving.
        helper.succeedWhen(() -> {
            helper.assertFalse(liquifier.getFluidStorage().isEmpty(), Component.literal("liquifier produced no fluid"));
            helper.assertTrue(FluidStack.isSameFluidSameComponents(liquifier.getFluidStorage().getFluid(), recipe.getOutput()),
                    Component.literal("liquifier output fluid is not the recipe's output fluid: expected " + recipe.getOutput().getFluid()
                            + ", found " + liquifier.getFluidStorage().getFluid().getFluid()));
            helper.assertTrue(liquifier.getFluidStorage().getFluidAmount() == recipe.getOutput().getAmount(),
                    Component.literal("liquifier output amount expected " + recipe.getOutput().getAmount()
                            + ", found " + liquifier.getFluidStorage().getFluidAmount()));
        });
    }

    /**
     * Drives one full atomize operation end-to-end across the fluid-in -> item-out seam: place the atomizer, resolve
     * a loaded {@link AtomizerRecipe}, seed it with energy and exactly the recipe's input fluid, let the block-entity
     * tick (its block's server ticker calls {@link AtomizerBlockEntity#tick()} every tick), then assert the output
     * handler holds exactly the recipe's output item -- correct item and exact count.
     *
     * <p>The fluid storage is seeded with exactly {@code recipe.getInput()} (one operation's worth), so the single
     * operation drains it entirely and {@link AtomizerBlockEntity#canProcessRecipe()} blocks a second (the remaining
     * fluid no longer meets the required amount). The output slot therefore settles at exactly one operation's output,
     * making the item-and-count equality exact rather than a lower bound. The seed fill is asserted to have accepted
     * the full input amount so the precondition cannot silently under-fill and make the test pass vacuously.</p>
     */
    public static void atomizerProcessesFluidToItem(GameTestHelper helper) {
        AtomizerBlockEntity atomizer = placeAtomizer(helper);

        AtomizerRecipe recipe = RecipeRegistry.getAtomizerRecipe(r -> true, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no atomizer recipe loaded"));

        atomizer.getEnergyHandler().setEnergy(Integer.MAX_VALUE);

        // Seed exactly one operation's input fluid and confirm the tank accepted all of it, so the operation settles
        // after a single pass and the output equality stays exact.
        int filled = atomizer.getFluidStorage().fill(recipe.getInput().copy(), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(filled == recipe.getInput().getAmount(),
                Component.literal("atomizer fluid seed under-filled: expected " + recipe.getInput().getAmount() + ", filled " + filled));

        helper.succeedWhen(() -> {
            ItemStack produced = atomizer.getOutputHandler().getStackInSlot(0);
            helper.assertFalse(produced.isEmpty(), Component.literal("atomizer produced no output item"));
            helper.assertTrue(ItemStack.isSameItemSameComponents(produced, recipe.getOutput()),
                    Component.literal("atomizer output item is not the recipe's output: expected " + recipe.getOutput().getItem()
                            + ", found " + produced.getItem()));
            helper.assertTrue(produced.getCount() == recipe.getOutput().getCount(),
                    Component.literal("atomizer output count expected " + recipe.getOutput().getCount() + ", found " + produced.getCount()));
        });
    }

    // Places a liquifier at the structure centre and returns its block-entity, failing the test if either the block
    // or its block-entity is missing. Block-entity positions are structure-relative; getBlockEntity converts to the
    // absolute world position for us.
    private static LiquifierBlockEntity placeLiquifier(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.LIQUIFIER.get());
        LiquifierBlockEntity liquifier = helper.getBlockEntity(MACHINE_POS, LiquifierBlockEntity.class);
        if (liquifier == null) {
            helper.fail(Component.literal("expected a LiquifierBlockEntity at " + MACHINE_POS), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return liquifier;
    }

    // Places an atomizer at the structure centre and returns its block-entity, failing the test if either the block
    // or its block-entity is missing. Block-entity positions are structure-relative; getBlockEntity converts to the
    // absolute world position for us.
    private static AtomizerBlockEntity placeAtomizer(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.ATOMIZER.get());
        AtomizerBlockEntity atomizer = helper.getBlockEntity(MACHINE_POS, AtomizerBlockEntity.class);
        if (atomizer == null) {
            helper.fail(Component.literal("expected an AtomizerBlockEntity at " + MACHINE_POS), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return atomizer;
    }
}
