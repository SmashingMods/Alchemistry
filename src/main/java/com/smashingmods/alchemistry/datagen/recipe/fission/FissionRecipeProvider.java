package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class FissionRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public FissionRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new FissionRecipeProvider(pConsumer).register();
    }

    private void register() {
        for (int index = 2; index <= 118; index++) {
            ItemRegistry.getElementByAtomicNumber(index).ifPresent(this::fission);
        }
    }

    private void fission(ElementItem pInput) {
        FissionRecipeBuilder.createRecipe(pInput)
                .group("fission")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(DatagenUtil.getLocation(pInput, "fission")))
                .save(consumer);
    }
}
