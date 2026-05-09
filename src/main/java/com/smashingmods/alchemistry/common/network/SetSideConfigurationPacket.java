package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSideConfigurationPacket(BlockPos blockPos, short sideConfigurationBits) implements AlchemyPacket {

    public static final Type<SetSideConfigurationPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "set_side_configuration"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSideConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetSideConfigurationPacket::blockPos,
            ByteBufCodecs.SHORT, SetSideConfigurationPacket::sideConfigurationBits,
            SetSideConfigurationPacket::new
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
            BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryEntity) {
                inventoryEntity.getCombinedSlotHandler().setSideModesFromShort(sideConfigurationBits);
                blockEntity.setChanged();
            }
        });
    }
}
