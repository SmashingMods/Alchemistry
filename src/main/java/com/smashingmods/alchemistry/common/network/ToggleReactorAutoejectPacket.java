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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ToggleReactorAutoejectPacket implements AlchemyPacket {

    public static final Type<ToggleReactorAutoejectPacket> TYPE = new Type<>(new ResourceLocation(Alchemistry.MODID, "toggle_reactor_autoeject"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleReactorAutoejectPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.BOOL, packet -> packet.autoeject,
            ToggleReactorAutoejectPacket::new
    );

    private final BlockPos blockPos;

    private final boolean autoeject;

    public ToggleReactorAutoejectPacket(BlockPos blockPos, boolean autoeject) {
        this.blockPos = blockPos;
        this.autoeject = autoeject;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        if (pContext.player().level().getBlockEntity(blockPos) instanceof AbstractReactorBlockEntity reactorController) {
            reactorController.setAutoeject(autoeject);
            if (autoeject) {
                reactorController.tryEjectOutputs();
            }
            reactorController.setChanged();
        }
    }
}
