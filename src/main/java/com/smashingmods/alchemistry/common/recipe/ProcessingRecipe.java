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

    protected final RecipeType<?> type;
    //private final RecipeSerializer<?> serializer;
    protected final String group;
    protected final ItemStack output1;
    protected final Ingredient input1;
    public final ResourceLocation id;

    public ProcessingRecipe(RecipeType<?> recipeType, ResourceLocation id, String group, Ingredient input, ItemStack output) {
        this.type = recipeType;
        this.id = id;
        this.group = group;
        this.output1 = output;
        this.input1 = input;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    @Nonnull
    public String getGroup() {
        return this.group;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return this.output1;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull Inventory inv) {
        return this.output1.copy();
    }

    @Override
    public boolean matches(Inventory inv, @Nonnull Level worldIn) {
        return this.input1.test(inv.getItem(0));
    }
}