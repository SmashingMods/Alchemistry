package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.network.jei.CombinerTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.CompactorTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.DissolverTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FissionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FusionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.LiquifierTransferPacket;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(PayloadRegistrar registrar) {
        registrar.playToServer(ToggleAutoBalanceButtonPacket.TYPE, ToggleAutoBalanceButtonPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(SetRecipePacket.TYPE, SetRecipePacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(SetSideConfigurationPacket.TYPE, SetSideConfigurationPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(ToggleReactorAutoejectPacket.TYPE, ToggleReactorAutoejectPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(CombinerTransferPacket.TYPE, CombinerTransferPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(CompactorTransferPacket.TYPE, CompactorTransferPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(DissolverTransferPacket.TYPE, DissolverTransferPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(FissionTransferPacket.TYPE, FissionTransferPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(FusionTransferPacket.TYPE, FusionTransferPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(LiquifierTransferPacket.TYPE, LiquifierTransferPacket.STREAM_CODEC, AlchemyPacket::handle);
    }
}
