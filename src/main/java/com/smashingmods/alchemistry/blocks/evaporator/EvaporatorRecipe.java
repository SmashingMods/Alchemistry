package com.smashingmods.alchemistry.blocks.evaporator;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemistry.misc.ProcessingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatorRecipe extends ProcessingRecipe {

    public FluidStack input;
    public ItemStack output;
    FluidTags n;

    public EvaporatorRecipe(ResourceLocation id, String group, FluidStack input, ItemStack output) {
        super(Registration.EVAPORATOR_TYPE,id,group, Ingredient.EMPTY,output);
        this.input = input;
        this.output = output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.EVAPORATOR_SERIALIZER.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }
}