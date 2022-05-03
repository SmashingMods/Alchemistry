package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.datagen.recipe.IngredientStack;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class DissolverRecipeBuilder implements RecipeBuilder {

    private String group;
    private final IngredientStack input;
    private final ProbabilitySet result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public DissolverRecipeBuilder(IngredientStack input, ProbabilitySet output) {
        this.input = input;
        this.result = output;
    }

    public static DissolverRecipeBuilder createRecipe(IngredientStack input, ProbabilitySet output) {
        return new DissolverRecipeBuilder(input, output);
    }

    @Override
    @Nonnull
    public RecipeBuilder unlockedBy(@Nonnull String pCriterionName, @Nonnull CriterionTriggerInstance pCriterionTrigger) {
        Item item = input.ingredient.getItems()[0].getItem();
        Objects.requireNonNull(item.getRegistryName());

        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, item.getRegistryName().getPath())))
                .requirements(RequirementsStrategy.OR);
        return this;
    }

    @Override
    @Nonnull
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    @Nonnull
    public Item getResult() {
        return input.ingredient.getItems()[0].getItem();
    }

    @Override
    public void save(@Nonnull Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        String advancementPath = String.format("recipes/dissolver/%s", pRecipeId.getPath());

        ResourceLocation recipeLocation = new ResourceLocation(Alchemistry.MODID, String.format("dissolver/%s", pRecipeId.getPath()));
        ResourceLocation advancementLocation = new ResourceLocation(Alchemistry.MODID, advancementPath);

        pFinishedRecipeConsumer.accept(new DissolverRecipeResult(
                group,
                advancementBuilder,
                recipeLocation,
                advancementLocation,
                input,
                result
        ));
    }
}
