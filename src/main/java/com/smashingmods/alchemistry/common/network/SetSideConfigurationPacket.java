package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class SetSideConfigurationPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final short sideConfigurationBits;

    public SetSideConfigurationPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readShort());
    }

    public SetSideConfigurationPacket(BlockPos blockPos, short sideConfigurationBits) {
        this.blockPos = blockPos;
        this.sideConfigurationBits = sideConfigurationBits;
    }

    @Override
    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeShort(sideConfigurationBits);
    }

    @Override
    public void handle(Context pContext) {
        Player player = pContext.getSender();
        if (player == null) {
            return;
        }

        Level level = player.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof InventoryBlockEntity inventoryEntity) {
            inventoryEntity.getCombinedSlotHandler().setSideModesFromShort(sideConfigurationBits);
            blockEntity.setChanged();
        }
    }

}
