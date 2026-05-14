package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
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
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class LiquifierRecipeBuilder {

    @Nullable
    private String group;
    private final ResourceLocation recipeId;
    private final IngredientStack input;
    private final FluidStack output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public LiquifierRecipeBuilder(IngredientStack pInput, FluidStack pOutput, ResourceLocation pRecipeId) {
        this.input = pInput;
        this.output = pOutput;
        this.recipeId = pRecipeId;
    }

    public static LiquifierRecipeBuilder createRecipe(IngredientStack pInput, FluidStack pOutput, ResourceLocation pRecipeId) {
        return new LiquifierRecipeBuilder(pInput, pOutput, pRecipeId);
    }

    public LiquifierRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public LiquifierRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("liquifier/%s", recipeId.getPath()));
        save(pOutput, recipeLocation);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        save(pOutput, pId, new ICondition[0]);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId, ICondition... pConditions) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/liquifier/"));

        LiquifierRecipe recipe = new LiquifierRecipe(group == null ? "liquifier" : group, input, output);

        if (pConditions.length == 0) {
            pOutput.accept(pId, recipe, advHolder);
        } else {
            pOutput.accept(pId, recipe, advHolder, pConditions);
        }
    }
}
