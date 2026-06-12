package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.jei.*;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(RegisterPayloadHandlersEvent pEvent) {
        registrar(pEvent, Alchemistry.MODID);
        registerServerBound(ToggleAutoBalanceButtonPacket.TYPE, ToggleAutoBalanceButtonPacket.STREAM_CODEC);
        registerServerBound(SetRecipePacket.TYPE, SetRecipePacket.STREAM_CODEC);
        // The six recipe-transfer packets (common/network/jei/) are the JEI "+"-button senders -- each is
        // sent by the TransferHandler the JEI plugin wires per machine. NeoForge refuses to send a payload
        // that was never registered: sendToServer throws UnsupportedOperationException, JEI catches it
        // inside the transfer handler, and the "+" click dies silently -- a missing line here is an
        // invisibly dead button, not a crash. (These were unregistered for the JEI-less 1.21.3 hop and
        // missed on restore, which shipped exactly that dead button.) PacketHandlerTest pins every
        // sendToServer payload to a server-bound registration.
        registerServerBound(CombinerTransferPacket.TYPE, CombinerTransferPacket.STREAM_CODEC);
        registerServerBound(CompactorTransferPacket.TYPE, CompactorTransferPacket.STREAM_CODEC);
        registerServerBound(DissolverTransferPacket.TYPE, DissolverTransferPacket.STREAM_CODEC);
        registerServerBound(FissionTransferPacket.TYPE, FissionTransferPacket.STREAM_CODEC);
        registerServerBound(FusionTransferPacket.TYPE, FusionTransferPacket.STREAM_CODEC);
        registerServerBound(LiquifierTransferPacket.TYPE, LiquifierTransferPacket.STREAM_CODEC);
        registerServerBound(ToggleReactorAutoejectPacket.TYPE, ToggleReactorAutoejectPacket.STREAM_CODEC);
        registerServerBound(SetSideConfigurationPacket.TYPE, SetSideConfigurationPacket.STREAM_CODEC);
    }
}
