package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class AtomizerRegistry {

    private static List<AtomizerRecipe> recipes;

    public static List<AtomizerRecipe> getRecipes(Level pLevel) {
        if (recipes == null) {
            recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType() == RecipeRegistry.ATOMIZER_TYPE)
                    .map(recipe -> (AtomizerRecipe) recipe)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}
