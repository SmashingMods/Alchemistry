package com.smashingmods.alchemistry.datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;


public interface RecipeBuilder {

    void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id);

    void validate(ResourceLocation id);
}
