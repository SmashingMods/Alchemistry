package com.smashingmods.alchemistry.block.fission;

import com.smashingmods.alchemistry.Registry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class FissionRegistry {

    private static List<FissionRecipe> recipes = null;

    public static List<FissionRecipe> getRecipes(Level world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == Registry.FISSION_TYPE)
                    .map(x -> (FissionRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}