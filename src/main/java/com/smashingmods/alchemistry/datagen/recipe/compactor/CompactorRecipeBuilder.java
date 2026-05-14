package com.smashingmods.alchemistry.datagen.recipe.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class CompactorRecipeBuilder {

    @Nullable
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

    public CompactorRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public CompactorRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("compactor/%s", recipeId.getPath()));
        save(pOutput, recipeLocation);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        save(pOutput, pId, new ICondition[0]);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId, ICondition... pConditions) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/compactor/"));

        CompactorRecipe recipe = new CompactorRecipe(group == null ? "compactor" : group, input, result);

        if (pConditions.length == 0) {
            pOutput.accept(pId, recipe, advHolder);
        } else {
            pOutput.accept(pId, recipe, advHolder, pConditions);
        }
    }
}
