package com.smashingmods.alchemistry.api.blockentity.container;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemistry.api.recipe.ProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.stream.Collectors;

public interface RecipeScreen<B extends AbstractProcessingBlockEntity, R extends ProcessingRecipe > {

    void setupRecipeList();

    void resetDisplayedRecipes();

    LinkedList<ProcessingRecipe> getDisplayedRecipes();

    B getBlockEntity();

    default void searchRecipeList(String pKeyword) {
        getDisplayedRecipes().clear();

        LinkedList<ProcessingRecipe> recipes = getBlockEntity().getAllRecipes().stream()
            .filter(recipe -> {

                Pair<ResourceLocation, String> searchablePair = RecipeDisplayUtil.getSearchablePair(recipe);
                ResourceLocation registryName = searchablePair.getLeft();
                String description = searchablePair.getRight();

                if (pKeyword.charAt(0) == '@') {
                    boolean space = pKeyword.contains(" ");
                    boolean namespace = registryName.getNamespace().contains(space ? pKeyword.split(" ")[0] : pKeyword.substring(1));
                    boolean path = registryName.getPath().contains(pKeyword.split(" ")[1]);
                    if (space) {
                        return namespace && path;
                    } else {
                        return namespace;
                    }
                }

                return description.toLowerCase().contains(pKeyword.toLowerCase());
            })
                .collect(Collectors.toCollection(LinkedList::new));

        getDisplayedRecipes().addAll(recipes);
    }

    default boolean isValidRecipeIndex(int pSlot) {
        return pSlot >= 0 && pSlot < getDisplayedRecipes().size();
    }
}
