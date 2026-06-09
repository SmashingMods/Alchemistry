package com.smashingmods.alchemistry.client.recipe;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Collects the machine recipes NeoForge syncs to the client into {@link ClientRecipeStore} for the
 * recipe-selector GUI.
 *
 * <p>{@link RecipesReceivedEvent} fires on the client once recipe data has arrived from the server -- shortly
 * after entering the world and on every {@code /reload} -- carrying the recipe types the server requested via
 * {@link com.smashingmods.alchemistry.registry.RecipeSyncHandler}. The store is dropped on
 * {@link ClientPlayerNetworkEvent.LoggingOut} so the next world starts clean. Both events fire on the game
 * event bus, client side only.</p>
 */
@EventBusSubscriber(modid = Alchemistry.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class RecipeReceivedHandler {

    private RecipeReceivedHandler() {
    }

    @SubscribeEvent
    public static void onRecipesReceived(final RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();
        List<RecipeHolder<?>> recipes = new ArrayList<>();
        for (RecipeType<?> type : RecipeRegistry.recipeTypes()) {
            recipes.addAll(byType(recipeMap, type));
        }
        ClientRecipeStore.setRecipes(recipes);
        // Drop the by-type/by-group caches so the next read rebuilds from the recipes just synced.
        RecipeRegistry.clearCache();
    }

    @SubscribeEvent
    public static void onLoggingOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        ClientRecipeStore.clear();
        RecipeRegistry.clearCache();
    }

    // RecipeMap#byType is keyed on the recipe's own input/value types; a wildcard RecipeType erases those, so
    // the returned holders come back raw. The store keeps them as RecipeHolder<?> regardless -- RecipeRegistry
    // re-narrows per machine type when it reads -- so the wildcard recovery is safe here.
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Collection<RecipeHolder<?>> byType(RecipeMap recipeMap, RecipeType<?> type) {
        return (Collection<RecipeHolder<?>>) (Collection) recipeMap.byType((RecipeType) type);
    }
}
