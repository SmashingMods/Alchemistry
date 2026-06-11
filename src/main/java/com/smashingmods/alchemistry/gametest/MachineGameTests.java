package com.smashingmods.alchemistry.gametest;

import com.mojang.authlib.GameProfile;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.network.jei.CompactorTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FissionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FusionTransferPacket;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.player.Inventory;
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
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    // Extra count seeded above each fusion input's per-operation debit, so the post-transfer slot count is an exact
    // expectation rather than an emptied slot. Kept small so both inputs still fit in a single inventory slot.
    private static final int SLACK = 3;

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

    /**
     * Drives {@link FusionTransferPacket}'s server handler down its non-creative branch and asserts it debits the
     * two distinct recipe inputs from the right inventory slots while filling both machine input slots. The handler
     * resolves a fusion recipe, matches both inputs jointly against the main inventory, and removes each input's
     * count by the slot its claim carried -- so the two debits must hit the two distinct slots, not the same slot
     * twice. With one input removed from {@code slot1} and the other from {@code slot2}, both inventory stacks
     * shrink by their recipe count; debiting {@code slot1} for both inputs instead would over-drain the first input
     * and leave the second untouched, which the per-slot count assertions pin.
     *
     * <p>The branch needs a non-creative {@link ServerPlayer} whose inventory holds both inputs: the mock player from
     * {@link GameTestHelper#makeMockPlayer(GameType)} is a plain {@code Player}, which the handler's
     * {@code instanceof ServerPlayer} check rejects, and {@link GameTestHelper#makeMockServerPlayerInLevel()} forces
     * creative (overriding {@code isCreative()} to {@code true}), which routes to the no-inventory-debit creative
     * branch. So a real {@code ServerPlayer} is built directly on the gametest level -- not placed onto the network --
     * and its default {@code SURVIVAL} game mode is the non-creative path. {@code maxTransfer=false} pins one
     * operation, so each debit equals exactly the recipe input's count.</p>
     *
     * <p>A bare controller suffices: the handler resolves the block-entity by position and mutates its handlers
     * directly, independent of the reactor multiblock being formed. The recipe is whichever loaded fusion recipe has
     * two distinct inputs, so the two inputs occupy two distinct inventory slots -- the precondition that makes the
     * single-slot bug observable.</p>
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void fusionTransferDebitsBothInputs(GameTestHelper helper) {
        FusionControllerBlockEntity controller = placeFusionController(helper);

        // A loaded fusion recipe whose two inputs differ, so input1 and input2 land in distinct inventory slots --
        // the only arrangement under which debiting the same slot twice is distinguishable from debiting each once.
        FusionRecipe recipe = RecipeRegistry.getFusionRecipe(
                        r -> !ItemStack.isSameItemSameComponents(r.getInput1(), r.getInput2()), helper.getLevel())
                .orElseThrow(() -> new AssertionError("no fusion recipe with two distinct inputs loaded"));

        ItemStack input1 = recipe.getInput1();
        ItemStack input2 = recipe.getInput2();

        // Seed each input with a count safely above its single-operation debit, so the post-handle count is an exact
        // expectation rather than an emptied slot whose debit could not be measured.
        int seeded1 = input1.getCount() + SLACK;
        int seeded2 = input2.getCount() + SLACK;

        ServerPlayer player = makeServerPlayer(helper);
        Inventory inventory = player.getInventory();
        inventory.add(new ItemStack(input1.getItem(), seeded1));
        inventory.add(new ItemStack(input2.getItem(), seeded2));

        int slot1 = inventory.findSlotMatchingItem(input1);
        int slot2 = inventory.findSlotMatchingItem(input2);
        helper.assertTrue(slot1 != slot2,
                "the two distinct inputs must occupy distinct inventory slots, both resolved to " + slot1);

        // Drive the production receive path: build the packet JEI would send and invoke its handler with a real
        // server-bound context carrying the non-creative player. maxTransfer=false pins exactly one operation.
        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new FusionTransferPacket(controller.getBlockPos(), input1, input2, false).handle(context);

        // Both machine input slots filled with the recipe inputs at one operation's count.
        ProcessingSlotHandler machineInputs = controller.getInputHandler();
        ItemStack machineSlot0 = machineInputs.getStackInSlot(0);
        ItemStack machineSlot1 = machineInputs.getStackInSlot(1);
        helper.assertTrue(ItemStack.isSameItemSameComponents(machineSlot0, input1) && machineSlot0.getCount() == input1.getCount(),
                "machine input slot 0 expected " + input1.getItem() + " x" + input1.getCount() + ", found " + machineSlot0);
        helper.assertTrue(ItemStack.isSameItemSameComponents(machineSlot1, input2) && machineSlot1.getCount() == input2.getCount(),
                "machine input slot 1 expected " + input2.getItem() + " x" + input2.getCount() + ", found " + machineSlot1);

        // Each inventory slot debited by exactly its own input's count -- input1's slot by input1's count, input2's
        // slot by input2's count. Debiting slot1 twice would leave slot2 at its seed and over-drain slot1.
        int remaining1 = inventory.getItem(slot1).getCount();
        int remaining2 = inventory.getItem(slot2).getCount();
        helper.assertTrue(remaining1 == seeded1 - input1.getCount(),
                "input1 slot debited wrong: expected " + (seeded1 - input1.getCount()) + ", found " + remaining1);
        helper.assertTrue(remaining2 == seeded2 - input2.getCount(),
                "input2 slot was not debited (single-slot bug): expected " + (seeded2 - input2.getCount()) + ", found " + remaining2);
        helper.succeed();
    }

    /**
     * Regression for the same-element max-transfer dupe: drives {@link FusionTransferPacket} for a recipe fusing
     * an element with itself (hydrogen + hydrogen -> helium), seeded as ONE inventory stack, and asserts the
     * inventory debit equals the machine placement. Pre-fix the two inputs were matched independently:
     * {@code findSlotMatchingItem} resolved the same stack twice, max transfer authorized stackCount operations
     * per input, the second {@code removeItem} came back empty, and the placement still inserted both shares --
     * duplicating the whole stack out of one debit. The joint claim bounds operations by the TOTAL per-operation
     * claim on the shared slot (seeded / (count1 + count2)), so what is placed is exactly what was removed; the
     * conservation assertion fails against the old math, which placed twice the seed.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void fusionTransferSameElementConservesItems(GameTestHelper helper) {
        FusionControllerBlockEntity controller = placeFusionController(helper);

        FusionRecipe recipe = RecipeRegistry.getFusionRecipe(
                        r -> ItemStack.isSameItemSameComponents(r.getInput1(), r.getInput2()), helper.getLevel())
                .orElseThrow(() -> new AssertionError("no same-element fusion recipe loaded"));

        ItemStack input1 = recipe.getInput1();
        ItemStack input2 = recipe.getInput2();
        int perOperation = input1.getCount() + input2.getCount();

        // One stack covering two whole operations plus a remainder, so the expected operation count and the
        // leftover are both non-trivial: max transfer must stop at seeded / perOperation, not seeded per input.
        int seeded = 2 * perOperation + 1;

        ServerPlayer player = makeServerPlayer(helper);
        Inventory inventory = player.getInventory();
        inventory.add(new ItemStack(input1.getItem(), seeded));
        int slot = inventory.findSlotMatchingItem(input1);

        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new FusionTransferPacket(controller.getBlockPos(), input1, input2, true).handle(context);

        int expectedOperations = seeded / perOperation;
        ProcessingSlotHandler machineInputs = controller.getInputHandler();
        ItemStack machineSlot0 = machineInputs.getStackInSlot(0);
        ItemStack machineSlot1 = machineInputs.getStackInSlot(1);
        helper.assertTrue(machineSlot0.getCount() == input1.getCount() * expectedOperations
                        && machineSlot1.getCount() == input2.getCount() * expectedOperations,
                "expected " + expectedOperations + " operations' worth in the machine, found "
                        + machineSlot0 + " and " + machineSlot1);

        int placed = machineSlot0.getCount() + machineSlot1.getCount();
        int remaining = inventory.getItem(slot).getCount();
        helper.assertTrue(remaining + placed == seeded,
                "items not conserved (same-element dupe): seeded " + seeded + ", inventory " + remaining
                        + " + machine " + placed);
        helper.succeed();
    }

    /**
     * The short-seed half of the same-element regression: with the inventory holding only ONE input's worth of
     * the shared element (a 1-count stack for the shipped count-1 recipes), the joint match leaves the second
     * claim EMPTY, the full-match gate fails, and the handler must not transfer at all. Pre-fix the count-blind
     * {@code Inventory#contains} gate passed for both inputs, the single item was removed, the second removal
     * came back empty, and the machine was still filled with both inputs -- items created from a seed that could
     * not fund one operation, which the machine-stays-empty assertion pins.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void fusionTransferSameElementShortSeedDoesNotTransfer(GameTestHelper helper) {
        FusionControllerBlockEntity controller = placeFusionController(helper);

        FusionRecipe recipe = RecipeRegistry.getFusionRecipe(
                        r -> ItemStack.isSameItemSameComponents(r.getInput1(), r.getInput2()), helper.getLevel())
                .orElseThrow(() -> new AssertionError("no same-element fusion recipe loaded"));

        ItemStack input1 = recipe.getInput1();
        ItemStack input2 = recipe.getInput2();

        // Only the first claim's worth: the second claim must come up EMPTY and fail the full-match gate.
        int seeded = input1.getCount();

        ServerPlayer player = makeServerPlayer(helper);
        Inventory inventory = player.getInventory();
        inventory.add(new ItemStack(input1.getItem(), seeded));
        int slot = inventory.findSlotMatchingItem(input1);

        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new FusionTransferPacket(controller.getBlockPos(), input1, input2, false).handle(context);

        ProcessingSlotHandler machineInputs = controller.getInputHandler();
        helper.assertTrue(machineInputs.getStackInSlot(0).isEmpty() && machineInputs.getStackInSlot(1).isEmpty(),
                "machine must stay empty on an insufficient seed, found "
                        + machineInputs.getStackInSlot(0) + " and " + machineInputs.getStackInSlot(1));
        helper.assertTrue(inventory.getItem(slot).getCount() == seeded,
                "inventory must stay untouched on an insufficient seed, found " + inventory.getItem(slot));
        helper.succeed();
    }

    /**
     * Regression for the compactor partial-stack dupe: the recipe input sits split across a small stack at a low
     * slot and a full stack at a higher slot. The count-aware gate matched the full stack, but the pre-fix
     * removal re-looked the slot up with the count-blind {@code Inventory#findSlotMatchingItem}, hit the small
     * stack first, drained it short of the recipe count, and the placement still inserted the full count --
     * creating the shortfall, which the conservation assertion pins. Post-fix the removal debits the very slot
     * the joint claim carried, so the small stack stays untouched and the matched stack funds the whole debit.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void compactorTransferPartialStackConservesItems(GameTestHelper helper) {
        CompactorBlockEntity compactor = placeCompactor(helper);

        // A recipe taking more than one input item, so a stack one short of the recipe count is a genuine
        // partial stack. The handler resolves recipes by output (first match wins), so re-resolve the picked
        // recipe the same way to pin exactly the recipe the packet will act on.
        CompactorRecipe picked = RecipeRegistry.getCompactorRecipe(
                        r -> r.getInput().getCount() >= 2 && r.getInput().getIngredient().items().findAny().isPresent(), helper.getLevel())
                .orElseThrow(() -> new AssertionError("no compactor recipe with input count >= 2 loaded"));
        CompactorRecipe recipe = RecipeRegistry.getCompactorRecipe(
                        r -> ItemStack.isSameItemSameComponents(r.getOutput(), picked.getOutput()), helper.getLevel())
                .orElseThrow(() -> new AssertionError("compactor recipe lookup by output found nothing"));
        int inputCount = recipe.getInput().getCount();
        helper.assertTrue(inputCount >= 2,
                "the output-resolved recipe must keep an input of count >= 2, got count " + inputCount);

        Item inputItem = recipe.getInput().getIngredient().items().findFirst().map(Holder::value)
                .orElseThrow(() -> new AssertionError("compactor recipe input resolved to no items"));
        int small = inputCount - 1;
        int large = inputCount + SLACK;

        ServerPlayer player = makeServerPlayer(helper);
        Inventory inventory = player.getInventory();
        // Set the slots directly -- Inventory#add would merge the two same-item stacks into one slot. The small
        // stack sits at the lower index so a count-blind first-match lookup would hit it first.
        inventory.setItem(0, new ItemStack(inputItem, small));
        inventory.setItem(9, new ItemStack(inputItem, large));

        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new CompactorTransferPacket(compactor.getBlockPos(), recipe.getOutput(), false).handle(context);

        ItemStack machineInput = compactor.getInputHandler().getStackInSlot(0);
        helper.assertTrue(machineInput.is(inputItem) && machineInput.getCount() == inputCount,
                "machine input expected " + inputItem + " x" + inputCount + ", found " + machineInput);
        helper.assertTrue(inventory.getItem(0).getCount() == small,
                "the small partial stack must stay untouched, found " + inventory.getItem(0));
        helper.assertTrue(inventory.getItem(9).getCount() == large - inputCount,
                "the matched full stack must fund the whole debit: expected " + (large - inputCount)
                        + ", found " + inventory.getItem(9));

        int totalAfter = inventory.getItem(0).getCount() + inventory.getItem(9).getCount() + machineInput.getCount();
        helper.assertTrue(totalAfter == small + large,
                "items not conserved (partial-stack dupe): seeded " + (small + large) + ", found " + totalAfter);
        helper.succeed();
    }

    /**
     * Regression for the offhand-only disconnect: an input held ONLY in the offhand passed the pre-fix
     * {@code Inventory#contains} gate (it scans every compartment), but the main-inventory-only slot lookup then
     * returned -1 and {@code getItem(-1)} threw out of the network handler, disconnecting the player -- here the
     * exception would propagate out of {@code handle} and fail the test. The joint matcher only ever walks the
     * main inventory, so the offhand-only case now simply fails the full-match gate: no exception, no transfer,
     * offhand untouched. Driven against fission, whose single input keeps the setup minimal.
     */
    @GameTest(template = "loadsemptytemplate")
    @PrefixGameTestTemplate(false)
    public void fissionTransferOffhandOnlyDoesNotCrash(GameTestHelper helper) {
        FissionControllerBlockEntity controller = placeFissionController(helper);

        FissionRecipe recipe = RecipeRegistry.getFissionRecipe(r -> true, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no fission recipe loaded"));
        ItemStack input = recipe.getInput();

        ServerPlayer player = makeServerPlayer(helper);
        Inventory inventory = player.getInventory();
        // Seed ONLY the offhand compartment; the main inventory stays empty.
        inventory.offhand.set(0, new ItemStack(input.getItem(), input.getCount()));

        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new FissionTransferPacket(controller.getBlockPos(), input, true).handle(context);

        helper.assertTrue(controller.getInputHandler().isEmpty(),
                "machine must stay empty when the input is only in the offhand, found "
                        + controller.getInputHandler().getStackInSlot(0));
        helper.assertTrue(inventory.offhand.get(0).getCount() == input.getCount(),
                "offhand stack must stay untouched, found " + inventory.offhand.get(0));
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

    // Places a fusion controller at the structure centre and returns its block-entity, failing the test if either the
    // block or its block-entity is missing. The transfer handler addresses it by position and never needs the reactor
    // multiblock formed, so a bare controller is enough.
    private static FusionControllerBlockEntity placeFusionController(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.FUSION_CONTROLLER.get());
        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof FusionControllerBlockEntity controller)) {
            helper.fail("expected a FusionControllerBlockEntity at " + MACHINE_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return controller;
    }

    // Places a compactor at the structure centre and returns its block-entity, failing the test if either the
    // block or its block-entity is missing.
    private static CompactorBlockEntity placeCompactor(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.COMPACTOR.get());
        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof CompactorBlockEntity compactor)) {
            helper.fail("expected a CompactorBlockEntity at " + MACHINE_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return compactor;
    }

    // Places a fission controller at the structure centre and returns its block-entity, failing the test if either
    // the block or its block-entity is missing. Like the fusion controller, a bare controller is enough for the
    // transfer handler, which addresses it by position without the multiblock formed.
    private static FissionControllerBlockEntity placeFissionController(GameTestHelper helper) {
        helper.setBlock(MACHINE_POS, BlockRegistry.FISSION_CONTROLLER.get());
        BlockEntity blockEntity = helper.getBlockEntity(MACHINE_POS);
        if (!(blockEntity instanceof FissionControllerBlockEntity controller)) {
            helper.fail("expected a FissionControllerBlockEntity at " + MACHINE_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), MACHINE_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return controller;
    }

    // A real non-creative ServerPlayer on the gametest level. The transfer handlers reject a non-ServerPlayer and
    // route a creative player to a branch that never touches the inventory, so neither the helper's plain-Player mock
    // nor its deprecated mock server-player (which forces creative) fits; this builds a ServerPlayer directly whose
    // default SURVIVAL game mode is the non-creative path. It is not placed onto the network -- the handler runs
    // synchronously on the server thread a gametest already executes on, so no connection is needed.
    private static ServerPlayer makeServerPlayer(GameTestHelper helper) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "test-transfer-player");
        return new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(), profile,
                CommonListenerCookie.createInitial(profile, false).clientInformation());
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
