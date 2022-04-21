package com.smashingmods.alchemistry.common.recipe;

import com.smashingmods.alchemistry.registry.SerializerRegistry;
import net.minecraft.core.NonNullList;
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
        super(SerializerRegistry.ATOMIZER_TYPE, pResourceLocation, pGroup, Ingredient.EMPTY, pOutput);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return SerializerRegistry.ATOMIZER_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }
}