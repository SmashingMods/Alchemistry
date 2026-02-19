package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.jei.CombinerTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.CompactorTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FissionTransferPacket;
import com.smashingmods.alchemistry.common.network.jei.FusionTransferPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Alchemistry.MODID)
public class PacketHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);
        
        registrar.playToServer(ToggleAutoBalanceButtonPacket.TYPE, ToggleAutoBalanceButtonPacket.STREAM_CODEC, ToggleAutoBalanceButtonPacket::handle);
        // SetRecipePacket
        registrar.playToServer(CombinerTransferPacket.TYPE, CombinerTransferPacket.STREAM_CODEC, CombinerTransferPacket::handle);
        registrar.playToServer(CompactorTransferPacket.TYPE, CompactorTransferPacket.STREAM_CODEC, CompactorTransferPacket::handle);
        // DissolverTransferPacket
        registrar.playToServer(FissionTransferPacket.TYPE, FissionTransferPacket.STREAM_CODEC, FissionTransferPacket::handle);
        registrar.playToServer(FusionTransferPacket.TYPE, FusionTransferPacket.STREAM_CODEC, FusionTransferPacket::handle);
        // LiquifierTransferPacket
        registrar.playToServer(ToggleReactorAutoejectPacket.TYPE, ToggleReactorAutoejectPacket.STREAM_CODEC, ToggleReactorAutoejectPacket::handle);
        registrar.playToServer(SetSideConfigurationPacket.TYPE, SetSideConfigurationPacket.STREAM_CODEC, SetSideConfigurationPacket::handle);
    }
}
