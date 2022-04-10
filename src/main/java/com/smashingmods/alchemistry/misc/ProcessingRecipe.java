package com.smashingmods.alchemistry.misc;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

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
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output1;
    }


    @Override
    public ItemStack assemble(Inventory inv) {
        return this.output1.copy();
    }


    @Override
    public boolean matches(Inventory inv, Level worldIn) {
        return this.input1.test(inv.getItem(0));
    }
}