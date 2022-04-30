package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CombinerRegistry {

    private static List<CombinerRecipe> recipes;
    private static final HashMap<List<ItemStack>, CombinerRecipe> inputCache = new HashMap<>();
    private static final HashMap<ItemStack, CombinerRecipe> outputCache = new HashMap<>();

    public static List<CombinerRecipe> getRecipes(Level pLevel) {
        if (recipes == null) {
            recipes = pLevel.getRecipeManager().getRecipes().stream()
                    .filter(recipe -> recipe.getType() == RecipeRegistry.COMBINER_TYPE)
                    .map(recipe -> (CombinerRecipe) recipe)
                    .collect(Collectors.toList());
        }
        return recipes;
    }

    public static CombinerRecipe matchOutput(Level world, ItemStack stack) {
        CombinerRecipe cacheResult = outputCache.getOrDefault(stack, null);
        if (cacheResult != null) return cacheResult;

        CombinerRecipe recipe = getRecipes(world).stream()
                .filter(it -> it.output.getItem() == stack.getItem())
                .filter(it -> ItemStack.isSame(it.output, stack))
                .findFirst()
                .orElse(null);
        outputCache.put(stack, recipe);
        return recipe;
    }

    public static CombinerRecipe matchInputs(Level pLevel, CustomStackHandler pItemHandler) {
        return matchInputs(pLevel, pItemHandler.getSlotsAsList().subList(0, 4));
    }

    private static CombinerRecipe matchInputs(Level pLevel, List<ItemStack> pItemStackList) {
        CombinerRecipe cachedResult = inputCache.get(pItemStackList);

        if (cachedResult != null) {
            return cachedResult;
        } else {
            return getRecipes(pLevel).stream().filter(recipe -> {

                List<Item> inputList = new ArrayList<>();

                for (ItemStack itemStack : pItemStackList) {
                    if (itemStack != ItemStack.EMPTY) {
                        inputList.add(itemStack.getItem());
                    }
                }

                List<Item> recipeList = new ArrayList<>();
                for (ItemStack itemStack : recipe.inputs) {
                    if (itemStack != ItemStack.EMPTY) {
                        recipeList.add(itemStack.getItem());
                    }
                }

                boolean test = inputList.containsAll(recipeList);

                if (test) {
                    inputCache.put(pItemStackList, recipe);
                }
                return test;
            }).findFirst().orElse(null);
        }
    }
}