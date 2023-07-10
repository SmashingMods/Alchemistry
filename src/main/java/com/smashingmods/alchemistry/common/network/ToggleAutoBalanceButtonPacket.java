package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class ToggleAutoBalanceButtonPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final boolean autoBalance;

    public ToggleAutoBalanceButtonPacket(BlockPos pBlockPos, boolean pBalance) {
        this.blockPos = pBlockPos;
        this.autoBalance = pBalance;
    }

    public ToggleAutoBalanceButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.autoBalance = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(autoBalance);
    }

    public void handle(NetworkEvent.Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(blockPos);

            if (blockEntity instanceof FusionControllerBlockEntity fusionController) {
                fusionController.setAutoBalanced(autoBalance);
                fusionController.autoBalance();
                fusionController.updateRecipe();
                fusionController.setCanProcess(fusionController.canProcessRecipe());
                blockEntity.setChanged();
            }
        }
    }
}
