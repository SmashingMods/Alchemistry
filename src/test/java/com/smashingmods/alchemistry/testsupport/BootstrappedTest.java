package com.smashingmods.alchemistry.testsupport;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

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
 */
public abstract class BootstrappedTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }
}
