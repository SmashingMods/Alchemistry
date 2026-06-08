package com.smashingmods.alchemistry.testsupport;

import io.netty.buffer.Unpooled;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Shared base for Tier-2 tests that need Minecraft's built-in registries populated. Extend this and the
 * {@code SharedConstants.setVersion(...)} + {@link Bootstrap#bootStrap()} incantation runs once before the
 * subclass's tests, instead of every bootstrap-needing test class redeclaring its own {@code @BeforeAll}.
 *
 * <p>{@code @BeforeAll} is inherited, so each subclass runs {@link #bootstrap()} once for its own container.
 * {@link Bootstrap#bootStrap()} guards itself against re-entry, so inheriting it per subclass is harmless.</p>
 *
 * <p>Subclasses use vanilla registry entries (e.g. {@code Items.STONE}, {@code Fluids.WATER}) that
 * {@link Bootstrap#bootStrap()} registers; constructing a mod {@code Item} would instead trigger an
 * intrusive-holder registry write that needs the registry unfrozen, which these tests neither need nor want.</p>
 *
 * <p>{@link #registryBuffer()} hands subclasses a {@link RegistryFriendlyByteBuf} backed by the bootstrapped
 * built-in registries, which the 1.20.6 {@code StreamCodec}s (for {@code ItemStack}/{@code FluidStack}/
 * {@code IngredientStack} content) need to resolve registry holders while encoding and decoding.</p>
 */
public abstract class BootstrappedTest {

    @BeforeAll
    static void bootstrap() {
        primeLoadingModList();
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
        markBuiltInRegistriesSynced();
    }

    /**
     * Seeds {@code LoadingModList} with an empty instance before {@link Bootstrap#bootStrap()} runs. At 1.21.1
     * NeoForge patches {@code FeatureFlags.<clinit>} (which {@link Bootstrap#bootStrap()} triggers) to call
     * {@code FeatureFlagLoader.loadModdedFlags(...)}, which dereferences {@code LoadingModList.get()}. That
     * static is null in a plain-JUnit JVM (no FML launch sets it), so the bare bootstrap NPEs during the
     * {@code FeatureFlags} static initialiser; the resulting {@code ExceptionInInitializerError} poisons
     * {@code FeatureFlags} for the whole JVM and leaves the built-in registries empty. {@code LoadingModList.of(...)}
     * is the factory a launch uses to populate {@code INSTANCE}; an all-empty call gives a non-null list whose
     * {@code getModFiles()} is empty, so {@code loadModdedFlags} registers no modded flags and does not NPE.
     * Runs before {@link Bootstrap#bootStrap()} so the static is set before the {@code FeatureFlags} initialiser
     * reads it. The factory is reached reflectively to avoid pinning the test source to FML's internal
     * mod-discovery types; that {@code of(...)} sets the static is fixed by the platform.
     */
    private static void primeLoadingModList() {
        try {
            Class<?> loadingModList = Class.forName("net.neoforged.fml.loading.LoadingModList");
            Method of = loadingModList.getDeclaredMethod("of", List.class, List.class, List.class, List.class, Map.class);
            of.setAccessible(true);
            of.invoke(null, List.of(), List.of(), List.of(), List.of(), Map.of());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to prime LoadingModList for the test bootstrap", exception);
        }
    }

    /**
     * A {@link RegistryAccess} over the bootstrapped built-in registries. This is the registry view the
     * registry-aware {@code StreamCodec}s read from when encoding or decoding; there is no server, so the
     * built-in registry-of-registries is the access source.
     *
     * @return RegistryAccess
     */
    protected static RegistryAccess registryAccess() {
        return RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    }

    /**
     * A fresh, empty {@link RegistryFriendlyByteBuf} backed by {@link #registryAccess()}, ready to encode into.
     *
     * @return RegistryFriendlyByteBuf
     */
    protected static RegistryFriendlyByteBuf registryBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
    }

    /**
     * Marks every built-in registry as network-synced. The {@code StreamCodec}s that encode an {@code ItemStack}
     * (and therefore {@code IngredientStack}) or a {@code FluidStack} by registry id refuse to run against a
     * built-in registry whose {@code doesSync()} is false, throwing
     * {@code "Cannot use ID syncing for non-synced built-in registry"}. NeoForge sets that flag during the
     * mod-loading lifecycle, which {@link Bootstrap#bootStrap()} does not run, so a bare bootstrap leaves the flag
     * false and the id-based codecs unusable. Flipping it restores the sync state a launched game has. The setter
     * is package-private on NeoForge's {@code BaseMappedRegistry}, so it is reached reflectively; that the
     * registries are concrete {@code MappedRegistry} subclasses is fixed by the platform.
     */
    private static void markBuiltInRegistriesSynced() {
        try {
            Class<?> baseMappedRegistry = Class.forName("net.neoforged.neoforge.registries.BaseMappedRegistry");
            Method setSync = baseMappedRegistry.getDeclaredMethod("setSync", boolean.class);
            setSync.setAccessible(true);
            for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
                if (baseMappedRegistry.isInstance(registry)) {
                    setSync.invoke(registry, true);
                }
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to mark built-in registries synced for the test bootstrap", exception);
        }
    }
}
