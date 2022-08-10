package com.smashingmods.alchemistry.datagen.recipe.atomizer;

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

public class AtomizerRecipeBuilder implements RecipeBuilder {

    private String group;
    private final FluidStack input;
    private final ItemStack result;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public AtomizerRecipeBuilder(FluidStack pInput, ItemStack pResult) {
        this.input = pInput;
        this.result = pResult;
    }

    public static AtomizerRecipeBuilder createRecipe(FluidStack pInput, ItemStack pResult) {
        return new AtomizerRecipeBuilder(pInput, pResult);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result.getItem())).getPath())))
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

        Objects.requireNonNull(this.result.getItem().getItemCategory());

        String advancementPath = String.format("recipes/atomizer/%s", pRecipeId.getPath());

        ResourceLocation recipeLocation = new ResourceLocation(Alchemistry.MODID, String.format("atomizer/%s", pRecipeId.getPath()));
        ResourceLocation advancementLocation = new ResourceLocation(Alchemistry.MODID, advancementPath);

        pFinishedRecipeConsumer.accept(new AtomizerRecipeResult(
                group,
                advancementBuilder,
                recipeLocation,
                advancementLocation,
                input,
                result
        ));
    }
}
