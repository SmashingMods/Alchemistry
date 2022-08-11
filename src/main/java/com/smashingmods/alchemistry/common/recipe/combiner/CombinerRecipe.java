package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.AbstractAlchemistryRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CombinerRecipe extends AbstractAlchemistryRecipe implements Comparable<CombinerRecipe> {

    private final ItemStack output;
    private final List<ItemStack> input = new ArrayList<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, List<ItemStack> pInputList, ItemStack pOutput) {
        super(pId, pGroup);
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
    public ItemStack assemble(Inventory pContainer) {
        return output;
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

    public boolean matchInputs(List<ItemStack> pStacks) {
        int matchingStacks = 0;

        List<ItemStack> handlerStacks = pStacks.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        List<ItemStack> recipeStacks = input.stream().filter(itemStack -> !itemStack.isEmpty()).toList();

        if (recipeStacks.size() == handlerStacks.size()) {
            for (ItemStack handlerStack : handlerStacks) {
                for (ItemStack recipeStack : recipeStacks) {
                    if (ItemStack.isSameItemSameTags(recipeStack, handlerStack) && handlerStack.getCount() >= recipeStack.getCount()) {
                        matchingStacks++;
                        break;
                    }
                }
            }
            return matchingStacks == recipeStacks.size();
        }
        return false;
    }

    public boolean matchInputs(CustomItemStackHandler pHandler) {
        return matchInputs(pHandler.getStacks());
    }
}
