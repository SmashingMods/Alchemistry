package com.smashingmods.alchemistry.block.atomizer;

import com.smashingmods.alchemistry.Registry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class AtomizerRegistry {

    private static List<AtomizerRecipe> recipes = null;

    public static List<AtomizerRecipe> getRecipes(Level pLevel) {
        if (recipes == null) {
            recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType() == Registry.ATOMIZER_TYPE)
                    .map(recipe -> (AtomizerRecipe) recipe)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}
