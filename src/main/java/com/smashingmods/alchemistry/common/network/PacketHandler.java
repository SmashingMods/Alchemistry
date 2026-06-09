package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(RegisterPayloadHandlersEvent pEvent) {
        registrar(pEvent, Alchemistry.MODID);
        registerServerBound(ToggleAutoBalanceButtonPacket.TYPE, ToggleAutoBalanceButtonPacket.STREAM_CODEC);
        registerServerBound(SetRecipePacket.TYPE, SetRecipePacket.STREAM_CODEC);
        // The machine recipe list is server-only at 1.21.3; the server sends this on join and `/reload`
        // so the recipe-selector GUI has recipes to display client-side.
        registerClientBound(SyncRecipesPacket.TYPE, SyncRecipesPacket.STREAM_CODEC);
        // The six recipe-transfer packets (common/network/jei/) are JEI-only -- each wraps a JEI
        // IRecipeTransferHandler and is sent only from the JEI plugin's "+" transfer button. JEI has no
        // 1.21.3 build, so that source is excluded for this hop and the packets have no remaining sender;
        // their registration is removed with them and restored at 1.21.4 alongside the JEI integration.
        // NeoForge payload registration is type-keyed (by each packet's CustomPacketPayload.Type id), not a
        // sequential index, so dropping these does not shift the identity of the packets that remain.
        registerServerBound(ToggleReactorAutoejectPacket.TYPE, ToggleReactorAutoejectPacket.STREAM_CODEC);
        registerServerBound(SetSideConfigurationPacket.TYPE, SetSideConfigurationPacket.STREAM_CODEC);
    }
}
