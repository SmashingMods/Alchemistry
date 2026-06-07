package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

public class AtomizerRecipeBuilder implements RecipeBuilder {

    private String group;
    private final FluidStack input;
    private final ItemStack result;
    private final ResourceLocation recipeId;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public AtomizerRecipeBuilder(FluidStack pInput, ItemStack pResult, ResourceLocation pRecipeId) {
        this.input = pInput;
        this.result = pResult;
        this.recipeId = pRecipeId;
    }

    public static AtomizerRecipeBuilder createRecipe(FluidStack pInput, ItemStack pResult, ResourceLocation pRecipeId) {
        return new AtomizerRecipeBuilder(pInput, pResult, pRecipeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, recipeId.getPath())))
                .requirements(AdvancementRequirements.Strategy.OR);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pRecipeId) {
        String advancementPath = String.format("recipes/atomizer/%s", pRecipeId.getPath());

        ResourceLocation recipeLocation = new ResourceLocation(Alchemistry.MODID, String.format("atomizer/%s", pRecipeId.getPath()));
        ResourceLocation advancementLocation = new ResourceLocation(Alchemistry.MODID, advancementPath);

        AtomizerRecipe recipe = new AtomizerRecipe(recipeLocation, group, input, result);
        pRecipeOutput.accept(recipeLocation, recipe, advancementBuilder.build(advancementLocation));
    }
}
