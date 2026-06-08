package com.smashingmods.alchemistry.datagen.recipe.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;

public class CompactorRecipeBuilder implements RecipeBuilder {

    private String group;
    private final IngredientStack input;
    private final ItemStack result;
    private final ResourceLocation recipeId;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public CompactorRecipeBuilder(IngredientStack pInput, ItemStack pResult, ResourceLocation pRecipeId) {
        this.input = pInput;
        this.result = pResult;
        this.recipeId = pRecipeId;
    }

    public static CompactorRecipeBuilder createRecipe(IngredientStack pInput, ItemStack pOutput, ResourceLocation pRecipeId) {
        return new CompactorRecipeBuilder(pInput, pOutput, pRecipeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, recipeId.getPath())))
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
        String advancementPath = String.format("recipes/compactor/%s", pRecipeId.getPath());
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("compactor/%s", pRecipeId.getPath()));
        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, advancementPath);

        CompactorRecipe recipe = new CompactorRecipe(recipeId, group, input, result);
        pRecipeOutput.accept(recipeId, recipe, advancementBuilder.build(advancementId));
    }
}
