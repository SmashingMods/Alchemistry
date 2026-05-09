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
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleAutoBalanceButtonPacket(BlockPos blockPos, boolean autoBalance) implements AlchemyPacket {

    public static final Type<ToggleAutoBalanceButtonPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "toggle_auto_balance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleAutoBalanceButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ToggleAutoBalanceButtonPacket::blockPos,
            ByteBufCodecs.BOOL, ToggleAutoBalanceButtonPacket::autoBalance,
            ToggleAutoBalanceButtonPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            if (player == null) return;
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
