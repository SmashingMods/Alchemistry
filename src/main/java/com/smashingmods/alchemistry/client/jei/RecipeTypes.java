package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import mezz.jei.api.recipe.RecipeType;

public class RecipeTypes {
    public static final RecipeType<AtomizerRecipe> ATOMIZER = RecipeType.create(Alchemistry.MODID, "atomizer", AtomizerRecipe.class);
    public static final RecipeType<CombinerRecipe> COMBINER = RecipeType.create(Alchemistry.MODID, "combiner", CombinerRecipe.class);
    public static final RecipeType<CompactorRecipe> COMPACTOR = RecipeType.create(Alchemistry.MODID, "compactor", CompactorRecipe.class);
    public static final RecipeType<DissolverRecipe> DISSOLVER = RecipeType.create(Alchemistry.MODID, "dissolver", DissolverRecipe.class);
    public static final RecipeType<FissionRecipe> FISSION = RecipeType.create(Alchemistry.MODID, "fission", FissionRecipe.class);
    public static final RecipeType<FusionRecipe> FUSION = RecipeType.create(Alchemistry.MODID, "fusion", FusionRecipe.class);
    public static final RecipeType<LiquifierRecipe> LIQUIFIER = RecipeType.create(Alchemistry.MODID, "liquifier", LiquifierRecipe.class);
}
