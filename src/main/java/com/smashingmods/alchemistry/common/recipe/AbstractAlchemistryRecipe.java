package com.smashingmods.alchemistry.common.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class AbstractAlchemistryRecipe implements Recipe<Inventory> {

    private final ResourceLocation recipeId;
    private final String group;

    public AbstractAlchemistryRecipe(ResourceLocation pRecipeId, String pGroup) {
        this.recipeId = pRecipeId;
        this.group = pGroup;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public boolean matches(Inventory pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(Inventory pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }
}
