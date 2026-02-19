package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSideConfigurationPacket(BlockPos blockPos, short sideConfigurationBits) implements CustomPacketPayload {
    public static final Type<SetSideConfigurationPacket> TYPE = new Type<>(Alchemistry.modLoc("set_side_configuration"));

    public static final StreamCodec<ByteBuf, SetSideConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetSideConfigurationPacket::blockPos,

            ByteBufCodecs.SHORT,
            SetSideConfigurationPacket::sideConfigurationBits,

            SetSideConfigurationPacket::new
    );

    public static void handle(SetSideConfigurationPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();

            Level level = player.level();
            BlockEntity blockEntity = level.getBlockEntity(packet.blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryEntity) {
                inventoryEntity.getCombinedSlotHandler().setSideModesFromShort(packet.sideConfigurationBits);
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
