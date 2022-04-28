package com.smashingmods.alchemistry.common.recipe.evaporator;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class EvaporatorRegistry {

    private static List<EvaporatorRecipe> recipes = null;

    public static List<EvaporatorRecipe> getRecipes(Level world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeRegistry.EVAPORATOR_TYPE)
                    .map(x -> (EvaporatorRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}