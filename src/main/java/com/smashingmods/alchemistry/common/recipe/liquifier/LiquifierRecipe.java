package com.smashingmods.alchemistry.common.recipe.liquifier;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class LiquifierRecipe extends ProcessingRecipe {

    public ItemStack input;
    public FluidStack output;

    public LiquifierRecipe(ResourceLocation pId, String pGroup, ItemStack pInput, FluidStack pOutput) {
        super(RecipeRegistry.LIQUIFIER_TYPE, pId, pGroup, Ingredient.of(pInput), ItemStack.EMPTY);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.LIQUIFIER_SERIALIZER.get();
    }
}