package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;

public class CombinerRecipe implements Recipe<Inventory>, Comparable<CombinerRecipe> {

    private final ResourceLocation recipeId;
    private final String group;
    private final ItemStack output;
    private final List<ItemStack> input = new ArrayList<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, List<ItemStack> pInputList, ItemStack pOutput) {
        this.recipeId = pId;
        this.group = pGroup;
        this.output = pOutput;

        for (ItemStack itemStack : pInputList) {
            input.add(itemStack.copy());
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COMBINER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.COMBINER_TYPE;
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
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public int compareTo(@NotNull CombinerRecipe pRecipe) {
        Objects.requireNonNull(this.output.getItem().getRegistryName());
        Objects.requireNonNull(pRecipe.output.getItem().getRegistryName());
        return this.output.getItem().getRegistryName().compareNamespaced(pRecipe.output.getItem().getRegistryName());
    }

    @Override
    public String toString() {
        return String.format("input=[%s],output=[%s]", input, output);
    }

    public List<ItemStack> getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean matchInputs(CustomItemStackHandler pHandler) {

        int matchingStacks = 0;

        List<ItemStack> handlerStacks = pHandler.getStacks().stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        List<ItemStack> recipeStacks = input.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());

        if (recipeStacks.size() == handlerStacks.size()) {
            for (ItemStack recipeStack : recipeStacks) {
                for (ItemStack handlerStack : handlerStacks) {
                    if (ItemStack.isSameItemSameTags(recipeStack, handlerStack) && handlerStack.getCount() >= recipeStack.getCount()) {
                        matchingStacks++;
                    }
                }
            }
            return matchingStacks == recipeStacks.size();
        }
        return false;
    }
}
