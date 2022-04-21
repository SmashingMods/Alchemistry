package com.smashingmods.alchemistry.common.recipe;

import com.smashingmods.alchemistry.common.recipe.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.SerializerRegistry;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

public class LiquifierRegistry {

    private static List<LiquifierRecipe> recipes = null;

    public static List<LiquifierRecipe> getRecipes(Level world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == SerializerRegistry.LIQUIFIER_TYPE)
                    .map(x -> (LiquifierRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}