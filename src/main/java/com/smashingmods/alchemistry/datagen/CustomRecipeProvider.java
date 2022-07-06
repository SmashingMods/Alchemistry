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
import net.minecraft.data.recipes.RecipeProvider;
import java.util.function.Consumer;

public class CustomRecipeProvider extends RecipeProvider {

    public CustomRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pConsumer) {

        AtomizerRecipeProvider atomizerRecipeProvider = new AtomizerRecipeProvider(pConsumer);
        CompactorRecipeProvider compactorRecipeProvider = new CompactorRecipeProvider(pConsumer);
        CombinerRecipeProvider combinerRecipeProvider = new CombinerRecipeProvider(pConsumer);
        DissolverRecipeProvider dissolverRecipeProvider = new DissolverRecipeProvider(pConsumer);
        LiquifierRecipeProvider liquifierRecipeProvider = new LiquifierRecipeProvider(pConsumer);
        FissionRecipeProvider fissionRecipeProvider = new FissionRecipeProvider(pConsumer);
        FusionRecipeProvider fusionRecipeProvider = new FusionRecipeProvider(pConsumer);

        atomizerRecipeProvider.register();
        compactorRecipeProvider.register();
        combinerRecipeProvider.register();
        dissolverRecipeProvider.register();
        liquifierRecipeProvider.register();
        fissionRecipeProvider.register();
        fusionRecipeProvider.register();
    }
}