package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.common.block.oldblocks.dissolver.DissolverTagData;
import com.smashingmods.alchemistry.datagen.recipe.atomizer.AtomizerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.compactor.CompactorRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.dissolver.DissolverRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.fission.FissionRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.liquifier.LiquifierRecipeProvider;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.google.common.collect.Sets;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CustomRecipeProvider extends RecipeProvider {

    public static List<DissolverTagData> metalTagData = new ArrayList<>();
    public static List<String> metals = new ArrayList<>();

    public CustomRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> pConsumer) {

        initMetals();

        AtomizerRecipeProvider atomizerRecipeProvider = new AtomizerRecipeProvider(pConsumer);
        CompactorRecipeProvider compactorRecipeProvider = new CompactorRecipeProvider(pConsumer);
        CombinerRecipeProvider combinerRecipeProvider = new CombinerRecipeProvider(pConsumer);
        DissolverRecipeProvider dissolverRecipeProvider = new DissolverRecipeProvider(pConsumer);
        LiquifierRecipeProvider liquifierRecipeProvider = new LiquifierRecipeProvider(pConsumer);
        FissionRecipeProvider fissionRecipeProvider = new FissionRecipeProvider(pConsumer);

        atomizerRecipeProvider.register();
        compactorRecipeProvider.register();
        combinerRecipeProvider.register();
        dissolverRecipeProvider.register();
        liquifierRecipeProvider.register();
        fissionRecipeProvider.register();
    }

    private void initMetals() {
        metals.addAll(ItemRegistry.getElements().stream()
                .filter(element -> element.getMetalType().equals(MetalType.METAL))
                .map(ElementItem::getChemicalName).collect(Collectors.toList()));

        metalTagData.add(new DissolverTagData("ingots", 16, metals));
        metalTagData.add(new DissolverTagData("ores", 32, metals));
        metalTagData.add(new DissolverTagData("dusts", 16, metals));
        metalTagData.add(new DissolverTagData("storage_blocks", 144, metals));
        metalTagData.add(new DissolverTagData("nuggets", 1, metals));
        metalTagData.add(new DissolverTagData("plates", 16, metals));
    }
}