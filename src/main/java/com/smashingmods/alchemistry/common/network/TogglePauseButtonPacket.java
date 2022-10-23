package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TogglePauseButtonPacket {

    private final BlockPos blockPos;
    private final boolean paused;

    public TogglePauseButtonPacket(BlockPos pBlockPos, boolean pPause) {
        this.blockPos = pBlockPos;
        this.paused = pPause;
    }

    public TogglePauseButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.paused = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(paused);
    }

    public static void handle(final TogglePauseButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            if (player != null) {
                AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

                if (blockEntity != null) {
                    blockEntity.setPaused(pPacket.paused);
                    blockEntity.setChanged();
                }
            }
        });
        pContext.get().setPacketHandled(true);
    }
}
