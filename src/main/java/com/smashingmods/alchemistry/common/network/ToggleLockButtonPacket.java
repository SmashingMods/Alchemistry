package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleLockButtonPacket {

    private final BlockPos blockPos;
    private final boolean locked;

    public ToggleLockButtonPacket(BlockPos pBlockPos, boolean pLock) {
        this.blockPos = pBlockPos;
        this.locked = pLock;
    }

    public ToggleLockButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.locked = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(locked);
    }

    public static void handle(final ToggleLockButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            if (player != null) {
                AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

                if (blockEntity != null) {
                    blockEntity.setRecipeLocked(pPacket.locked);
                    blockEntity.setChanged();
                }
            }
        });
        pContext.get().setPacketHandled(true);
    }
}
