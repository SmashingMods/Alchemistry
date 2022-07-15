package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.smashingmods.alchemistry.common.recipe.AbstractAlchemistryRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

public class AtomizerRecipe extends AbstractAlchemistryRecipe {

    private final FluidStack input;
    private final ItemStack output;

    public AtomizerRecipe(ResourceLocation pId, String pGroup, FluidStack pInput, ItemStack pOutput) {
        super(pId, pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ATOMIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ATOMIZER_TYPE;
    }

    @Override
    public ItemStack assemble(Inventory pContainer) {
        return output;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(input.getFluid().getBucket()));
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, output);
    }

    public FluidStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}