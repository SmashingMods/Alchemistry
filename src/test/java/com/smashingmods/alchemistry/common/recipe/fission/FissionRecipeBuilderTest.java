package com.smashingmods.alchemistry.common.recipe.fission;

import com.smashingmods.alchemistry.datagen.recipe.fission.FissionRecipeBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-0 tests for the pure fission split math in {@link FissionRecipeBuilder#fissionSplit(int)}:
 * an even input halves evenly, an odd input rounds the first output up, and the pair always sums to the input.
 */
class FissionRecipeBuilderTest {

    @Test
    void fissionSplit_evenHalvesEvenly() {
        assertSplit(2, 1, 1);
        assertSplit(8, 4, 4);
        assertSplit(118, 59, 59);
    }

    @Test
    void fissionSplit_oddRoundsFirstUp() {
        assertSplit(1, 1, 0);
        assertSplit(7, 4, 3);
        assertSplit(117, 59, 58);
    }

    @Test
    void fissionSplit_outputsSumToInput() {
        for (int n : new int[]{1, 2, 3, 4, 7, 8, 26, 79, 117, 118}) {
            int[] split = FissionRecipeBuilder.fissionSplit(n);
            assertEquals(n, split[0] + split[1], () -> "outputs must sum to input for N=" + n);
        }
    }

    private static void assertSplit(int input, int expected1, int expected2) {
        int[] split = FissionRecipeBuilder.fissionSplit(input);
        assertEquals(expected1, split[0], () -> "output1 for N=" + input);
        assertEquals(expected2, split[1], () -> "output2 for N=" + input);
    }
}
