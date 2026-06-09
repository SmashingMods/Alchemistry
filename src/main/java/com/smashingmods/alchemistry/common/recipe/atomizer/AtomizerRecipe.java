package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
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
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class AtomizerRecipe extends AbstractProcessingRecipe {

    private final FluidStack input;
    private final ItemStack output;

    public AtomizerRecipe(ResourceLocation pId, String pGroup, FluidStack pInput, ItemStack pOutput) {
        super(pId, pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return RecipeRegistry.ATOMIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RecipeRegistry.ATOMIZER_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        // Atomizer recipes are crafted in the machine, never placed through the vanilla recipe book grid.
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
    public AtomizerRecipe copy() {
        return new AtomizerRecipe(getId(), getGroup(), input.copy(), output.copy());
    }

    public FluidStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}