package com.smashingmods.alchemistry.blocks.liquifier;

import com.smashingmods.alchemistry.Registry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class LiquifierRegistry {

    private static List<LiquifierRecipe> recipes = null;

    public static List<LiquifierRecipe> getRecipes(Level world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == Registry.LIQUIFIER_TYPE)
                    .map(x -> (LiquifierRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}