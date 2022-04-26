package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.registry.SerializerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinerRecipe extends ProcessingRecipe {

    public final ItemStack output;
    public final List<ItemStack> inputs = new ArrayList<>();
    public final Set<Integer> nonEmptyIndices = new HashSet<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, List<ItemStack> pInputList, ItemStack pOutput) {
        super(SerializerRegistry.COMBINER_TYPE, pId, pGroup, Ingredient.EMPTY, ItemStack.EMPTY);

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

        List<ItemStack> itemStacks = pItemStackHandler.getSlotsAsList().subList(0, 4);

        for (int index = 0; index < inputs.size(); index++) {

            ItemStack recipeStack = inputs.get(index);
            ItemStack handlerStack = itemStacks.get(index);

            if (handlerStack.isEmpty() && recipeStack.isEmpty()) {
                matchingStacks++;
            } else if (ItemStack.isSameItemSameTags(recipeStack, handlerStack)
                    && handlerStack.getCount() >= recipeStack.getCount()) {
                matchingStacks++;
            }
        }
        return matchingStacks == 4;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return SerializerRegistry.COMBINER_SERIALIZER.get();
    }
}
