package com.smashingmods.alchemistry.gametest;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * In-game test bodies for Alchemistry. 1.21.5 replaced the annotation-driven gametest framework
 * ({@code @GameTest}/{@code @GameTestHolder}/{@code @PrefixGameTestTemplate}) with a registry-based system: each
 * test body is a {@code Consumer<GameTestHelper>} registered to {@link BuiltInRegistries#TEST_FUNCTION}, paired with
 * a {@code GameTestInstance} (a structure + run parameters) in the {@code TEST_INSTANCE} datapack registry. The
 * {@code gameTestServer} run boots a dedicated server, runs every registered instance, and exits non-zero if any
 * required test fails. {@link AlchemistryGameTestRegistry} owns the registration; the bodies stay here as thin
 * {@code static} methods. This package is compiled into {@code src/main} so the mod scan discovers it, but the
 * {@code jar} task excludes it so the tests never ship in the published jar.
 *
 * <p>The test here is the chain load-smoke: a required check that asserts the full ChemLib -> AlchemyLib ->
 * Alchemistry chain registered before the server reached the in-world phase. Every gametest in this package -- this
 * load-smoke plus the richer per-machine and guidebook behaviour tests in {@link MachineGameTests},
 * {@link ReactorGameTests}, {@link FluidMachineGameTests}, and {@link GuidebookGameTests} -- is registered as
 * {@code required}, so a failure in any (including a behavioural regression) drives a non-zero {@code gameTestServer}
 * exit and fails the gate. This load-smoke is the broadest check: it is the first to break if the chain does not
 * register at all.</p>
 */
public class AlchemistryGameTests {

    private AlchemistryGameTests() {}

    /**
     * Asserts the full mod chain registered, then succeeds. The registry lookups are static and do not need the
     * in-world structure, but running them inside a gametest proves the checks execute against a fully-booted server
     * with the chain loaded.
     *
     * <p>Each check is a static built-in registry lookup against a known id from one rung of the chain: a ChemLib
     * element (hydrogen, element #1, always present), an Alchemistry machine block, its block-entity type, and its
     * recipe type. A missing id means that rung never registered, so the gate fails the required test.</p>
     */
    public static void fullChainLoaded(GameTestHelper helper) {
        assertRegistered(helper, "chemlib element", BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath("chemlib", "hydrogen"));
        assertRegistered(helper, "alchemistry block", BuiltInRegistries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver"));
        assertRegistered(helper, "alchemistry block-entity type", BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver_block_entity"));
        assertRegistered(helper, "alchemistry recipe type", BuiltInRegistries.RECIPE_TYPE,
                ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver"));
        helper.succeed();
    }

    private static void assertRegistered(GameTestHelper helper, String what, Registry<?> registry, ResourceLocation id) {
        if (!registry.containsKey(id)) {
            helper.fail(Component.literal(String.format("%s not registered: %s", what, id)));
        }
    }
}
