package com.smashingmods.alchemistry.common.recipe;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public abstract class ProcessingRecipe implements Recipe<Inventory> {

    protected final RecipeType<?> recipeType;
    protected final String group;
    protected final ItemStack output;
    protected final Ingredient input;
    public final ResourceLocation id;

    public ProcessingRecipe(RecipeType<?> pRecipeType, ResourceLocation pId, String pGroup, Ingredient pInput, ItemStack pOutput) {
        this.recipeType = pRecipeType;
        this.id = pId;
        this.group = pGroup;
        this.output = pOutput;
        this.input = pInput;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return recipeType;
    }

    @Override
    @Nonnull
    public String getGroup() {
        return group;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull Inventory pContainer) {
        return output.copy();
    }

    @Override
    public boolean matches(Inventory pContainer, @Nonnull Level pLevel) {
        return input.test(pContainer.getItem(0));
    }
}