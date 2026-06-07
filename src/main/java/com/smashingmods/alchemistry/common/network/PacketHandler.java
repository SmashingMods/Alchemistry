package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.jei.*;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(RegisterPayloadHandlerEvent pEvent) {
        registrar(pEvent, Alchemistry.MODID);
        registerServerBound(ToggleAutoBalanceButtonPacket.ID, ToggleAutoBalanceButtonPacket::new);
        registerServerBound(SetRecipePacket.ID, SetRecipePacket::new);
        registerServerBound(CombinerTransferPacket.ID, CombinerTransferPacket::new);
        registerServerBound(CompactorTransferPacket.ID, CompactorTransferPacket::new);
        registerServerBound(DissolverTransferPacket.ID, DissolverTransferPacket::new);
        registerServerBound(FissionTransferPacket.ID, FissionTransferPacket::new);
        registerServerBound(FusionTransferPacket.ID, FusionTransferPacket::new);
        registerServerBound(LiquifierTransferPacket.ID, LiquifierTransferPacket::new);
        registerServerBound(ToggleReactorAutoejectPacket.ID, ToggleReactorAutoejectPacket::new);
        registerServerBound(SetSideConfigurationPacket.ID, SetSideConfigurationPacket::new);
    }
}
