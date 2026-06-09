package com.smashingmods.alchemistry.datagen.recipe.fusion;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.Optional;

import static com.smashingmods.alchemylib.datagen.DatagenHelpers.getLocation;

public class FusionRecipeProvider {

    private final RecipeOutput consumer;

    public FusionRecipeProvider(RecipeOutput pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(RecipeOutput pConsumer) {
        new FusionRecipeProvider(pConsumer).register();
    }

    private void register() {
        List<ElementItem> elements = ItemRegistry.getElements();

        for (int x = 1; x < elements.size(); x++) {
            for (int y = 1; y < elements.size(); y++) {
                if (isValidFusion(x, y, elements.size())) {
                    Optional<ElementItem> input1 = ItemRegistry.getElementByAtomicNumber(x);
                    Optional<ElementItem> input2 = ItemRegistry.getElementByAtomicNumber(y);
                    Optional<ElementItem> output = ItemRegistry.getElementByAtomicNumber(fusionOutput(x, y));

                    if (input1.isPresent() && input2.isPresent() && output.isPresent()) {
                        fusion(input1.get(), input2.get(), output.get());
                    }
                }
            }
        }
    }

    /**
     * A fusion pair is valid when the inputs are ordered (to avoid mirror duplicates) and their combined
     * atomic number still maps to an existing element.
     */
    public static boolean isValidFusion(int x, int y, int count) {
        return !(x > y) && (x + y) <= count;
    }

    public static int fusionOutput(int x, int y) {
        return x + y;
    }

    private void fusion(ElementItem pInput1, ElementItem pInput2, ElementItem pOutput) {
        FusionRecipeBuilder.createRecipe(pInput1, pInput2, pOutput)
                .group("fusion")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(ResourceKey.create(Registries.RECIPE, getLocation(pOutput, "fusion", Alchemistry.MODID))))
                .save(consumer);
    }
}
