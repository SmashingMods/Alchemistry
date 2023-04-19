package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class ToggleReactorAutoejectPacket implements AlchemyPacket {
    private final BlockPos blockPos;

    private final boolean autoeject;

    public ToggleReactorAutoejectPacket(BlockPos blockPos, boolean autoeject) {
        this.blockPos = blockPos;
        this.autoeject = autoeject;
    }

    public ToggleReactorAutoejectPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.autoeject = pBuffer.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(autoeject);
    }

    @Override
    public void handle(Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(blockPos);

            if (blockEntity instanceof AbstractReactorBlockEntity reactorController) {
                reactorController.setAutoeject(autoeject);
                if (autoeject) {
                    reactorController.tryEjectOutputs();
                }
                blockEntity.setChanged();
            }
        }
    }
}
