package com.smashingmods.alchemistry.gametest;

import com.mojang.authlib.GameProfile;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.atomizer.AtomizerBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.network.jei.LiquifierTransferPacket;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

/**
 * In-world tests for the two fluid machines -- the liquifier (item in -> fluid out) and the atomizer
 * (fluid in -> item out). {@code MachineGameTests} drives the standalone item-only machines through the
 * dissolver, and {@code ReactorGameTests} covers the reactor multiblock; neither exercises the fluid I/O seam
 * these two share via {@link com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity}.
 * Every test here is {@code required=true} (the {@code @GameTest} default), so a failure fails the
 * {@code gameTestServer} gate alongside the full-chain load-smoke in {@link AlchemistryGameTests}.
 *
 * <p>Like the other holders this class lives in {@code src/main} so the mod scan registers it for the
 * {@code gameTestServer} run, but the {@code jar} task excludes the {@code gametest} package so it never ships.
 * Each test pins {@code template = "loadsemptytemplate"} (the staged 3x3x3 air structure) with
 * {@code @PrefixGameTestTemplate(false)} so the id resolves un-prefixed to {@code alchemistry:loadsemptytemplate};
 * a single machine block fits inside it. Bodies stay as thin plain helpers so the {@code @GameTest} methods stay
 * thin, matching the other holders.</p>
 *
 * <p>The two processing tests are data-driven against a loaded recipe rather than a hand-picked input: each
 * resolves any loaded recipe of its type, seeds that recipe's exact input, and asserts that recipe's exact output.
 * Each seeds the input at exactly one operation's worth so the machine settles after a single operation --
 * {@code canProcessRecipe()} then blocks a second because the input is fully consumed -- which makes the output an
 * exact equality (fluid type + amount, or item + count) rather than a {@code > 0} lower bound. The liquifier
 * transfer regression follows the same data-driven shape against the JEI transfer packet's handler.</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class FluidMachineGameTests {

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
     * <p>The timeout is raised above the {@code @GameTest} default: the liquifier's default operation length is 100
     * ticks ({@code Config.Common.liquifierTicksPerOperation}), which equals the standard 100-tick timeout, so the
     * fill lands on the operation's final tick -- on the edge of, or just past, the default window. A 200-tick
     * timeout restores headroom so the produced fluid is observed well before the test gives up, without making the
     * wait unbounded.</p>
     */
    @GameTest(template = "loadsemptytemplate", timeoutTicks = 200)
    @PrefixGameTestTemplate(false)
    public void liquifierProcessesItemToFluid(GameTestHelper helper) {
        LiquifierBlockEntity liquifier = placeLiquifier(helper);

        LiquifierRecipe recipe = RecipeRegistry.getLiquifierRecipe(r -> true, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no liquifier recipe loaded"));

        // One concrete seedable stack from the recipe's ingredient, already carried at the ingredient's required
        // count by toStacks(). An ingredient resolves to at least one item, so the list is non-empty; assert it
        // rather than risk an opaque index error, which would also mask a recipe that resolved to nothing.
        List<ItemStack> inputStacks = recipe.getInput().toStacks();
        helper.assertFalse(inputStacks.isEmpty(), "liquifier recipe input resolved to no item stacks");
        ItemStack input = inputStacks.get(0);

        // Plenty of energy for the whole operation; the fill capacity comfortably holds one operation's output.
        liquifier.getEnergyHandler().setEnergy(Integer.MAX_VALUE);
        liquifier.getInputHandler().setStackInSlot(0, input);

        // succeedWhen re-runs the criterion each tick until it passes or the test times out, so it naturally waits
        // for the operation to finish without manual tick driving.
        helper.succeedWhen(() -> {
            helper.assertFalse(liquifier.getFluidStorage().isEmpty(), "liquifier produced no fluid");
            helper.assertTrue(FluidStack.isSameFluidSameComponents(liquifier.getFluidStorage().getFluid(), recipe.getOutput()),
                    "liquifier output fluid is not the recipe's output fluid: expected " + recipe.getOutput().getFluid()
                            + ", found " + liquifier.getFluidStorage().getFluid().getFluid());
            helper.assertTrue(liquifier.getFluidStorage().getFluidAmount() == recipe.getOutput().getAmount(),
                    "liquifier output amount expected " + recipe.getOutput().getAmount()
                            + ", found " + liquifier.getFluidStorage().getFluidAmount());
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
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void atomizerProcessesFluidToItem(GameTestHelper helper) {
        AtomizerBlockEntity atomizer = placeAtomizer(helper);

        AtomizerRecipe recipe = RecipeRegistry.getAtomizerRecipe(r -> true, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no atomizer recipe loaded"));

        atomizer.getEnergyHandler().setEnergy(Integer.MAX_VALUE);

        // Seed exactly one operation's input fluid and confirm the tank accepted all of it, so the operation settles
        // after a single pass and the output equality stays exact.
        int filled = atomizer.getFluidStorage().fill(recipe.getInput().copy(), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(filled == recipe.getInput().getAmount(),
                "atomizer fluid seed under-filled: expected " + recipe.getInput().getAmount() + ", filled " + filled);

        helper.succeedWhen(() -> {
            ItemStack produced = atomizer.getOutputHandler().getStackInSlot(0);
            helper.assertFalse(produced.isEmpty(), "atomizer produced no output item");
            helper.assertTrue(ItemStack.isSameItemSameComponents(produced, recipe.getOutput()),
                    "atomizer output item is not the recipe's output: expected " + recipe.getOutput().getItem()
                            + ", found " + produced.getItem());
            helper.assertTrue(produced.getCount() == recipe.getOutput().getCount(),
                    "atomizer output count expected " + recipe.getOutput().getCount() + ", found " + produced.getCount());
        });
    }

    /**
     * Regression for the dead liquifier "+" button: drives {@link LiquifierTransferPacket}'s server handler against
     * a liquifier whose tank already holds the recipe's own output fluid -- the state every liquifier reaches after
     * its first operation. The pre-fix {@code canTransfer} required the tank to be EMPTY outright, and unlike the
     * item machines (whose output slots the handler first empties into the player inventory) a tank cannot be
     * emptied that way, so that first operation's fluid permanently vetoed every later JEI transfer in both game
     * modes -- the machine-input-stays-empty failure this test turns into assertions. Post-fix the gate mirrors
     * {@code canProcessRecipe}'s fluid acceptance (empty or the recipe's own output), so the transfer must debit
     * the inventory and fill the machine input while the tank keeps its fluid.
     *
     * <p>The handler resolves the recipe by matching every item of a candidate's ingredient against the packet's
     * ingredient, so the seeded recipe is re-resolved through that same predicate to pin exactly the recipe the
     * packet acts on. {@code maxTransfer=false} pins one operation, making the debit exact.</p>
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void liquifierTransferWithProducedFluidStillTransfers(GameTestHelper helper) {
        LiquifierBlockEntity liquifier = placeLiquifier(helper);

        LiquifierRecipe picked = RecipeRegistry.getLiquifierRecipe(r -> true, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no liquifier recipe loaded"));
        // Re-resolve the way the handler does -- first recipe whose resolved items all match the packet ingredient.
        LiquifierRecipe recipe = RecipeRegistry.getLiquifierRecipe(r -> {
                    Ingredient recipeIngredient = r.getInput().getIngredient();
                    return recipeIngredient.items().findAny().isPresent()
                            && recipeIngredient.items().map(holder -> new ItemStack(holder.value())).allMatch(picked.getInput().getIngredient());
                }, helper.getLevel())
                .orElseThrow(() -> new AssertionError("liquifier recipe lookup by input found nothing"));

        // The tank state after one completed operation: exactly the recipe's output fluid.
        int filled = liquifier.getFluidStorage().fill(recipe.getOutput().copy(), IFluidHandler.FluidAction.EXECUTE);
        helper.assertTrue(filled == recipe.getOutput().getAmount(),
                "tank seed under-filled: expected " + recipe.getOutput().getAmount() + ", filled " + filled);

        List<ItemStack> inputStacks = recipe.getInput().toStacks();
        helper.assertFalse(inputStacks.isEmpty(), "liquifier recipe input resolved to no item stacks");
        Item inputItem = inputStacks.get(0).getItem();
        int inputCount = recipe.getInput().getCount();

        ServerPlayer player = makeServerPlayer(helper);
        Inventory inventory = player.getInventory();
        int seeded = inputCount + 3;
        inventory.add(new ItemStack(inputItem, seeded));
        int slot = inventory.findSlotMatchingItem(new ItemStack(inputItem));

        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new LiquifierTransferPacket(liquifier.getBlockPos(), recipe.getInput(), false).handle(context);

        ItemStack machineInput = liquifier.getInputHandler().getStackInSlot(0);
        helper.assertTrue(machineInput.is(inputItem) && machineInput.getCount() == inputCount,
                "transfer must fill the machine input despite the produced fluid (dead-button regression): expected "
                        + inputItem + " x" + inputCount + ", found " + machineInput);
        helper.assertTrue(inventory.getItem(slot).getCount() == seeded - inputCount,
                "inventory debit expected " + (seeded - inputCount) + ", found " + inventory.getItem(slot).getCount());
        helper.assertTrue(liquifier.getFluidStorage().getFluidAmount() == recipe.getOutput().getAmount(),
                "the tank's produced fluid must stay untouched, found " + liquifier.getFluidStorage().getFluidAmount());
        helper.succeed();
    }

    // Places a liquifier at the structure centre and returns its block-entity, failing the test if either the block
    // or its block-entity is missing. Block-entity positions are structure-relative; getBlockEntity converts to the
    // absolute world position for us.
    private static LiquifierBlockEntity placeLiquifier(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.LIQUIFIER.get());
        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof LiquifierBlockEntity liquifier)) {
            helper.fail("expected a LiquifierBlockEntity at " + MACHINE_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return liquifier;
    }

    // A real non-creative ServerPlayer on the gametest level, mirroring MachineGameTests: the transfer handlers
    // reject a non-ServerPlayer and route a creative player to a branch that never touches the inventory, so this
    // builds a ServerPlayer directly whose default SURVIVAL game mode is the non-creative path. It is not placed
    // onto the network -- the handler runs synchronously on the server thread a gametest already executes on.
    private static ServerPlayer makeServerPlayer(GameTestHelper helper) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "test-transfer-player");
        return new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(), profile,
                CommonListenerCookie.createInitial(profile, false).clientInformation());
    }

    // Places an atomizer at the structure centre and returns its block-entity, failing the test if either the block
    // or its block-entity is missing. Block-entity positions are structure-relative; getBlockEntity converts to the
    // absolute world position for us.
    private static AtomizerBlockEntity placeAtomizer(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.ATOMIZER.get());
        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof AtomizerBlockEntity atomizer)) {
            helper.fail("expected an AtomizerBlockEntity at " + MACHINE_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return atomizer;
    }
}
