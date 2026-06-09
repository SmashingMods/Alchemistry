package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public class DissolverRecipe extends AbstractProcessingRecipe {

    private final IngredientStack input;
    private final ProbabilitySet output;

    public DissolverRecipe(ResourceLocation pId, String pGroup, IngredientStack pInput, ProbabilitySet pOutput) {
        super(pId, pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public DissolverRecipeSerializer<DissolverRecipe> getSerializer() {
        return RecipeRegistry.DISSOLVER_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RecipeRegistry.DISSOLVER_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        // Dissolver recipes are crafted in the machine, never placed through the vanilla recipe book grid.
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        // These recipes are not shown in the vanilla recipe book; use the catch-all crafting category.
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, output);
    }

    @Override
    public int compareTo(AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public DissolverRecipe copy() {
        return new DissolverRecipe(getId(), getGroup(), input.copy(), output.copy());
    }

    public IngredientStack getInput() {
        return input;
    }

    public ProbabilitySet getOutput() {
        return output;
    }

    public boolean matches(ItemStack pItemStack) {
        return input.matches(pItemStack);
    }
}