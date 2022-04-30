package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class CombinerRecipe extends ProcessingRecipe implements Comparable<CombinerRecipe> {

    public final ItemStack output;
    public final List<ItemStack> inputs = new ArrayList<>();
    public final Set<Integer> nonEmptyIndices = new HashSet<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, List<ItemStack> pInputList, ItemStack pOutput) {
        super(RecipeRegistry.COMBINER_TYPE, pId, pGroup, Ingredient.EMPTY, ItemStack.EMPTY);

        output = pOutput;

        for (int index = 0; index < 4; index++) {

            ItemStack temp;

            if (pInputList.size() > index) {
                temp = pInputList.get(index);
            } else {
                temp = null;
            }

            if (temp != null) {
                inputs.add(temp.copy());
            } else {
                inputs.add(ItemStack.EMPTY);
            }
            if (!inputs.get(index).isEmpty()) {
                nonEmptyIndices.add(index);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("input=[%s],output=[%s]", inputs, output);
    }

    public boolean matchesHandlerStacks(CustomStackHandler pItemStackHandler) {

        int matchingStacks = 0;

        List<ItemStack> itemStacks = pItemStackHandler.getSlotsAsList().subList(0, 4).stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        List<ItemStack> inputStacks = inputs.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());

        if (inputStacks.size() == itemStacks.size()) {
            for (ItemStack inputStack : inputStacks) {
                for (ItemStack itemStack : itemStacks) {
                    if (ItemStack.isSameItemSameTags(inputStack, itemStack) && itemStack.getCount() >= inputStack.getCount()) {
                        matchingStacks++;
                    }
                }
            }
            return matchingStacks == inputStacks.size();
        }
        return false;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COMBINER_SERIALIZER.get();
    }

    @Override
    public int compareTo(@NotNull CombinerRecipe pRecipe) {
        Objects.requireNonNull(this.output.getItem().getRegistryName());
        Objects.requireNonNull(pRecipe.output.getItem().getRegistryName());
        return this.output.getItem().getRegistryName().compareNamespaced(pRecipe.output.getItem().getRegistryName());
    }
}
