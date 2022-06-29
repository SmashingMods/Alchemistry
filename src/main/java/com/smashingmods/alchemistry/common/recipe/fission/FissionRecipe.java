package com.smashingmods.alchemistry.common.recipe.fission;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class FissionRecipe implements Recipe<Inventory> {

    private final ResourceLocation recipeId;
    private final String group;
    private final ItemStack input;
    private final List<ItemStack> output;

    public FissionRecipe(ResourceLocation pId, String pGroup, ItemStack pInput, ItemStack pOutput1, ItemStack pOutput2) {
        this(pId, pGroup, pInput, Arrays.asList(pOutput1, pOutput2));
    }

    private FissionRecipe(ResourceLocation pId, String pGroup, ItemStack pInput, List<ItemStack> pOutput) {
        this.recipeId = pId;
        this.group = pGroup;
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FISSION_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return RecipeRegistry.FISSION_TYPE;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    @Nonnull
    public String getGroup() {
        return group;
    }

    @Override
    public boolean matches(@Nonnull Inventory pContainer, @Nonnull Level pLevel) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull Inventory pContainer) {
        return output.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(input));
    }

    public ItemStack getInput() {
        return input;
    }

    public List<ItemStack> getOutput() {
        return output;
    }
}
