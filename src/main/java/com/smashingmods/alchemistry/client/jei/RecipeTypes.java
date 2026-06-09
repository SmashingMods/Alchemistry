package com.smashingmods.alchemistry.client.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import mezz.jei.api.recipe.types.IRecipeType;

public class RecipeTypes {
    public static final IRecipeType<AtomizerRecipe> ATOMIZER = IRecipeType.create(Alchemistry.MODID, "atomizer", AtomizerRecipe.class);
    public static final IRecipeType<CombinerRecipe> COMBINER = IRecipeType.create(Alchemistry.MODID, "combiner", CombinerRecipe.class);
    public static final IRecipeType<CompactorRecipe> COMPACTOR = IRecipeType.create(Alchemistry.MODID, "compactor", CompactorRecipe.class);
    public static final IRecipeType<DissolverRecipe> DISSOLVER = IRecipeType.create(Alchemistry.MODID, "dissolver", DissolverRecipe.class);
    public static final IRecipeType<FissionRecipe> FISSION = IRecipeType.create(Alchemistry.MODID, "fission", FissionRecipe.class);
    public static final IRecipeType<FusionRecipe> FUSION = IRecipeType.create(Alchemistry.MODID, "fusion", FusionRecipe.class);
    public static final IRecipeType<LiquifierRecipe> LIQUIFIER = IRecipeType.create(Alchemistry.MODID, "liquifier", LiquifierRecipe.class);
}
