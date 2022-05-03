package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;

public class DissolverRecipe extends ProcessingRecipe {

    public final ItemStack input;
    public boolean reversible = false;
    public ProbabilitySet output;

    public DissolverRecipe(ResourceLocation pId, String pGroup, ItemStack pInput, ProbabilitySet pOutput) {
        super(RecipeRegistry.DISSOLVER_TYPE, pId, pGroup, Ingredient.EMPTY, ItemStack.EMPTY);

        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DISSOLVER_SERIALIZER.get();
    }

    @Override
    public String toString(){
        return "input=" + input.toString() + "\treversible=" + reversible + "\toutputs=" + output.toString();
    }

    public DissolverRecipe copy() {
        return new DissolverRecipe(this.id, this.group, this.input, this.output);
    }
}