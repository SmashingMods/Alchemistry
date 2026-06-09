package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.recipe.ClientRecipeStore;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Server-to-client packet carrying Alchemistry's machine recipes so the recipe-selector GUI can display
 * them. At 1.21.3 the recipe list is server-only, so the server sends this on player join and on
 * {@code /reload} (see {@link com.smashingmods.alchemistry.registry.RecipeSyncHandler}); the client stores
 * the recipes in {@link ClientRecipeStore}, which {@link RecipeRegistry} reads from.
 *
 * <p>The recipes travel as {@link RecipeHolder}s -- each holder pairs the recipe with its identity key, which
 * is no longer part of the serialized recipe itself -- so the client can re-stamp the real id onto every
 * recipe exactly as the server-side path does.</p>
 */
public class SyncRecipesPacket implements AlchemyPacket {

    public static final Type<SyncRecipesPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "sync_recipes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRecipesPacket> STREAM_CODEC = StreamCodec.composite(
            RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), packet -> packet.recipes,
            SyncRecipesPacket::new
    );

    private final List<RecipeHolder<?>> recipes;

    public SyncRecipesPacket(List<RecipeHolder<?>> pRecipes) {
        this.recipes = pRecipes;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        ClientRecipeStore.setRecipes(recipes);
        // Drop the by-type/by-group caches so the next read rebuilds from the recipes just synced.
        RecipeRegistry.clearCache();
    }
}
