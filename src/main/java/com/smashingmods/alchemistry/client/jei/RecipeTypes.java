package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import mezz.jei.api.recipe.RecipeType;

public class RecipeTypes {
    public static final RecipeType<AtomizerRecipe> ATOMIZER = RecipeType.create(Alchemistry.MODID, "atomizer", AtomizerRecipe.class);
}
