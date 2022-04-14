package com.smashingmods.alchemistry.block.atomizer;

import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.misc.ProcessingRecipe;
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

    public AtomizerRecipe(ResourceLocation id, String group, FluidStack input, ItemStack output) {
        super(Registry.ATOMIZER_TYPE, id, group, Ingredient.EMPTY, output);
        this.input = input;
        this.output = output;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Registry.ATOMIZER_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }
}