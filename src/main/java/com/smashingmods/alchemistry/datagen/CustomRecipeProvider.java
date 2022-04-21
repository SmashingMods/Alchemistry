package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.common.block.oldblocks.dissolver.DissolverTagData;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.dissolver.DissolverRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.fission.FissionRecipeProvider;
import com.smashingmods.alchemistry.common.recipe.ProbabilitySet;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import com.google.common.collect.Sets;
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
        CombinerRecipeProvider combinerRecipeProvider = new CombinerRecipeProvider(pConsumer);
        DissolverRecipeProvider dissolverRecipeProvider = new DissolverRecipeProvider(pConsumer);
        FissionRecipeProvider fissionRecipeProvider = new FissionRecipeProvider(pConsumer);

        combinerRecipeProvider.register(pConsumer);
        dissolverRecipeProvider.register(pConsumer);
        fissionRecipeProvider.register(pConsumer);
    }

    public static ProbabilitySet.Builder set() {
        return new ProbabilitySet.Builder();
    }

    private void initMetals() {
        Set<Integer> nonMetals = Sets.newHashSet(1, 2, 6, 7, 8, 9, 10, 15, 16, 17, 18, 35, 36, 53, 54, 80, 86);
        //noinspection SimplifyStreamApiCallChains
        metals.addAll(ElementRegistry.elements.values().stream()
                .filter(it -> !nonMetals.contains(it.atomicNumber))
                .map(it -> it.internalName).collect(Collectors.toList()));

        //metalTagData.add(new DissolverTagData("ingots", 16, metals));
        metalTagData.add(new DissolverTagData("ores", 32, metals));
        metalTagData.add(new DissolverTagData("dusts", 16, metals));
        metalTagData.add(new DissolverTagData("storage_blocks", 144, metals));
        metalTagData.add(new DissolverTagData("nuggets", 1, metals));
        metalTagData.add(new DissolverTagData("plates", 16, metals));
    }
}