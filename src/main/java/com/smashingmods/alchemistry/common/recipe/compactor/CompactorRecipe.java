package com.smashingmods.alchemistry.common.recipe.compactor;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;

public class CompactorRecipe extends ProcessingRecipe {

    private final ItemStack input;
    private final ItemStack output;

    public CompactorRecipe(ResourceLocation pId, String pGroup, ItemStack pInput, ItemStack pOutput) {
        super(RecipeRegistry.COMPACTOR_TYPE, pId, pGroup, Ingredient.of(pInput), pOutput);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COMPACTOR_SERIALIZER.get();
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}
