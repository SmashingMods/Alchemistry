package com.smashingmods.alchemistry.common.recipe;

import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // getIngredients() feeds JEI's input slots. NonNullList.of(default, elements...) takes its first arg as the
    // list's *default* rather than an element, so the old NonNullList.of(<ingredient>) returned a size-0 list and
    // JEI rendered empty input slots. These tests pin that each recipe type now reports a populated ingredient list
    // carrying the right input(s) -- single-input types report one ingredient, Fusion reports both of its inputs.

    @Test
    void getIngredients_dissolverNonEmpty() {
        ProbabilitySet output = ProbabilitySet.Builder.createSet()
                .addGroup(50.0, new ItemStack(Items.IRON_NUGGET))
                .build();
        DissolverRecipe recipe = new DissolverRecipe(ID, GROUP, new IngredientStack(Items.IRON_INGOT), output);

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(1, ingredients.size());
        assertFalse(ingredients.get(0).isEmpty());
        assertTrue(ingredients.get(0).test(new ItemStack(Items.IRON_INGOT)));
    }

    @Test
    void getIngredients_compactorNonEmpty() {
        CompactorRecipe recipe = new CompactorRecipe(ID, GROUP,
                new IngredientStack(Items.IRON_INGOT), new ItemStack(Items.IRON_BLOCK));

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(1, ingredients.size());
        assertFalse(ingredients.get(0).isEmpty());
        assertTrue(ingredients.get(0).test(new ItemStack(Items.IRON_INGOT)));
    }

    @Test
    void getIngredients_liquifierNonEmpty() {
        LiquifierRecipe recipe = new LiquifierRecipe(ID, GROUP,
                new IngredientStack(Items.ICE), new FluidStack(Fluids.WATER, 500));

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(1, ingredients.size());
        assertFalse(ingredients.get(0).isEmpty());
        assertTrue(ingredients.get(0).test(new ItemStack(Items.ICE)));
    }

    @Test
    void getIngredients_fissionNonEmpty() {
        FissionRecipe recipe = new FissionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.COPPER_INGOT));

        // One input -> one ingredient (Fission's two stacks are outputs, not inputs).
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(1, ingredients.size());
        assertFalse(ingredients.get(0).isEmpty());
        assertTrue(ingredients.get(0).test(new ItemStack(Items.IRON_INGOT)));
    }

    @Test
    void getIngredients_atomizerNonEmpty() {
        AtomizerRecipe recipe = new AtomizerRecipe(ID, GROUP,
                new FluidStack(Fluids.WATER, 500), new ItemStack(Items.SNOWBALL));

        // The fluid input is surfaced to JEI as its bucket item (water -> water bucket).
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(1, ingredients.size());
        assertFalse(ingredients.get(0).isEmpty());
        assertTrue(ingredients.get(0).test(new ItemStack(Items.WATER_BUCKET)));
    }

    @Test
    void getIngredients_fusionHasBothInputs() {
        FusionRecipe recipe = new FusionRecipe(ID, GROUP,
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.NETHERITE_INGOT));

        // Two inputs -> two separate ingredients, one per input slot.
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        assertEquals(2, ingredients.size());
        assertTrue(ingredients.get(0).test(new ItemStack(Items.IRON_INGOT)));
        assertTrue(ingredients.get(1).test(new ItemStack(Items.GOLD_INGOT)));
    }
}
