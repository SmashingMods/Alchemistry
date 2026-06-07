package com.smashingmods.alchemistry.common.recipe;

import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tier-2 tests pinning the structural cardinality of each Alchemistry recipe type: how many inputs/outputs it
 * carries. Recipes are built from vanilla {@code Items.*} (no mod-Item construction) under
 * {@link BootstrappedTest}'s bootstrap so {@link ItemStack} and {@link IngredientStack} are usable.
 */
class RecipeCardinalityTest extends BootstrappedTest {

    private static final ResourceLocation ID = new ResourceLocation("alchemistry", "test");
    private static final String GROUP = "test";

    @Test
    void recipeCardinality_fissionHasTwoOutputs() {
        FissionRecipe recipe = new FissionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.COPPER_INGOT));

        // getOutput() exposes the pair; getOutput1/getOutput2 are the two halves.
        assertEquals(2, ((java.util.List<?>) recipe.getOutput()).size());
        assertEquals(Items.GOLD_INGOT, recipe.getOutput1().getItem());
        assertEquals(Items.COPPER_INGOT, recipe.getOutput2().getItem());
    }

    @Test
    void recipeCardinality_fusionHasTwoInputs() {
        FusionRecipe recipe = new FusionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.NETHERITE_INGOT));

        assertEquals(2, ((java.util.List<?>) recipe.getInput()).size());
        assertEquals(Items.IRON_INGOT, recipe.getInput1().getItem());
        assertEquals(Items.GOLD_INGOT, recipe.getInput2().getItem());
    }

    @Test
    void recipeCardinality_dissolverHasOneInputAndProbabilitySetOutput() {
        ProbabilitySet output = ProbabilitySet.Builder.createSet()
                .addGroup(50.0, new ItemStack(Items.IRON_NUGGET))
                .build();

        IngredientStack input = new IngredientStack(Items.IRON_INGOT);
        DissolverRecipe recipe = new DissolverRecipe(ID, GROUP, input, output);

        // One IngredientStack input -> a ProbabilitySet output.
        assertNotNull(recipe.getInput());
        assertEquals(input, recipe.getInput());
        assertEquals(output, recipe.getOutput());
    }

    @Test
    void recipeCardinality_combinerTakesInputSet() {
        Set<IngredientStack> inputs = new LinkedHashSet<>();
        inputs.add(new IngredientStack(Items.IRON_INGOT));
        inputs.add(new IngredientStack(Items.GOLD_INGOT));
        inputs.add(new IngredientStack(Items.COPPER_INGOT));

        CombinerRecipe recipe = new CombinerRecipe(ID, GROUP, inputs, new ItemStack(Items.NETHERITE_INGOT));

        assertEquals(3, recipe.getInput().size());
        assertEquals(Items.NETHERITE_INGOT, recipe.getOutput().getItem());
    }
}
