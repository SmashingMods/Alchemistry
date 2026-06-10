package com.smashingmods.alchemistry.client.recipe;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.List;

/**
 * Client-side holder for the machine recipes the server syncs on join and on {@code /reload}.
 *
 * <p>The full recipe list is server-only -- {@code Level#getRecipeManager} was removed and the client's
 * {@code RecipeAccess} exposes only the placeable {@code RecipePropertySet}s and stonecutter recipes, not
 * arbitrary custom recipe types. Alchemistry's recipe-selector GUI still needs the machine recipes client-side
 * to display the picker, so the server is asked to send them (see
 * {@link com.smashingmods.alchemistry.registry.RecipeSyncHandler}) and the client collects them here from
 * {@link net.neoforged.neoforge.client.event.RecipesReceivedEvent}
 * (see {@link RecipeReceivedHandler}). {@link com.smashingmods.alchemistry.registry.RecipeRegistry} reads from
 * this store on the client instead of the (server-only) recipe manager.</p>
 */
public final class ClientRecipeStore {

    // volatile: written from the network thread (RecipesReceivedEvent handler, logout clear in RecipeReceivedHandler)
    // and read from the client/integrated-server threads, so the reference swap has to publish safely.
    private static volatile Collection<RecipeHolder<?>> recipes = List.of();

    private ClientRecipeStore() {
    }

    /**
     * Replaces the synced recipes with the collection the server just sent. Called from
     * {@link RecipeReceivedHandler} when the client finishes receiving recipe data.
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

    /**
     * Drops the synced recipes. Called when the player disconnects so the next world starts from a clean
     * store rather than the previous server's recipes.
     */
    public static void clear() {
        recipes = List.of();
    }
}
