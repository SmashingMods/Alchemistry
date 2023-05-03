package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.jei.*;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler extends AbstractPacketHandler {

    private final SimpleChannel simpleChannel;

    public PacketHandler() {
        this.simpleChannel = createChannel(new ResourceLocation(Alchemistry.MODID, "main"), "1.1.0");
    }

    @Override
    public PacketHandler register() {
        registerMessage(ToggleAutoBalanceButtonPacket.class, ToggleAutoBalanceButtonPacket::new);
        registerMessage(SetRecipePacket.class, SetRecipePacket::new);
        registerMessage(CombinerTransferPacket.class, CombinerTransferPacket::new);
        registerMessage(CompactorTransferPacket.class, CompactorTransferPacket::new);
        registerMessage(DissolverTransferPacket.class, DissolverTransferPacket::new);
        registerMessage(FissionTransferPacket.class, FissionTransferPacket::new);
        registerMessage(FusionTransferPacket.class, FusionTransferPacket::new);
        registerMessage(LiquifierTransferPacket.class, LiquifierTransferPacket::new);
        registerMessage(SetSideConfigurationPacket.class, SetSideConfigurationPacket::new);
        return this;
    }

    @Override
    protected SimpleChannel getChannel() {
        return simpleChannel;
    }
}
