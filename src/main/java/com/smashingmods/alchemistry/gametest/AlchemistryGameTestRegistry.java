package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.smashingmods.alchemistry.Alchemistry.MODID;

/**
 * Registers Alchemistry's gametests onto the 1.21.5 registry-based framework. 1.21.5 removed the annotation-driven
 * system ({@code @GameTest}/{@code @GameTestHolder}/{@code @PrefixGameTestTemplate}); a test now has three pieces:
 *
 * <ul>
 *   <li>a <b>test function</b> -- a {@code Consumer<GameTestHelper>} (the body) registered to
 *       {@link BuiltInRegistries#TEST_FUNCTION} via a {@link DeferredRegister}, yielding a
 *       {@link ResourceKey} the instance points at;</li>
 *   <li>a <b>test environment</b> -- a {@link TestEnvironmentDefinition} that sets up/tears down the world for a
 *       batch; an empty {@link TestEnvironmentDefinition.AllOf} is a no-op, which is all these tests need;</li>
 *   <li>a <b>test instance</b> -- a {@link FunctionGameTestInstance} pairing the function key with a
 *       {@link TestData} (the structure to load plus run parameters: tick budget, required flag), registered into
 *       the {@code TEST_INSTANCE} datapack registry.</li>
 * </ul>
 *
 * <p>NeoForge surfaces the environment + instance registration through {@link RegisterGameTestsEvent} (mod bus); the
 * function registry is a plain built-in registry a {@link DeferredRegister} can append to. The {@code gameTestServer}
 * run launches with no test selection, so it runs every non-manual instance registered here. Each test is registered
 * with {@code required = true}, so a single failure fails the gate.</p>
 *
 * <p>The structures are the staged all-air boxes from {@link GameTestStructureProvider}: a 3x3x3
 * {@code alchemistry:loadsemptytemplate} for the single-block and data-only tests, and a 9x9x9
 * {@code alchemistry:reactor_space} for the reactor multiblock tests. They are loaded as ordinary datapack structures
 * from {@code data/alchemistry/structure/*.nbt} (1.21.5 dropped the old {@code gameteststructures/*.snbt}
 * working-directory source).</p>
 */
public final class AlchemistryGameTestRegistry {

    private AlchemistryGameTestRegistry() {}

    // The empty no-op environment every Alchemistry test runs in: no game-rule tweaks, time-of-day, or weather, since
    // these tests drive block-entities and data directly rather than depending on world conditions.
    private static final ResourceLocation ENVIRONMENT_ID = ResourceLocation.fromNamespaceAndPath(MODID, "default");

    // The two staged structures (see GameTestStructureProvider): a 3x3x3 air box for the single-block / data-only
    // tests and a 9x9x9 air box with room for a 5x5x5 reactor shell.
    private static final ResourceLocation EMPTY_TEMPLATE = ResourceLocation.fromNamespaceAndPath(MODID, "loadsemptytemplate");
    private static final ResourceLocation REACTOR_TEMPLATE = ResourceLocation.fromNamespaceAndPath(MODID, "reactor_space");

    // The default per-test tick budget. Matches the old @GameTest default of 100 ticks; the liquifier test overrides
    // it (its operation length equals 100, so it needs headroom past the default window -- see its javadoc).
    private static final int DEFAULT_MAX_TICKS = 100;
    private static final int LIQUIFIER_MAX_TICKS = 200;

    private static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, MODID);

    // The full gametest set, declared once so register-the-function and register-the-instance stay in lockstep: every
    // entry registers its body to TEST_FUNCTION here and gets a matching FunctionGameTestInstance in the event below.
    private static final List<TestEntry> TESTS = new ArrayList<>();

    // -- AlchemistryGameTests: the full-chain load smoke + the payload-registration pin. --
    private static final TestEntry FULL_CHAIN_LOADED =
            define("full_chain_loaded", AlchemistryGameTests::fullChainLoaded, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry SENT_PAYLOADS_REGISTERED_SERVER_BOUND =
            define("sent_payloads_registered_server_bound", AlchemistryGameTests::sentPayloadsRegisteredServerBound, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);

    // -- MachineGameTests: the standalone item machines (dissolver) + the fusion transfer handler. --
    private static final TestEntry DISSOLVER_PROCESSING =
            define("dissolver_processing", MachineGameTests::dissolverProcessing, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry DISSOLVER_PROCESSING_NO_LOSS =
            define("dissolver_processing_no_loss", MachineGameTests::dissolverProcessingNoLoss, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry RECIPE_RESOLUTION =
            define("recipe_resolution", MachineGameTests::recipeResolution, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry MACHINE_CAPABILITY =
            define("machine_capability", MachineGameTests::machineCapability, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry MENU_OPENS =
            define("menu_opens", MachineGameTests::menuOpens, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry FUSION_TRANSFER_DEBITS_BOTH_INPUTS =
            define("fusion_transfer_debits_both_inputs", MachineGameTests::fusionTransferDebitsBothInputs, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry FUSION_TRANSFER_SPLIT_STACKS_MOVES_ALL_FULL_OPERATIONS =
            define("fusion_transfer_split_stacks_moves_all_full_operations", MachineGameTests::fusionTransferSplitStacksMovesAllFullOperations, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry TRANSFER_ON_LOCKED_MACHINE_FOR_DIFFERENT_RECIPE_MOVES_NOTHING =
            define("transfer_on_locked_machine_for_different_recipe_moves_nothing", MachineGameTests::transferOnLockedMachineForDifferentRecipeMovesNothing, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry LOCKED_COMBINER_ACCEPTS_FULL_STACK_OF_ITS_INGREDIENT =
            define("locked_combiner_accepts_full_stack_of_its_ingredient", MachineGameTests::lockedCombinerAcceptsFullStackOfItsIngredient, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);

    // -- RecipeSelectionGameTests: the recipe-selection contract on the selector machines (combiner + compactor). --
    private static final TestEntry COMBINER_AUTO_PICK_DETERMINISTIC =
            define("combiner_auto_pick_deterministic", RecipeSelectionGameTests::combinerAutoPickDeterministic, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry COMBINER_SELECTION_STICKS_THROUGH_PROCESSING =
            define("combiner_selection_sticks_through_processing", RecipeSelectionGameTests::combinerSelectionSticksThroughProcessing, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry COMPACTOR_SELECTION_STICKS_THROUGH_PROCESSING =
            define("compactor_selection_sticks_through_processing", RecipeSelectionGameTests::compactorSelectionSticksThroughProcessing, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry COMBINER_RESELECT_MID_PROCESSING_TAKES_EFFECT =
            define("combiner_reselect_mid_processing_takes_effect", RecipeSelectionGameTests::combinerReselectMidProcessingTakesEffect, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);

    // -- FluidMachineGameTests: the fluid I/O seam (liquifier item->fluid, atomizer fluid->item). --
    private static final TestEntry LIQUIFIER_PROCESSES_ITEM_TO_FLUID =
            define("liquifier_processes_item_to_fluid", FluidMachineGameTests::liquifierProcessesItemToFluid, EMPTY_TEMPLATE, LIQUIFIER_MAX_TICKS);
    private static final TestEntry ATOMIZER_PROCESSES_FLUID_TO_ITEM =
            define("atomizer_processes_fluid_to_item", FluidMachineGameTests::atomizerProcessesFluidToItem, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry LIQUIFIER_TRANSFER_WITH_PRODUCED_FLUID_STILL_TRANSFERS =
            define("liquifier_transfer_with_produced_fluid_still_transfers", FluidMachineGameTests::liquifierTransferWithProducedFluidStillTransfers, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);

    // -- GuidebookGameTests: the Modonomicon book refs + build. --
    private static final TestEntry GUIDEBOOK_ITEM_REFS_RESOLVE =
            define("guidebook_item_refs_resolve", GuidebookGameTests::guidebook_itemRefsResolve, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry GUIDEBOOK_BOOK_LOADS =
            define("guidebook_book_loads", GuidebookGameTests::guidebook_bookLoads, EMPTY_TEMPLATE, DEFAULT_MAX_TICKS);

    // -- ReactorGameTests: the fission reactor multiblock (needs the 9x9x9 structure). --
    private static final TestEntry REACTOR_FORMS_AND_PROXIES_ENERGY_CAP =
            define("reactor_forms_and_proxies_energy_cap", ReactorGameTests::reactorFormsAndProxiesEnergyCap, REACTOR_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry REACTOR_FORMATION_RECONNECTS_CACHED_PORT_CAPABILITY =
            define("reactor_formation_reconnects_cached_port_capability", ReactorGameTests::reactorFormationReconnectsCachedPortCapability, REACTOR_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry AUTOEJECT_PACKET_TOGGLES_REACTOR =
            define("autoeject_packet_toggles_reactor", ReactorGameTests::autoejectPacketTogglesReactor, REACTOR_TEMPLATE, DEFAULT_MAX_TICKS);
    private static final TestEntry REMOVAL_BEFORE_TICK_DOES_NOT_THROW =
            define("removal_before_tick_does_not_throw", ReactorGameTests::removalBeforeTickDoesNotThrow, REACTOR_TEMPLATE, DEFAULT_MAX_TICKS);

    public static void register(IEventBus modEventBus) {
        TEST_FUNCTIONS.register(modEventBus);
        modEventBus.addListener(AlchemistryGameTestRegistry::registerGameTests);
    }

    // Registers the no-op environment, then one FunctionGameTestInstance per declared test, each pinned to its
    // structure and tick budget and marked required so a failure fails the gate.
    private static void registerGameTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition> environment = event.registerEnvironment(ENVIRONMENT_ID, new TestEnvironmentDefinition.AllOf());
        for (TestEntry entry : TESTS) {
            TestData<Holder<TestEnvironmentDefinition>> data =
                    new TestData<>(environment, entry.structure(), entry.maxTicks(), 0, true);
            event.registerTest(entry.instanceId(), new FunctionGameTestInstance(entry.functionKey(), data));
        }
    }

    // Registers a test body to TEST_FUNCTION and records it for instance registration. The function and instance share
    // the same id (modid:name), so the registry name reads identically across both registries.
    private static TestEntry define(String name, Consumer<GameTestHelper> body, ResourceLocation structure, int maxTicks) {
        DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> holder = TEST_FUNCTIONS.register(name, () -> body);
        TestEntry entry = new TestEntry(holder, structure, maxTicks);
        TESTS.add(entry);
        return entry;
    }

    // One gametest: the DeferredHolder for its body (whose key both feeds the FunctionGameTestInstance and names the
    // instance) plus the structure and tick budget the instance runs under.
    private record TestEntry(DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> function,
                             ResourceLocation structure, int maxTicks) {

        ResourceKey<Consumer<GameTestHelper>> functionKey() {
            return function.getKey();
        }

        // The test instance is keyed by the same id as its function, so the gameTestServer log names it modid:name.
        ResourceLocation instanceId() {
            return function.getId();
        }
    }
}
