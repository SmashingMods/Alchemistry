package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class AtomizerRecipeBuilder {

    @Nullable
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

    public AtomizerRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public AtomizerRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("atomizer/%s", recipeId.getPath()));
        save(pOutput, recipeLocation);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        save(pOutput, pId, new ICondition[0]);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId, ICondition... pConditions) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/atomizer/"));

        AtomizerRecipe recipe = new AtomizerRecipe(group == null ? "atomizer" : group, input, result);

        if (pConditions.length == 0) {
            pOutput.accept(pId, recipe, advHolder);
        } else {
            pOutput.accept(pId, recipe, advHolder, pConditions);
        }
    }
}
