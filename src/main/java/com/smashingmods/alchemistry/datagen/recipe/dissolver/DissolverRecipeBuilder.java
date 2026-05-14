package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class DissolverRecipeBuilder {

    @Nullable
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

    public DissolverRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public DissolverRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("dissolver/%s", recipeId.getPath()));
        save(pOutput, recipeLocation);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        save(pOutput, pId, new ICondition[0]);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId, ICondition... pConditions) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/dissolver/"));

        DissolverRecipe recipe = new DissolverRecipe(group == null ? "dissolver" : group, input, result);

        if (pConditions.length == 0) {
            pOutput.accept(pId, recipe, advHolder);
        } else {
            pOutput.accept(pId, recipe, advHolder, pConditions);
        }
    }
}
