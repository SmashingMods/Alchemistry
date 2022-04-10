package com.smashingmods.alchemistry.network;

import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


public class Messages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Alchemistry.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(CombinerButtonPkt.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CombinerButtonPkt::new)
                .encoder(CombinerButtonPkt::toBytes)
                .consumer(CombinerButtonPkt::handle)
                .add();
        net.messageBuilder(CombinerTransferPkt.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CombinerTransferPkt::new)
                .encoder(CombinerTransferPkt::toBytes)
                .consumer(CombinerTransferPkt::handle)
                .add();
        net.messageBuilder(BlockEntityPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(BlockEntityPacket::new)
                .encoder(BlockEntityPacket::toBytes)
                .consumer(BlockEntityPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
        // , player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToTracking(MSG message, Level level, BlockPos pos) {//}, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);//.with(() -> level.getChunk(pos.getX() >> 4, pos.getZ() >> 4)),message);
    }
}
