package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.api.blockentity.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ProcessingButtonPacket {

    private final BlockPos blockPos;
    private final boolean lock;
    private final boolean pause;

    public ProcessingButtonPacket(BlockPos pBlockPos, boolean pLock, boolean pPause) {
        this.blockPos = pBlockPos;
        this.lock = pLock;
        this.pause = pPause;
    }

    public ProcessingButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.lock = pBuffer.readBoolean();
        this.pause = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(lock);
        pBuffer.writeBoolean(pause);
    }

    public static void handle(final ProcessingButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            Objects.requireNonNull(player);

            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

            Objects.requireNonNull(blockEntity);

            blockEntity.setRecipeLocked(pPacket.lock);
            blockEntity.setPaused(pPacket.pause);
            blockEntity.setChanged();
        });
        pContext.get().setPacketHandled(true);
    }
}
