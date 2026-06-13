package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleReactorAutoejectPacket(BlockPos blockPos, boolean autoeject) implements CustomPacketPayload {
    public static final Type<ToggleReactorAutoejectPacket> TYPE = new Type<>(Alchemistry.modLoc("toogle_reactor_auto_eject"));

    public static final StreamCodec<ByteBuf, ToggleReactorAutoejectPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ToggleReactorAutoejectPacket::blockPos,

            ByteBufCodecs.BOOL,
            ToggleReactorAutoejectPacket::autoeject,

            ToggleReactorAutoejectPacket::new
    );

    public static void handle(ToggleReactorAutoejectPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(packet.blockPos);

            if (blockEntity instanceof AbstractReactorBlockEntity reactorController) {
                reactorController.setAutoeject(packet.autoeject);
                if (packet.autoeject) {
                    reactorController.tryEjectOutputs();
                }
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
