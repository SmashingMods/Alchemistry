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
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
        markBuiltInRegistriesSynced();
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
