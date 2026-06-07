package com.smashingmods.alchemistry.common.recipe.fusion;

import com.smashingmods.alchemistry.datagen.recipe.fusion.FusionRecipeProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-0 tests for the pure fusion math in {@link FusionRecipeProvider}: a pair is valid only when the inputs are
 * ordered ({@code x <= y}, to avoid mirror duplicates) and their sum still maps to an existing element
 * ({@code x + y <= count}); the output atomic number is simply {@code x + y}.
 */
class FusionRecipeProviderTest {

    private static final int COUNT = 118;

    @Test
    void fusion_isValidOrderedPairWithinCount() {
        assertTrue(FusionRecipeProvider.isValidFusion(2, 3, COUNT));
    }

    @Test
    void fusion_isValidRejectsUnorderedPair() {
        // x > y is a mirror of a pair already emitted, so it is rejected.
        assertFalse(FusionRecipeProvider.isValidFusion(5, 3, COUNT));
    }

    @Test
    void fusion_isValidRejectsSumOverCount() {
        // x + y exceeds the highest atomic number, so the output element does not exist.
        assertFalse(FusionRecipeProvider.isValidFusion(60, 60, COUNT));
    }

    @Test
    void fusion_isValidAcceptsSumEqualToCount() {
        // x + y == count is the boundary: the heaviest element is still a valid output. Inputs stay ordered (x <= y),
        // since the ordering rule rejects x > y regardless of the sum.
        assertTrue(FusionRecipeProvider.isValidFusion(58, 60, COUNT));
    }

    @Test
    void fusion_output() {
        assertEquals(5, FusionRecipeProvider.fusionOutput(2, 3));
    }
}
