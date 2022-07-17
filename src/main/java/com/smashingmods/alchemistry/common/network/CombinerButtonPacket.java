package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import java.util.Objects;
import java.util.function.Supplier;

public class CombinerButtonPacket {

    private final BlockPos blockPos;
    private final boolean lock;
    private final boolean pause;

    public CombinerButtonPacket(BlockPos pBlockPos, boolean pLock, boolean pPause) {
        this.blockPos = pBlockPos;
        this.lock = pLock;
        this.pause = pPause;
    }

    public CombinerButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.lock = pBuffer.readBoolean();
        this.pause = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(lock);
        pBuffer.writeBoolean(pause);
    }

    public static void handle(final CombinerButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            Objects.requireNonNull(player);

            CombinerBlockEntity blockEntity = (CombinerBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

            Objects.requireNonNull(blockEntity);

            blockEntity.setRecipeLocked(pPacket.lock);
            blockEntity.setPaused(pPacket.pause);
            blockEntity.setChanged();
        });
        pContext.get().setPacketHandled(true);
    }
}
