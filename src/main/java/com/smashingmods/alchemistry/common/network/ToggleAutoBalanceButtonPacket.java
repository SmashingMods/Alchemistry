package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleAutoBalanceButtonPacket(BlockPos blockPos, boolean autoBalance) implements CustomPacketPayload {
    public static final Type<ToggleAutoBalanceButtonPacket> TYPE = new Type<>(Alchemistry.modLoc("toogle_auto_balance_button"));

    public static final StreamCodec<ByteBuf, ToggleAutoBalanceButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ToggleAutoBalanceButtonPacket::blockPos,

            ByteBufCodecs.BOOL,
            ToggleAutoBalanceButtonPacket::autoBalance,

            ToggleAutoBalanceButtonPacket::new
    );

    public static void handle(ToggleAutoBalanceButtonPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(packet.blockPos);

            if (blockEntity instanceof FusionControllerBlockEntity fusionController) {
                fusionController.setAutoBalanced(packet.autoBalance);
                fusionController.autoBalance();
                fusionController.updateRecipe();
                fusionController.setCanProcess(fusionController.canProcessRecipe());
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
