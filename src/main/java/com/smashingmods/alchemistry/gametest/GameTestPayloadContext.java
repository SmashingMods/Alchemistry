package com.smashingmods.alchemistry.gametest;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Minimal {@link IPayloadContext} for the networking gametests. At 1.20.6 {@code IPayloadContext} became an
 * interface with no public implementation a test can construct, so the gametests that drive a packet's production
 * {@code handle} body directly build this stub instead of the old {@code PlayPayloadContext} record.
 *
 * <p>The Alchemistry server-bound handlers read only {@link #player()} (then {@code player.level()....}), so the
 * stub carries the gametest player and the real received {@link PacketFlow}. {@link #enqueueWork(Runnable)} runs
 * the task synchronously -- a gametest already runs on the server main thread, the same thread the production
 * network-to-main hop targets -- so a handler that enqueues work behaves identically here. Every other context
 * method is outside what these handlers touch and throws {@link UnsupportedOperationException} so an accidental
 * reliance on it surfaces loudly rather than returning a misleading default.</p>
 */
class GameTestPayloadContext implements IPayloadContext {

    private final Player player;
    private final PacketFlow flow;

    GameTestPayloadContext(Player player, PacketFlow flow) {
        this.player = player;
        this.flow = flow;
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public PacketFlow flow() {
        return flow;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        task.run();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return CompletableFuture.completedFuture(task.get());
    }

    @Override
    public ICommonPacketListener listener() {
        throw new UnsupportedOperationException("listener() is not provided by the gametest payload context");
    }

    @Override
    public void handle(CustomPacketPayload payload) {
        throw new UnsupportedOperationException("handle(CustomPacketPayload) is not provided by the gametest payload context");
    }

    @Override
    public void finishCurrentTask(ConfigurationTask.Type type) {
        throw new UnsupportedOperationException("finishCurrentTask(...) is not provided by the gametest payload context");
    }
}
