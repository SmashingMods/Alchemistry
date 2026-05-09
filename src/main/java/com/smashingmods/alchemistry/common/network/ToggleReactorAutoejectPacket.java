package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleReactorAutoejectPacket(BlockPos blockPos, boolean autoeject) implements AlchemyPacket {

    public static final Type<ToggleReactorAutoejectPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "toggle_reactor_autoeject"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleReactorAutoejectPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ToggleReactorAutoejectPacket::blockPos,
            ByteBufCodecs.BOOL, ToggleReactorAutoejectPacket::autoeject,
            ToggleReactorAutoejectPacket::new
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
            if (player.level().getBlockEntity(blockPos) instanceof AbstractReactorBlockEntity reactorController) {
                reactorController.setAutoeject(autoeject);
                if (autoeject) {
                    reactorController.tryEjectOutputs();
                }
                reactorController.setChanged();
            }
        });
    }
}
