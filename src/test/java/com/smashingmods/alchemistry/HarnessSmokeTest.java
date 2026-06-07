package com.smashingmods.alchemistry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-1 smoke test: proves the {@code test} source set runs JUnit Platform at all.
 * No Minecraft classpath required -- if this is green, the harness wiring is live.
 */
class HarnessSmokeTest {

    @Test
    void junitPlatformRunsTheTestSourceSet() {
        assertTrue(true);
    }
}
