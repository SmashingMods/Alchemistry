package com.smashingmods.alchemistry.datagen.recipe.fusion;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.smashingmods.alchemylib.datagen.DatagenHelpers.getLocation;

public class FusionRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public FusionRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new FusionRecipeProvider(pConsumer).register();
    }

    private void register() {
        List<ElementItem> elements = ItemRegistry.getElements();

        for (int x = 1; x < elements.size(); x++) {
            for (int y = 1; y < elements.size(); y++) {
                if (!(x > y) && (x + y) <= elements.size()) {
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
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput, "fusion", Alchemistry.MODID)))
                .save(consumer);
    }
}
