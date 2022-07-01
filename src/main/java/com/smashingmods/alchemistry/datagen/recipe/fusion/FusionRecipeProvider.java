package com.smashingmods.alchemistry.datagen.recipe.fusion;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.function.Consumer;

public class FusionRecipeProvider {

    Consumer<FinishedRecipe> consumer;

    public FusionRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public void register() {
        List<ElementItem> elements = ItemRegistry.getElements();

        for (int x = 1; x < elements.size(); x++) {
            for (int y = 1; y < elements.size(); y++) {
                if ((x + y) < elements.size()) {
                    Optional<ElementItem> input1 = ItemRegistry.getElementByAtomicNumber(x);
                    Optional<ElementItem> input2 = ItemRegistry.getElementByAtomicNumber(y);
                    Optional<ElementItem> output = ItemRegistry.getElementByAtomicNumber(x + y);

                    if (input1.isPresent() && input2.isPresent() && output.isPresent()) {
                        fusion(input1.get(), input2.get(), output.get());
                    }
                }
            }
        }
    }

    private void fusion(ElementItem pInput1, ElementItem pInput2, ElementItem pOutput) {
        FusionRecipeBuilder.createRecipe(pInput1, pInput2, pOutput)
                .group("fusion")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput)))
                .save(consumer);
    }

    private ResourceLocation getLocation(Item pItem) {
        Objects.requireNonNull(pItem.getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("fusion/%s", pItem.getRegistryName().getPath()));
    }
}
