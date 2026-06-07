package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorEnergyBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorShape;
import com.smashingmods.alchemistry.common.block.reactor.ReactorType;
import com.smashingmods.alchemistry.common.network.ToggleReactorAutoejectPacket;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-world test for the fission reactor multiblock -- the reactor half of P4.alchemistry.5 ({@code MachineGameTests}
 * is the machine half). Like the other holders this class lives in {@code src/main} so the mod scan registers it for
 * the {@code gameTestServer} run, but the {@code jar}/{@code sourcesJar} tasks exclude the {@code gametest} package so
 * it never ships. The single test is {@code required=false} so it can never gate the {@code gameTestServer} exit code;
 * the lone {@code required=true} gate stays the full-chain load-smoke in {@link AlchemistryGameTests}.
 *
 * <p>Unlike the dissolver tests this one needs room for a 5x5x5 reactor shell, so it pins {@code template =
 * "reactor_space"} -- a staged 9x9x9 all-air structure -- rather than the 3x3x3 {@code loadsemptytemplate}. The
 * structure is sized so the whole shell plus the controller fit with margin to spare. Bodies stay as thin plain
 * helpers, matching the other holders (forward-compat for the Phase-10 1.21.5 gametest rewrite).</p>
 */
@GameTestHolder(Alchemistry.MODID)
public class ReactorGameTests {

    // Reactor facing. ReactorShape builds the shell from the controller position via Direction.relative() math, and the
    // controller's own tick() rebuilds its shape by reading HORIZONTAL_FACING off the placed block -- so the block we
    // place and the ReactorShape we build to fill the world must agree on this one direction or the structure and the
    // controller's validation would disagree.
    private static final Direction FACING = Direction.NORTH;

    // Controller position, structure-relative. The shell is the controller's front-face centre extended UP3/DOWN1 and
    // 2 to each side and 4 to the rear, i.e. X[2,6] Y[3,7] Z[4,8] for a NORTH-facing controller at (4,4,4) -- entirely
    // inside the 9x9x9 reactor_space structure.
    private static final BlockPos CONTROLLER_POS = new BlockPos(4, 4, 4);

    /**
     * Builds a valid fission reactor around a placed controller, then asserts the multiblock validates and the energy
     * port proxies the ENERGY capability to the controller.
     *
     * <p>The shell is filled straight from {@link ReactorShape#createShapeMap()}: every region maps to its list of
     * allowed blocks, and the first entry of each list ({@code REACTOR_CASING}/{@code FISSION_CORE}) is valid for that
     * whole region, so filling every mapped position with {@code allowed.get(0)} -- skipping the controller cell, which
     * is itself an allowed block of the inner front plane -- produces a structure that passes {@code
     * validateMultiblockShape}. Three border cells are then swapped for the {@code REACTOR_ENERGY}/{@code
     * REACTOR_INPUT}/{@code REACTOR_OUTPUT} block-entities; all three are in every border region's allowed list, so the
     * shape stays valid while {@link AbstractReactorBlockEntity#setMultiblockHandlers()} (driven by the controller's
     * server ticker each tick) discovers them and sets {@code energyFound/inputFound/outputFound}.</p>
     *
     * <p>The capability check mirrors {@code MachineGameTests#machineCapability}: NeoForge 20.4 uses the
     * object-capability system, so the energy port's capability is queried off the level with
     * {@code level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side)}. Once the controller adopts the energy
     * port its registered resolver proxies to the controller's own {@code getEnergyHandler()} -- the proxy target --
     * so the queried instance must be identical to it.</p>
     */
    @GameTest(required = false, template = "reactor_space")
    @PrefixGameTestTemplate(false)
    public void reactorFormsAndProxiesEnergyCap(GameTestHelper helper) {
        FissionControllerBlockEntity controller = placeController(helper);
        List<BlockPos> ports = buildShellAndPlacePorts(helper);
        BlockPos energyPos = ports.get(0);

        // The controller's server ticker runs tick() every tick, which lazily builds its ReactorShape (reading the
        // placed block's facing) then calls setMultiblockHandlers() (adopting the ports) and isValidMultiblock();
        // succeedWhen re-runs the criterion each tick until it returns without throwing -- i.e. until the multiblock
        // validates and the energy port's capability resolves to the controller's handler -- then marks the test
        // succeeded. We gate on the shape being built first because isValidMultiblock() dereferences it unguarded --
        // on the very first tick the criterion can run before the block-entity ticker has, so the shape may briefly
        // be null.
        helper.succeedWhen(() -> {
            helper.assertTrue(controller.getReactorShape() != null, "controller has not built its reactor shape yet");
            helper.assertTrue(controller.isValidMultiblock(), "fission reactor multiblock did not validate");

            ReactorEnergyBlockEntity energy = energyBlockEntity(helper, energyPos);
            BlockPos energyWorldPos = energy.getBlockPos();
            IEnergyStorage proxied = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, energyWorldPos, null);
            helper.assertTrue(proxied != null, "reactor energy port did not expose an ENERGY capability");
            helper.assertTrue(proxied == controller.getEnergyHandler(),
                    "reactor energy port capability is not proxied to the controller's energy handler");
        });
    }

    /**
     * The send-&gt;handle runtime proof for the networking rewrite (audit B1). The encode/decode unit round-trips prove
     * the wire format survives a trip, and the full-chain boot smoke proves the packets register; nothing automated
     * proved that a server-bound packet's {@code handle} body runs on the server and mutates the right block-entity.
     * This drives {@link ToggleReactorAutoejectPacket} through its production receive path and asserts the reactor
     * controller's {@code autoeject} flag flips.
     *
     * <p><b>Context approach (b) of the ticket:</b> the packet is constructed and its production
     * {@link ToggleReactorAutoejectPacket#handle(PlayPayloadContext) handle(PlayPayloadContext)} is invoked directly
     * with a minimal-but-real {@link PlayPayloadContext} record. The handler reads only {@code player()} (then
     * {@code player.level().getBlockEntity(pos)}), so the context carries {@link PacketFlow#SERVERBOUND} -- the real
     * server-bound flow -- and {@code Optional.of(}a {@link GameTestHelper#makeMockPlayer() mock player}{@code )},
     * whose {@code level()} is the gametest {@link net.minecraft.server.level.ServerLevel}; the four unused handlers
     * (reply/packet/work/channel) are {@code null}. This is the production handler logic, executed server-side against
     * the real block-entity, so it proves the handler body, its block-entity effect, and the side. <b>Limitation:</b>
     * it does not exercise the registrar's decoder-and-side binding -- that the id is wired server-bound to this
     * handler with this decoder -- which is covered by the boot smoke ({@code fullChainLoaded}) plus the encode/decode
     * round-trip unit tests; nor the network-thread-to-main-thread {@code workHandler().execute(...)} hop, which
     * {@link com.smashingmods.alchemylib.api.network.AbstractPacketHandler} performs and which a gametest already runs
     * on the server main thread.</p>
     *
     * <p>A bare controller suffices: its {@code autoeject} field defaults {@code false}, and the handler's
     * {@code tryEjectOutputs()} call (only on a {@code true} toggle) early-returns while no output port is adopted, so
     * the assertable effect is purely the flag flip. The test captures the initial value, sends the opposite, ticks
     * once, and asserts the controller's {@link AbstractReactorBlockEntity#isAutoEject()} changed to match.</p>
     */
    @GameTest(required = false, template = "reactor_space")
    @PrefixGameTestTemplate(false)
    public void autoejectPacketTogglesReactor(GameTestHelper helper) {
        FissionControllerBlockEntity controller = placeController(helper);

        // Capture the starting flag and target its opposite so the assertion is a genuine change, not a coincidental
        // match with the field's default.
        boolean initial = controller.isAutoEject();
        boolean target = !initial;

        // The controller's absolute world position -- the handler resolves the block-entity by this exact key off the
        // player's level, so it must be the absolute position, not the structure-relative CONTROLLER_POS.
        BlockPos controllerWorldPos = controller.getBlockPos();

        // Drive the production receive path: build the packet the client would send, then invoke its handler with a
        // real server-bound context whose player lives in the gametest level. See the class/method docs for why the
        // four unused context handlers are null.
        Player player = helper.makeMockPlayer();
        PlayPayloadContext context = new PlayPayloadContext(
                null, null, null, PacketFlow.SERVERBOUND, null, Optional.of(player));
        new ToggleReactorAutoejectPacket(controllerWorldPos, target).handle(context);

        // One tick to settle, then assert the handler applied the flip to the block-entity it was addressed to.
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(controller.isAutoEject() == target,
                    "ToggleReactorAutoejectPacket handler did not flip autoeject: expected " + target
                            + ", found " + controller.isAutoEject());
            helper.succeed();
        });
    }

    // Places the fission controller at CONTROLLER_POS with a fixed horizontal facing and returns its block-entity,
    // failing the test if either the block or its block-entity is missing. The FissionControllerBlockEntity constructor
    // sets reactorType=FISSION, so no manual setReactorType is needed; we assert it as a guard against that changing.
    private static FissionControllerBlockEntity placeController(GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, BlockRegistry.FISSION_CONTROLLER.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, FACING));
        BlockEntity blockEntity = helper.getBlockEntity(CONTROLLER_POS);
        if (!(blockEntity instanceof FissionControllerBlockEntity controller)) {
            helper.fail("expected a FissionControllerBlockEntity at " + CONTROLLER_POS + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), CONTROLLER_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        helper.assertTrue(controller.getReactorType() == ReactorType.FISSION,
                "fission controller reactorType was " + controller.getReactorType() + ", expected FISSION");
        return controller;
    }

    // Fills the reactor shell from the shape map and swaps three border cells for the energy/input/output ports.
    // Returns the three port positions (structure-relative) in [energy, input, output] order; the caller needs the
    // energy port to check its capability proxy.
    //
    // ReactorShape does pure Direction.relative() math from whatever position it is handed, so we build it from the
    // structure-relative CONTROLLER_POS and place every region cell at its structure-relative position directly. This
    // works because in this gametest absolutePos() is a pure translation (no rotation): the controller's own tick()
    // builds its shape from the controller's ABSOLUTE position with world facing NORTH, and translation commutes with
    // relative(), so our relative-space shell maps cell-for-cell onto the absolute shell the controller validates
    // against. We deliberately avoid relativePos(): on NeoForge 20.2.93 it does not round-trip absolutePos() (it
    // mishandles the X/Z axes), and an earlier relativePos()-based fill scattered the shell so it never validated.
    private static List<BlockPos> buildShellAndPlacePorts(GameTestHelper helper) {
        ReactorShape shape = new ReactorShape(CONTROLLER_POS, ReactorType.FISSION, FACING);
        Map<BoundingBox, List<Block>> shapeMap = shape.createShapeMap();

        // Fill every mapped region with its first allowed block (casing for the borders/inner planes, the core block
        // for the core), skipping the controller cell so we do not overwrite it -- the controller is itself an allowed
        // block of the inner front plane, so leaving it in place keeps that region valid.
        Block energyPort = BlockRegistry.REACTOR_ENERGY.get();
        List<BlockPos> borderCells = new ArrayList<>();
        shapeMap.forEach((box, allowed) -> BlockPos.betweenClosedStream(box).forEach(pos -> {
            BlockPos relativePos = pos.immutable();
            if (!relativePos.equals(CONTROLLER_POS)) {
                helper.setBlock(relativePos, allowed.get(0));
            }
            // Border regions are exactly those that admit the energy port; collect their cells as candidate port slots.
            if (allowed.contains(energyPort) && !relativePos.equals(CONTROLLER_POS)) {
                borderCells.add(relativePos);
            }
        }));

        // Pick three distinct border cells deterministically (HashMap iteration order is unspecified) so the chosen
        // port positions are stable from run to run.
        List<BlockPos> sortedBorder = borderCells.stream()
                .distinct()
                .sorted(Comparator.<BlockPos>comparingInt(BlockPos::getX)
                        .thenComparingInt(BlockPos::getY)
                        .thenComparingInt(BlockPos::getZ))
                .toList();
        helper.assertTrue(sortedBorder.size() >= 3,
                "expected at least 3 border cells for the reactor ports, found " + sortedBorder.size());

        BlockPos energyPos = sortedBorder.get(0);
        BlockPos inputPos = sortedBorder.get(1);
        BlockPos outputPos = sortedBorder.get(2);
        helper.setBlock(energyPos, BlockRegistry.REACTOR_ENERGY.get());
        helper.setBlock(inputPos, BlockRegistry.REACTOR_INPUT.get());
        helper.setBlock(outputPos, BlockRegistry.REACTOR_OUTPUT.get());

        return List.of(energyPos, inputPos, outputPos);
    }

    // The energy port block-entity at the given structure-relative position, failing the test if it is missing.
    private static ReactorEnergyBlockEntity energyBlockEntity(GameTestHelper helper, BlockPos pos) {
        BlockEntity blockEntity = helper.getBlockEntity(pos);
        if (!(blockEntity instanceof ReactorEnergyBlockEntity energy)) {
            helper.fail("expected a ReactorEnergyBlockEntity at " + pos + ", got "
                    + (blockEntity == null ? "null" : blockEntity.getClass().getSimpleName()), pos);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return energy;
    }
}
