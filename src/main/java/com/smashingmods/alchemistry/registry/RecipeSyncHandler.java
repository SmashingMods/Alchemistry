package com.smashingmods.alchemistry.registry;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.SyncRecipesPacket;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.List;
import java.util.Set;

/**
 * Sends Alchemistry's machine recipes to clients so the recipe-selector GUI can display them.
 *
 * <p>At 1.21.3 the full recipe list is server-only, and NeoForge 21.3.96 has no native opt-in to sync extra
 * recipe types. {@link OnDatapackSyncEvent} fires on player join and on {@code /reload} -- the same moments
 * the vanilla recipe data is sent -- so it pushes the machine recipes to the affected players, who store them
 * in {@link com.smashingmods.alchemistry.client.recipe.ClientRecipeStore}. Registered on the game event bus
 * alongside {@link RecipeRegistry#postReload}.</p>
 */
public final class RecipeSyncHandler {

    private RecipeSyncHandler() {
    }

    public static void onDatapackSync(final OnDatapackSyncEvent event) {
        Set<RecipeType<?>> types = RecipeRegistry.recipeTypes();
        List<RecipeHolder<?>> recipes = event.getPlayerList().getServer().getRecipeManager().getRecipes().stream()
                .filter(holder -> types.contains(holder.value().getType()))
                .toList();
        event.getRelevantPlayers().forEach(player -> Alchemistry.PACKET_HANDLER.sendToPlayer(new SyncRecipesPacket(recipes), player));
    }
}
