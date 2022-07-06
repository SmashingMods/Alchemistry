package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.datagen.recipe.dissolver.DissolverRecipeProvider;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;

public class FissionRecipeProvider {

    Consumer<FinishedRecipe> consumer;

    public FissionRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new FissionRecipeProvider(pConsumer).register();
    }

    private void register() {
        for (int index = 2; index <= 118; index++) {
            //noinspection OptionalGetWithoutIsPresent
            fission(ItemRegistry.getElementByAtomicNumber(index).get());
        }
    }

    private void fission(ElementItem pInput) {
        FissionRecipeBuilder.createRecipe(pInput)
                .group("fission")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pInput)))
                .save(consumer);
    }

    private ResourceLocation getLocation(ElementItem pItem) {
        Objects.requireNonNull(pItem.getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("fission/%s", pItem.getRegistryName().getPath()));
    }
}
