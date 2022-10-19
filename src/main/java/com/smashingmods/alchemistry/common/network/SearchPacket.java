package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SearchPacket {

    private final BlockPos blockPos;
    private final String searchText;

    public SearchPacket(BlockPos pBlockPos, String pSearchText) {
        this.blockPos = pBlockPos;
        this.searchText = pSearchText;
    }

    public SearchPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.searchText = pBuffer.readUtf();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeUtf(searchText);
    }

    public static void handle(final SearchPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            if (player != null) {
                AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

                if (blockEntity != null) {
                    blockEntity.setSearchText(pPacket.searchText);
                    blockEntity.setChanged();
                }
            }
        });
        pContext.get().setPacketHandled(true);
    }
}
