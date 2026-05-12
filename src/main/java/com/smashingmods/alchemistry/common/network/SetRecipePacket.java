package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public record SetRecipePacket(BlockPos blockPos, ResourceLocation recipeId, String group) implements CustomPacketPayload {
    public static final Type<SetRecipePacket> TYPE = new Type<>(com.smashingmods.alchemistry.Alchemistry.modLoc("set_recipe"));

    public static final StreamCodec<ByteBuf, SetRecipePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetRecipePacket::blockPos,
            ResourceLocation.STREAM_CODEC,
            SetRecipePacket::recipeId,
            ByteBufCodecs.STRING_UTF8,
            SetRecipePacket::group,
            SetRecipePacket::new
    );

    public static void handle(SetRecipePacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();

            Objects.requireNonNull(player);
            Objects.requireNonNull(player.level());

            Level level = player.level();
            BlockEntity blockEntity = player.level().getBlockEntity(packet.blockPos);
            RecipeRegistry.getRecipeByGroupAndId(packet.group, packet.recipeId, level).ifPresent(recipe -> {
                if (blockEntity instanceof AbstractProcessingBlockEntity processingBlockEntity) {
                    processingBlockEntity.setProgress(0);
                    processingBlockEntity.setRecipe(recipe);
                    processingBlockEntity.setChanged();
                }
            });
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
