package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
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
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

public class LiquifierRecipeBuilder implements RecipeBuilder {

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

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
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

        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("liquifier/%s", pRecipeId.location().getPath()));
        ResourceLocation advancementLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("recipes/liquifier/%s", pRecipeId.location().getPath()));

        LiquifierRecipe recipe = new LiquifierRecipe(recipeLocation, group, input, output);
        pRecipeOutput.accept(ResourceKey.create(Registries.RECIPE, recipeLocation), recipe, advancementBuilder.build(advancementLocation));
    }
}
