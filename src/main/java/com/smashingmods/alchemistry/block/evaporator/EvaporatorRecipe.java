package com.smashingmods.alchemistry.block.evaporator;

import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.misc.ProcessingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class EvaporatorRecipe extends ProcessingRecipe {

    public FluidStack input;
    public ItemStack output;
    FluidTags n;

    public EvaporatorRecipe(ResourceLocation id, String group, FluidStack input, ItemStack output) {
        super(Registry.EVAPORATOR_TYPE,id,group, Ingredient.EMPTY,output);
        this.input = input;
        this.output = output;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Registry.EVAPORATOR_SERIALIZER.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }
}