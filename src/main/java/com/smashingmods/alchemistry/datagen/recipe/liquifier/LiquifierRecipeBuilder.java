package com.smashingmods.alchemistry.datagen.recipe.liquifier;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class LiquifierRecipeBuilder implements RecipeBuilder {

    private String group;
    private final ItemStack input;
    private final FluidStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public LiquifierRecipeBuilder(ItemStack pInput, FluidStack pResult) {
        this.input = pInput;
        this.result = pResult;
    }

    public static LiquifierRecipeBuilder createRecipe(ItemStack pInput, FluidStack pResult) {
        return new LiquifierRecipeBuilder(pInput, pResult);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getFluid().getBucket())).getPath())))
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
        return input.getItem();
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
                result
        ));
    }
}
