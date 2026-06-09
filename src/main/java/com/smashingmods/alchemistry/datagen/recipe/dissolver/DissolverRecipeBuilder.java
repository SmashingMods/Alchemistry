package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import javax.annotation.Nullable;

public class DissolverRecipeBuilder implements RecipeBuilder {

    private String group;
    private final ResourceLocation recipeId;
    private final IngredientStack input;
    private final ProbabilitySet result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public DissolverRecipeBuilder(IngredientStack input, ProbabilitySet output, ResourceLocation pRecipeId) {
        this.input = input;
        this.result = output;
        this.recipeId = pRecipeId;
    }

    public static DissolverRecipeBuilder createRecipe(IngredientStack input, ProbabilitySet output, ResourceLocation pRecipeId) {
        return new DissolverRecipeBuilder(input, output, pRecipeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(ResourceKey.create(Registries.RECIPE, recipeId)))
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
        return input.getIngredient().items().getFirst().value();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pRecipeId) {
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("dissolver/%s", pRecipeId.location().getPath()));
        ResourceLocation advancementLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("recipes/dissolver/%s", pRecipeId.location().getPath()));

        DissolverRecipe recipe = new DissolverRecipe(recipeLocation, group, input, result);
        pRecipeOutput.accept(ResourceKey.create(Registries.RECIPE, recipeLocation), recipe, advancementBuilder.build(advancementLocation));
    }
}
