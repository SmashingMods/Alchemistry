package com.smashingmods.alchemistry.client.recipe;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.List;

/**
 * Client-side holder for the machine recipes the server sends on join and on {@code /reload}.
 *
 * <p>At 1.21.3 the full recipe list is server-only: {@code Level#getRecipeManager} was removed and the
 * client's {@code RecipeAccess} exposes only the placeable {@code RecipePropertySet}s and stonecutter
 * recipes, not arbitrary custom recipe types. Alchemistry's recipe-selector GUI still needs the machine
 * recipes client-side to display the picker, so the server pushes them here through
 * {@link com.smashingmods.alchemistry.common.network.SyncRecipesPacket}. {@link com.smashingmods.alchemistry.registry.RecipeRegistry}
 * reads from this store on the client instead of the (server-only) recipe manager.</p>
 */
public final class ClientRecipeStore {

    private static Collection<RecipeHolder<?>> recipes = List.of();

    private ClientRecipeStore() {
    }

    /**
     * Replaces the synced recipes with the collection the server just sent. Called from the client handler
     * of {@link com.smashingmods.alchemistry.common.network.SyncRecipesPacket}.
     */
    public static void setRecipes(Collection<RecipeHolder<?>> pRecipes) {
        recipes = List.copyOf(pRecipes);
    }

    /**
     * Returns the recipes the server last synced. Empty until the first sync arrives, so callers behave as
     * though no machine recipes exist rather than failing.
     */
    public static Collection<RecipeHolder<?>> getRecipes() {
        return recipes;
    }
}
