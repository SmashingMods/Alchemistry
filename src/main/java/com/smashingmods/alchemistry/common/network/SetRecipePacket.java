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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetRecipePacket(BlockPos blockPos, ResourceLocation recipeId, String group) implements AlchemyPacket {

    public static final Type<SetRecipePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "set_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetRecipePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetRecipePacket::blockPos,
            ResourceLocation.STREAM_CODEC, SetRecipePacket::recipeId,
            ByteBufCodecs.STRING_UTF8, SetRecipePacket::group,
            SetRecipePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            if (player == null) return;
            Level level = player.level();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            RecipeRegistry.getRecipeByGroupAndId(group, recipeId, level).ifPresent(recipe -> {
                if (blockEntity instanceof AbstractProcessingBlockEntity processingBlockEntity) {
                    processingBlockEntity.setProgress(0);
                    processingBlockEntity.setRecipe(recipe);
                    processingBlockEntity.setChanged();
                }
            });
        });
    }
}
