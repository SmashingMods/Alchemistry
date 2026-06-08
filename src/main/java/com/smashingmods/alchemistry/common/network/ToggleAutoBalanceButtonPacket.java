package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleAutoBalanceButtonPacket implements AlchemyPacket {

    public static final Type<ToggleAutoBalanceButtonPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "toggle_auto_balance_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleAutoBalanceButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.BOOL, packet -> packet.autoBalance,
            ToggleAutoBalanceButtonPacket::new
    );

    private final BlockPos blockPos;
    private final boolean autoBalance;

    public ToggleAutoBalanceButtonPacket(BlockPos pBlockPos, boolean pBalance) {
        this.blockPos = pBlockPos;
        this.autoBalance = pBalance;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        if (pContext.player().level().getBlockEntity(blockPos) instanceof FusionControllerBlockEntity fusionController) {
            fusionController.setAutoBalanced(autoBalance);
            fusionController.autoBalance();
            fusionController.updateRecipe();
            fusionController.setCanProcess(fusionController.canProcessRecipe());
            fusionController.setChanged();
        }
    }
}
