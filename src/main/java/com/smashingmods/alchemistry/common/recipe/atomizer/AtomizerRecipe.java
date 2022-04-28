package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class AtomizerRecipe extends ProcessingRecipe {

    public FluidStack input;
    public ItemStack output;

    public AtomizerRecipe(ResourceLocation pResourceLocation, String pGroup, FluidStack pInput, ItemStack pOutput) {
        super(RecipeRegistry.ATOMIZER_TYPE, pResourceLocation, pGroup, Ingredient.EMPTY, pOutput);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ATOMIZER_SERIALIZER.get();
    }
}