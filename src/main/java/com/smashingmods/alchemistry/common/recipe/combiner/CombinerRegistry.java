package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class CombinerRegistry {

    private static List<CombinerRecipe> recipes;

    public static List<CombinerRecipe> getRecipes(Level pLevel) {
        if (recipes == null) {
            recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType() == RecipeRegistry.COMBINER_TYPE)
                    .map(recipe -> (CombinerRecipe) recipe)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}