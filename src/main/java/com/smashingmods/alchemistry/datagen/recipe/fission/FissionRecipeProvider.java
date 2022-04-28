package com.smashingmods.alchemistry.datagen.recipe.fission;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class FissionRecipeProvider {

    Consumer<FinishedRecipe> consumer;

    public FissionRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public void register() {
//        for (int i = 2; i <= 118; i++) {
//            FissionRecipeBuilder.recipe(i).build(consumer);
//        }
    }
}
