package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.smashingmods.alchemistry.Alchemistry;
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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CombinerRecipeBuilder implements RecipeBuilder {

    private String group;
    private final ResourceLocation recipeId;
    private final List<ItemStack> input;
    private final ItemStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public CombinerRecipeBuilder(ItemStack pOutput, List<ItemStack> pInput, ResourceLocation pRecipeId) {
        this.result = pOutput;
        this.input = pInput;
        this.recipeId = pRecipeId;
    }

    public static CombinerRecipeBuilder createRecipe(ItemStack pResult, List<ItemStack> pInput, ResourceLocation pRecipeId) {
        return new CombinerRecipeBuilder(pResult, pInput, pRecipeId);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        Objects.requireNonNull(result.getItem().getRegistryName());
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
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

        String advancementPath = String.format("recipes/combiner/%s", pRecipeId.getPath());

        ResourceLocation recipeLocation = new ResourceLocation(Alchemistry.MODID, String.format("combiner/%s", pRecipeId.getPath()));
        ResourceLocation advancementLocation = new ResourceLocation(Alchemistry.MODID, advancementPath);

        pFinishedRecipeConsumer.accept(new CombinerRecipeResult(
                group,
                advancementBuilder,
                recipeLocation,
                advancementLocation,
                input,
                result
        ));
    }
}
