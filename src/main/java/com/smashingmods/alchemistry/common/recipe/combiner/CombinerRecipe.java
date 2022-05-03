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
    public final List<ItemStack> input = new ArrayList<>();
    public final Set<Integer> nonEmptyIndices = new HashSet<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, List<ItemStack> pInputList, ItemStack pOutput) {
        super(RecipeRegistry.COMBINER_TYPE, pId, pGroup, Ingredient.EMPTY, ItemStack.EMPTY);

        this.output = pOutput;

        for (int index = 0; index < 4; index++) {

            ItemStack temp;

            if (pInputList.size() > index) {
                temp = pInputList.get(index);
            } else {
                temp = null;
            }

            if (temp != null) {
                input.add(temp.copy());
            } else {
                input.add(ItemStack.EMPTY);
            }
            if (!input.get(index).isEmpty()) {
                nonEmptyIndices.add(index);
            }
        }
    }

    public boolean matchInputs(CustomStackHandler pHandler) {

        int matchingStacks = 0;

        List<ItemStack> handlerStacks = pHandler.getSlotsAsList().subList(0, 4).stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
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

    @Override
    public String toString() {
        return String.format("input=[%s],output=[%s]", input, output);
    }
}
