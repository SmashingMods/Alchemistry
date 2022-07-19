package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CompactorResetPacket {

    BlockPos blockPos;

    public CompactorResetPacket(BlockPos pBlockPos) {
        this.blockPos = pBlockPos;
    }

    public CompactorResetPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
    }

    public static void handle(final CompactorResetPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);
            CompactorBlockEntity blockEntity = (CompactorBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);
            blockEntity.setRecipe(null);
            blockEntity.setTarget(ItemStack.EMPTY);
        });
        pContext.get().setPacketHandled(true);
    }
}
