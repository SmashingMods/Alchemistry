package com.smashingmods.alchemistry.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class BlockEntityPacket {

    private final BlockPos blockPos;
    private final CompoundTag tag;

    public BlockEntityPacket(BlockPos pBlockPos, CompoundTag pTag) {
        this.blockPos = pBlockPos;
        this.tag = pTag;
    }

    public BlockEntityPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.tag = pBuffer.readNbt();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeNbt(tag);
    }

    public static void handle(final BlockEntityPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Level level = Objects.requireNonNull(Minecraft.getInstance().player).getLevel();
            BlockEntity blockEntity = level.getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity).load(pPacket.tag);
        });
        pContext.get().setPacketHandled(true);
    }
}
