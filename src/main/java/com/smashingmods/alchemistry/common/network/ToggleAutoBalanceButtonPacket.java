package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleAutoBalanceButtonPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(Alchemistry.MODID, "toggle_auto_balance_button");

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

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(autoBalance);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            if (player.level().getBlockEntity(blockPos) instanceof FusionControllerBlockEntity fusionController) {
                fusionController.setAutoBalanced(autoBalance);
                fusionController.autoBalance();
                fusionController.updateRecipe();
                fusionController.setCanProcess(fusionController.canProcessRecipe());
                fusionController.setChanged();
            }
        });
    }
}
