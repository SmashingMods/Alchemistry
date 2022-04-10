package com.smashingmods.alchemistry.blocks.atomizer;

import com.smashingmods.alchemistry.Registration;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class AtomizerRegistry {

    private static List<AtomizerRecipe> recipes = null;

    public static List<AtomizerRecipe> getRecipes(Level world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == Registration.ATOMIZER_TYPE)
                    .map(x -> (AtomizerRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}
