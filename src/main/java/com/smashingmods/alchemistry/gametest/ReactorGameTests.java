package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorEnergyBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorShape;
import com.smashingmods.alchemistry.common.block.reactor.ReactorType;
import com.smashingmods.alchemistry.common.network.ToggleReactorAutoejectPacket;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * In-world test for the fission reactor multiblock ({@code MachineGameTests} covers the standalone machines). Like
 * the other test classes the bodies stay here as {@code static} methods and the registration lives in
 * {@link AlchemistryGameTestRegistry}; the package is compiled into {@code src/main} so the mod scan discovers it,
 * but the {@code jar}/{@code sourcesJar} tasks exclude it so it never ships. Every test is registered as required,
 * so a failure fails the {@code gameTestServer} gate alongside the full-chain load-smoke in
 * {@link AlchemistryGameTests}.
 *
 * <p>Unlike the dissolver tests these need room for a 5x5x5 reactor shell, so they run against the staged 9x9x9
 * all-air structure ({@code alchemistry:reactor_space}) rather than the 3x3x3 {@code loadsemptytemplate}. The
 * structure is sized so the whole shell plus the controller fit with margin to spare.</p>
 */
public class ReactorGameTests {

    private ReactorGameTests() {}

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
    public static void reactorFormsAndProxiesEnergyCap(GameTestHelper helper) {
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
            helper.assertTrue(controller.getReactorShape() != null, Component.literal("controller has not built its reactor shape yet"));
            helper.assertTrue(controller.isValidMultiblock(), Component.literal("fission reactor multiblock did not validate"));

            ReactorEnergyBlockEntity energy = energyBlockEntity(helper, energyPos);
            BlockPos energyWorldPos = energy.getBlockPos();
            IEnergyStorage proxied = helper.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, energyWorldPos, null);
            helper.assertTrue(proxied != null, Component.literal("reactor energy port did not expose an ENERGY capability"));
            helper.assertTrue(proxied == controller.getEnergyHandler(),
                    Component.literal("reactor energy port capability is not proxied to the controller's energy handler"));
        });
    }

    /**
     * Regression test for the capability-cache reconnect across multiblock formation. An adjacent block (e.g. a pipe)
     * that queries a reactor port's capability through a {@link BlockCapabilityCache} before the controller adopts the
     * port caches the resolver's pre-adoption {@code null} (the port's resolver returns {@code null} until its
     * controller is set). A {@code BlockCapabilityCache} only re-resolves after the level fires an invalidation for the
     * position, so unless formation invalidates the port's capabilities the cache stays stuck on that {@code null} and
     * the neighbour never reconnects. {@link AbstractReactorBlockEntity#setMultiblockHandlers()} calls
     * {@code level.invalidateCapabilities(portPos)} on adoption for exactly this reason; this test fails if that call
     * is removed.
     *
     * <p>Unlike {@link #reactorFormsAndProxiesEnergyCap} -- which re-queries the level fresh each tick and so would
     * pass even without the invalidation (a fresh query always re-resolves) -- this holds a single cache across
     * formation, so the cached pre-adoption {@code null} is only cleared by the invalidation the fix performs.</p>
     *
     * <p>Determinism rests on the setup body running before the controller's first tick: the controller builds its
     * shape and adopts its ports only from its server ticker (never from {@code setBlock}), and a test method body
     * runs at structure-load before that ticker has fired (the same first-tick ordering
     * {@link #reactorFormsAndProxiesEnergyCap} relies on). So the cache is created and primed with the pre-adoption
     * {@code null} -- asserted, not assumed, so a future ordering change surfaces as a failure rather than a vacuous
     * pass -- strictly before any adoption or invalidation. The criterion then lets the controller tick: once the
     * multiblock validates and the energy port is adopted, the cache must re-resolve to the controller's energy
     * handler (the same proxy target {@link #reactorFormsAndProxiesEnergyCap} checks).</p>
     */
    public static void reactorFormationReconnectsCachedPortCapability(GameTestHelper helper) {
        FissionControllerBlockEntity controller = placeController(helper);
        List<BlockPos> ports = buildShellAndPlacePorts(helper);
        BlockPos energyWorldPos = energyBlockEntity(helper, ports.get(0)).getBlockPos();

        // Build the cache and prime it before the controller's ticker runs, so it observes the port's pre-adoption
        // state. Cache against the absolute world position -- the cache registers its invalidation listener with the
        // level by absolute pos, the same key setMultiblockHandlers passes to invalidateCapabilities.
        BlockCapabilityCache<IEnergyStorage, Direction> cache = BlockCapabilityCache.create(
                Capabilities.EnergyStorage.BLOCK, helper.getLevel(), energyWorldPos, null);

        // The port has no controller yet, so its resolver returns null; this getCapability() caches that null and arms
        // the invalidation listener. Assert it rather than assume it, so a setup that accidentally adopted the port
        // first (which would make the post-formation check pass for the wrong reason) fails here instead.
        helper.assertTrue(cache.getCapability() == null,
                Component.literal("reactor energy port resolved a capability before the controller adopted it; "
                        + "the cache-reconnect test needs the pre-adoption null state"));

        // Let the controller tick: it builds its shape, adopts the ports (firing invalidateCapabilities on each), and
        // validates. succeedWhen re-runs the criterion each tick until it passes. The shape guard mirrors
        // reactorFormsAndProxiesEnergyCap -- isValidMultiblock dereferences the shape, which is briefly null on the
        // first tick before the block-entity ticker has built it.
        helper.succeedWhen(() -> {
            helper.assertTrue(controller.getReactorShape() != null, Component.literal("controller has not built its reactor shape yet"));
            helper.assertTrue(controller.isValidMultiblock(), Component.literal("fission reactor multiblock did not validate"));

            // The invalidation the fix performs should have cleared the cached null, so this re-resolves to the
            // controller's energy handler. Without that invalidation the cache stays stuck on the null cached above.
            IEnergyStorage reconnected = cache.getCapability();
            helper.assertTrue(reconnected != null,
                    Component.literal("reactor energy port capability cache did not reconnect after multiblock formation"));
            helper.assertTrue(reconnected == controller.getEnergyHandler(),
                    Component.literal("reconnected port capability is not the controller's energy handler"));
        });
    }

    /**
     * Proves that a server-bound packet's {@code handle} body runs on the server and mutates the addressed
     * block-entity. The encode/decode unit round-trips prove the wire format survives a trip, and the full-chain
     * boot smoke proves the packets register; this is the only check that exercises the handler body itself. It
     * drives {@link ToggleReactorAutoejectPacket} through its production receive path and asserts the reactor
     * controller's {@code autoeject} flag flips.
     *
     * <p>The packet is constructed and its production
     * {@link ToggleReactorAutoejectPacket#handle(IPayloadContext) handle(IPayloadContext)} is invoked directly
     * with a minimal-but-real {@link GameTestPayloadContext}. The handler reads only {@code player()} (then
     * {@code player.level().getBlockEntity(pos)}), so the context carries {@link PacketFlow#SERVERBOUND} -- the real
     * server-bound flow -- and a {@link GameTestHelper#makeMockPlayer(GameType) mock player} whose {@code level()}
     * is the gametest {@link net.minecraft.server.level.ServerLevel}; every other context method is unused and
     * throws. This is the production handler logic, executed server-side against the real block-entity, so it proves
     * the handler body, its block-entity effect, and the side. <b>Limitation:</b> it does not exercise the
     * registrar's decoder-and-side binding -- that the id is wired server-bound to this handler with this decoder --
     * which is covered by the boot smoke ({@code fullChainLoaded}) plus the encode/decode round-trip unit tests; nor
     * the network-thread-to-main-thread {@link IPayloadContext#enqueueWork(Runnable)} hop, which
     * {@link com.smashingmods.alchemylib.api.network.AbstractPacketHandler} performs and which a gametest already runs
     * on the server main thread.</p>
     *
     * <p>A bare controller suffices: its {@code autoeject} field defaults {@code false}, and the handler's
     * {@code tryEjectOutputs()} call (only on a {@code true} toggle) early-returns while no output port is adopted, so
     * the assertable effect is purely the flag flip. The test captures the initial value, sends the opposite, ticks
     * once, and asserts the controller's {@link AbstractReactorBlockEntity#isAutoEject()} changed to match.</p>
     */
    public static void autoejectPacketTogglesReactor(GameTestHelper helper) {
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
        // other context methods are unused.
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        IPayloadContext context = new GameTestPayloadContext(player, PacketFlow.SERVERBOUND);
        new ToggleReactorAutoejectPacket(controllerWorldPos, target).handle(context);

        // One tick to settle, then assert the handler applied the flip to the block-entity it was addressed to.
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(controller.isAutoEject() == target,
                    Component.literal("ToggleReactorAutoejectPacket handler did not flip autoeject: expected " + target
                            + ", found " + controller.isAutoEject()));
            helper.succeed();
        });
    }

    /**
     * Removing a reactor controller before it has ticked once must not throw. A controller builds its
     * {@link ReactorShape} lazily on its first server tick, so a controller placed and removed within the same tick
     * still has a {@code null} shape when {@link AbstractReactorBlockEntity#onRemove()} runs. That path dereferences
     * the shape twice -- once via {@code resetIO() -> setMultiblockHandlers()} and again in {@code onRemove()}'s own
     * core-block sweep -- so each site is guarded against a null shape; without those guards the removal throws a
     * {@link NullPointerException}. This drives {@code onRemove()} directly on a never-ticked controller -- the
     * {@code reactorShape == null} state the guards exist for -- and asserts it returns normally. (1.21.5 moved the
     * block-entity removal teardown into {@code BlockEntity#preRemoveSideEffects}, which still calls this
     * {@code onRemove()}; driving it directly keeps the test on the exact null-shape path the guards protect.)
     */
    public static void removalBeforeTickDoesNotThrow(GameTestHelper helper) {
        FissionControllerBlockEntity controller = placeController(helper);

        // The controller has not ticked, so its lazily-built shape is still null -- the exact state onRemove's null
        // guards protect. Fail explicitly if that precondition ever changes, otherwise the test would pass vacuously.
        helper.assertTrue(controller.getReactorShape() == null,
                Component.literal("controller already built its reactor shape; removal test needs the pre-tick null-shape state"));

        controller.onRemove();
        helper.succeed();
    }

    // Places the fission controller at CONTROLLER_POS with a fixed horizontal facing and returns its block-entity,
    // failing the test if either the block or its block-entity is missing. The FissionControllerBlockEntity constructor
    // sets reactorType=FISSION, so no manual setReactorType is needed; we assert it as a guard against that changing.
    private static FissionControllerBlockEntity placeController(GameTestHelper helper) {
        helper.setBlock(CONTROLLER_POS, BlockRegistry.FISSION_CONTROLLER.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, FACING));
        FissionControllerBlockEntity controller = helper.getBlockEntity(CONTROLLER_POS, FissionControllerBlockEntity.class);
        if (controller == null) {
            helper.fail(Component.literal("expected a FissionControllerBlockEntity at " + CONTROLLER_POS), CONTROLLER_POS);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        helper.assertTrue(controller.getReactorType() == ReactorType.FISSION,
                Component.literal("fission controller reactorType was " + controller.getReactorType() + ", expected FISSION"));
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
    // against. The shell is built entirely in relative space rather than relying on relativePos() to invert
    // absolutePos().
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
                Component.literal("expected at least 3 border cells for the reactor ports, found " + sortedBorder.size()));

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
        ReactorEnergyBlockEntity energy = helper.getBlockEntity(pos, ReactorEnergyBlockEntity.class);
        if (energy == null) {
            helper.fail(Component.literal("expected a ReactorEnergyBlockEntity at " + pos), pos);
            throw new IllegalStateException("unreachable -- helper.fail throws");
        }
        return energy;
    }
}
