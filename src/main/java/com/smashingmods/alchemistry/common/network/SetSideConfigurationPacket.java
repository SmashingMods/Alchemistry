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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetSideConfigurationPacket implements AlchemyPacket {

    public static final Type<SetSideConfigurationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "set_side_configuration"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSideConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.SHORT, packet -> packet.sideConfigurationBits,
            SetSideConfigurationPacket::new
    );

    private final BlockPos blockPos;
    private final short sideConfigurationBits;

    public SetSideConfigurationPacket(BlockPos blockPos, short sideConfigurationBits) {
        this.blockPos = blockPos;
        this.sideConfigurationBits = sideConfigurationBits;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        Level level = pContext.player().level();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof InventoryBlockEntity inventoryEntity) {
            inventoryEntity.getCombinedSlotHandler().setSideModesFromShort(sideConfigurationBits);
            blockEntity.setChanged();
        }
    }

}
