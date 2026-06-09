package com.smashingmods.alchemistry.common.recipe.compactor;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import javax.annotation.Nonnull;

public class CompactorRecipe extends AbstractProcessingRecipe {

    private final IngredientStack input;
    private final ItemStack output;

    public CompactorRecipe(ResourceLocation pId, String pGroup, IngredientStack pInput, ItemStack pOutput) {
        super(pId, pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return RecipeRegistry.COMPACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RecipeRegistry.COMPACTOR_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        // Compactor recipes are crafted in the machine, never placed through the vanilla recipe book grid.
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        // These recipes are not shown in the vanilla recipe book; use the catch-all crafting category.
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, output);
    }

    @Override
    public int compareTo(@Nonnull AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public CompactorRecipe copy() {
        return new CompactorRecipe(getId(), getGroup(), input.copy(), output.copy());
    }

    public IngredientStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    @SuppressWarnings("unused")
    public boolean matches(ItemStack pItemStack) {
        return input.matches(pItemStack);
    }
}
