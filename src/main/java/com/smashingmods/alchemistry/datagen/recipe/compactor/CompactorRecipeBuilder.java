package com.smashingmods.alchemistry.datagen.recipe.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.common.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

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
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, recipeId.getPath())))
                .requirements(RequirementsStrategy.OR);
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
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {

        Objects.requireNonNull(result.getItem().getItemCategory());

        String advancementPath = String.format("recipes/compactor/%s", pRecipeId.getPath());
        ResourceLocation recipeId = new ResourceLocation(Alchemistry.MODID, String.format("compactor/%s", pRecipeId.getPath()));
        ResourceLocation advancementId = new ResourceLocation(Alchemistry.MODID, advancementPath);

        pFinishedRecipeConsumer.accept(new CompactorRecipeResult(
                group,
                advancementBuilder,
                recipeId,
                advancementId,
                input,
                result
        ));
    }
}
