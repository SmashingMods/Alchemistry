package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.smashingmods.alchemistry.Alchemistry;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinerRecipeBuilder implements RecipeBuilder {

    private String group;
    private final ResourceLocation recipeId;
    private final Set<IngredientStack> input;
    private final ItemStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public CombinerRecipeBuilder(ItemStack pOutput, List<IngredientStack> pInput, ResourceLocation pRecipeId) {
        this.result = pOutput;
        this.input = new HashSet<>(pInput);
        this.recipeId = pRecipeId;
    }

    public static CombinerRecipeBuilder createRecipe(ItemStack pOutput, List<IngredientStack> pInput, ResourceLocation pRecipeId) {
        return new CombinerRecipeBuilder(pOutput, pInput, pRecipeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
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
        String advancementPath = String.format("recipes/combiner/%s", pRecipeId.getPath());

        ResourceLocation recipeLocation = new ResourceLocation(Alchemistry.MODID, String.format("combiner/%s", pRecipeId.getPath()));
        ResourceLocation advancementLocation = new ResourceLocation(Alchemistry.MODID, advancementPath);

        pRecipeOutput.accept(new CombinerRecipeResult(
                group,
                advancementBuilder,
                recipeLocation,
                advancementLocation,
                input,
                result
        ));
    }
}
