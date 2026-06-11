package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetRecipePacket implements AlchemyPacket {

    public static final Type<SetRecipePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "set_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetRecipePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ResourceLocation.STREAM_CODEC, packet -> packet.recipeId,
            ByteBufCodecs.STRING_UTF8, packet -> packet.group,
            SetRecipePacket::new
    );

    private final BlockPos blockPos;
    private final ResourceLocation recipeId;
    private final String group;

    public SetRecipePacket(BlockPos pBlockPos, ResourceLocation pRecipeId, String pGroup) {
        this.blockPos = pBlockPos;
        this.recipeId = pRecipeId;
        this.group = pGroup;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        applyRecipeSelection(pContext.player().level(), blockPos, group, recipeId);
    }

    /**
     * Resolves the recipe by group and id and records it as the player's selection on the machine at the
     * given position (see {@link AbstractProcessingBlockEntity#selectRecipe}). This is the whole server-side
     * selection path; the network handler delegates here so gametests can drive the real path without a
     * payload context. A failed lookup is logged rather than dropped silently -- the client only ever sends
     * ids from the synced recipe list, so a miss means the client and server recipe views disagree.
     */
    public static void applyRecipeSelection(Level pLevel, BlockPos pBlockPos, String pGroup, ResourceLocation pRecipeId) {
        if (pLevel.getBlockEntity(pBlockPos) instanceof AbstractProcessingBlockEntity processingBlockEntity) {
            RecipeRegistry.getRecipeByGroupAndId(pGroup, pRecipeId, pLevel).ifPresentOrElse(
                    processingBlockEntity::selectRecipe,
                    () -> Alchemistry.LOGGER.warn("Ignoring recipe selection {} for the machine at {}: no recipe with group {} and that id is loaded",
                            pRecipeId, pBlockPos, pGroup));
        }
    }
}
