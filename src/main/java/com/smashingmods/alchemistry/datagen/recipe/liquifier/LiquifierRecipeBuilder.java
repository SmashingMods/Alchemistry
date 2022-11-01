package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

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
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
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
        return input.getIngredient().getItems()[0].getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {

        ResourceLocation recipeLocation = new ResourceLocation(Alchemistry.MODID, String.format("liquifier/%s", pRecipeId.getPath()));
        ResourceLocation advancementLocation = new ResourceLocation(Alchemistry.MODID, String.format("recipes/liquifier/%s", pRecipeId.getPath()));

        pFinishedRecipeConsumer.accept(new LiquifierRecipeResult(
                group,
                advancementBuilder,
                recipeLocation,
                advancementLocation,
                input,
                output
        ));
    }
}
