package com.smashingmods.alchemistry;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 test: proves the {@code test} source set sees the Minecraft classpath and that
 * {@link Bootstrap#bootStrap()} is reachable and populates the built-in registries.
 * If this compiles and runs green, plain-JUnit tests can exercise registry-backed code.
 */
class BootstrapGuardTest extends BootstrappedTest {

    @Test
    void bootstrapPopulatesItemRegistry() {
        assertTrue(BuiltInRegistries.ITEM.containsKey(new ResourceLocation("minecraft", "stone")));
    }
}
