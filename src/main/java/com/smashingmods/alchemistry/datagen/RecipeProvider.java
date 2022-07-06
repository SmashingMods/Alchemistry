package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.datagen.recipe.atomizer.AtomizerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.compactor.CompactorRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.dissolver.DissolverRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.fission.FissionRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.fusion.FusionRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.liquifier.LiquifierRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pConsumer) {
        AtomizerRecipeProvider.register(pConsumer);
        CompactorRecipeProvider.register(pConsumer);
        CombinerRecipeProvider.register(pConsumer);
        DissolverRecipeProvider.register(pConsumer);
        LiquifierRecipeProvider.register(pConsumer);
        FissionRecipeProvider.register(pConsumer);
        FusionRecipeProvider.register(pConsumer);
    }
}