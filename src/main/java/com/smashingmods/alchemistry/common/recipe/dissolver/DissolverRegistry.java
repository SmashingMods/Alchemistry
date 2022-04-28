package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DissolverRegistry {

    private static List<DissolverRecipe> recipes = null;
    private static HashMap<ItemStack, DissolverRecipe> cache = new HashMap<>();

    public static List<DissolverRecipe> getRecipes(Level world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeRegistry.DISSOLVER_TYPE)
                    .map(x -> (DissolverRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }

    @Nullable
    public static DissolverRecipe match(Level world,ItemStack input, boolean quantitySensitive) {
        DissolverRecipe cachedRecipe = cache.getOrDefault(input, null);
        if (cachedRecipe != null) return cachedRecipe;

        for (DissolverRecipe recipe : getRecipes(world)) {
            if (recipe.inputIngredient != null) {
                for (ItemStack recipeStack : recipe.inputIngredient.ingredient.getItems().clone()) {
                    if (ItemStack.isSame(recipeStack, input)) {
                        // && (input.itemDamage == recipeStack.itemDamage
                        //|| recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                        if (quantitySensitive && input.getCount() >= recipeStack.getCount() || !quantitySensitive) {
                            cachedRecipe = recipe.copy();
                            cache.put(input, cachedRecipe);
                            return cachedRecipe;
                        }
                    }
                }
            } else {
                //TODO handle borked recipes
            }
        }
        return null;
    }
}