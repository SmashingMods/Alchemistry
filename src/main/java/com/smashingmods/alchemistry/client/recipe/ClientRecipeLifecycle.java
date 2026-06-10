package com.smashingmods.alchemistry.client.recipe;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Clears the client-side machine recipes when the player leaves a server. The server pushes its recipes into
 * {@link ClientRecipeStore} on join (see {@link com.smashingmods.alchemistry.common.network.SyncRecipesPacket}),
 * but nothing dropped them on logout, so the previous server's recipes -- carrying its stamped ids -- would
 * linger across the title screen into a later or failed reconnect. {@link ClientPlayerNetworkEvent.LoggingOut}
 * fires whenever the local player disconnects, so wiping the store and the {@link RecipeRegistry} caches here
 * leaves the next join to repopulate from a clean slate.
 */
@EventBusSubscriber(modid = Alchemistry.MODID, value = Dist.CLIENT)
public final class ClientRecipeLifecycle {

    private ClientRecipeLifecycle() {
    }

    @SubscribeEvent
    public static void onLoggingOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        ClientRecipeStore.clear();
        // Drop the by-type/by-group caches so a stale read can't outlive the recipes they were built from.
        RecipeRegistry.clearCache();
    }
}
