package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleAutoBalanceButtonPacket {

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


    public static void handle(final ToggleAutoBalanceButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            if (player != null) {
                AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

                if (blockEntity instanceof FusionControllerBlockEntity fusionController) {
                    fusionController.setAutoBalanced(pPacket.autoBalance);
                    fusionController.autoBalance();
                    fusionController.updateRecipe();
                    fusionController.setCanProcess(fusionController.canProcessRecipe());
                    blockEntity.setChanged();
                }
            }
        });
        pContext.get().setPacketHandled(true);
    }
}
