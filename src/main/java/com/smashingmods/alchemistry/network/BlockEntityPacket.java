package com.smashingmods.alchemistry.network;

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

    public static void toBytes(BlockEntityPacket pPacket, FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(pPacket.blockPos);
        pBuffer.writeNbt(pPacket.tag);
    }

    public static BlockEntityPacket decode(FriendlyByteBuf pBuffer) {
        return new BlockEntityPacket(pBuffer.readBlockPos(), pBuffer.readNbt());
    }

    public static void handle(final BlockEntityPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Level level = Objects.requireNonNull(pContext.get().getSender()).level;
            BlockEntity blockEntity = level.getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity).load(pPacket.tag);
        });
        pContext.get().setPacketHandled(true);
    }
}