package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class CombinerRecipeBuilder {

    @Nullable
    private String group;
    private final ResourceLocation recipeId;
    private final List<IngredientStack> input;
    private final ItemStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public CombinerRecipeBuilder(ItemStack pOutput, List<IngredientStack> pInput, ResourceLocation pRecipeId) {
        this.result = pOutput;
        this.input = new ArrayList<>(pInput);
        this.recipeId = pRecipeId;
    }

    public static CombinerRecipeBuilder createRecipe(ItemStack pOutput, List<IngredientStack> pInput, ResourceLocation pRecipeId) {
        return new CombinerRecipeBuilder(pOutput, pInput, pRecipeId);
    }

    public CombinerRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public CombinerRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("combiner/%s", recipeId.getPath()));
        save(pOutput, recipeLocation);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        save(pOutput, pId, new ICondition[0]);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId, ICondition... pConditions) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/combiner/"));

        CombinerRecipe recipe = new CombinerRecipe(group == null ? "combiner" : group, input, result);

        if (pConditions.length == 0) {
            pOutput.accept(pId, recipe, advHolder);
        } else {
            pOutput.accept(pId, recipe, advHolder, pConditions);
        }
    }
}
