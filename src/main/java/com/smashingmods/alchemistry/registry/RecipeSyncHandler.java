package com.smashingmods.alchemistry.registry;

import net.neoforged.neoforge.event.OnDatapackSyncEvent;

/**
 * Asks the server to send Alchemistry's machine recipes to clients so the recipe-selector GUI can display
 * them.
 *
 * <p>The full recipe list is server-only, but NeoForge can sync chosen recipe types to clients on request:
 * {@link OnDatapackSyncEvent#sendRecipes} marks the machine types to send, and NeoForge delivers them on
 * player join and on {@code /reload} -- the same moments the vanilla recipe data is sent. The client receives
 * them through {@link net.neoforged.neoforge.client.event.RecipesReceivedEvent}
 * (see {@link com.smashingmods.alchemistry.client.recipe.RecipeReceivedHandler}). Registered on the game event
 * bus alongside {@link RecipeRegistry#postReload}.</p>
 */
public final class RecipeSyncHandler {

    private RecipeSyncHandler() {
    }

    public static void onDatapackSync(final OnDatapackSyncEvent event) {
        event.sendRecipes(RecipeRegistry.recipeTypes());
    }
}
