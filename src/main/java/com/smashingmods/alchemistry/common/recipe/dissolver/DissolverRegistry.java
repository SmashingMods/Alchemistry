package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class DissolverRegistry {

    private static List<DissolverRecipe> recipes;

    public static List<DissolverRecipe> getRecipes(Level pLevel) {
        if (recipes == null) {
            recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType() == RecipeRegistry.DISSOLVER_TYPE)
                    .map(recipe -> (DissolverRecipe) recipe)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}