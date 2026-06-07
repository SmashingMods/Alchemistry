package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetSideConfigurationPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(Alchemistry.MODID, "set_side_configuration");

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
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeShort(sideConfigurationBits);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            Level level = player.level();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof InventoryBlockEntity inventoryEntity) {
                inventoryEntity.getCombinedSlotHandler().setSideModesFromShort(sideConfigurationBits);
                blockEntity.setChanged();
            }
        });
    }

}
