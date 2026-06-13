package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.gametest.AlchemistryGameTestRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Emits the gametest {@code test_environment} and {@code test_instance} datapack-registry entries as generated JSON
 * under {@code data/alchemistry/test_environment/} and {@code data/alchemistry/test_instance/}.
 *
 * <p>1.21.5's gametests are datapack-registry data, not runtime registrations: NeoForge's {@code RegisterGameTestsEvent}
 * re-fires on a normal dedicated server after those registries have frozen, so registering through it crashes the
 * server. Shipping the entries as data populates the registries while they are still writable and leaves them inert on
 * a normal server, while the {@code gameTestServer} run (and the {@code /test} command) read them back from the
 * registry like any other datapack entry. This mirrors how {@link GameTestStructureProvider} ships the test structures
 * as datapack {@code .nbt}.</p>
 *
 * <p>The single source of truth is {@link AlchemistryGameTestRegistry#tests()}: one no-op
 * {@link TestEnvironmentDefinition.AllOf} environment ({@link AlchemistryGameTestRegistry#ENVIRONMENT_ID}) every test
 * shares, then one {@link FunctionGameTestInstance} per declared test, pinned to its function key, structure, and tick
 * budget and marked {@code required = true} so a single failure fails the gate. The {@code function} keys come from the
 * same {@code TEST_FUNCTION} {@link net.neoforged.neoforge.registries.DeferredRegister} the registry registers at
 * runtime, so the JSON and the function bodies stay in lockstep.</p>
 */
public class GameTestRegistriesProvider extends DatapackBuiltinEntriesProvider {

    // The no-op environment and the instances live in two datapack registries; both bootstraps read from this builder
    // so each instance's TestData can look the environment Holder up by key (RegistrySetBuilder resolves the
    // cross-registry reference across the two add(...) blocks).
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TEST_ENVIRONMENT, context ->
                    context.register(environmentKey(), new TestEnvironmentDefinition.AllOf()))
            .add(Registries.TEST_INSTANCE, context -> {
                HolderGetter<TestEnvironmentDefinition> environments = context.lookup(Registries.TEST_ENVIRONMENT);
                Holder<TestEnvironmentDefinition> environment = environments.getOrThrow(environmentKey());
                for (AlchemistryGameTestRegistry.TestEntry entry : AlchemistryGameTestRegistry.tests()) {
                    TestData<Holder<TestEnvironmentDefinition>> data =
                            new TestData<>(environment, entry.structure(), entry.maxTicks(), 0, true);
                    ResourceKey<GameTestInstance> key = ResourceKey.create(Registries.TEST_INSTANCE, entry.instanceId());
                    context.register(key, new FunctionGameTestInstance(entry.functionKey(), data));
                }
            });

    public GameTestRegistriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BUILDER, Set.of(Alchemistry.MODID));
    }

    private static ResourceKey<TestEnvironmentDefinition> environmentKey() {
        return ResourceKey.create(Registries.TEST_ENVIRONMENT, AlchemistryGameTestRegistry.ENVIRONMENT_ID);
    }
}
