package com.smashingmods.alchemistry.network;

import com.smashingmods.alchemistry.blocks.combiner.CombinerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class CombinerButtonPkt {

    private BlockPos pos;
    private boolean lock;
    private boolean pause;

    public CombinerButtonPkt(BlockPos pos, boolean lock, boolean pause) {
        this.pos = pos;
        this.lock = lock;
        this.pause = pause;
    }

    public CombinerButtonPkt(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.lock = buf.readBoolean();
        this.pause = buf.readBoolean();
    }

    public static void toBytes(CombinerButtonPkt pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeBoolean(pkt.lock);
        buf.writeBoolean(pkt.pause);
    }

    public static CombinerButtonPkt decode(FriendlyByteBuf buf) {
        return new CombinerButtonPkt(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(final CombinerButtonPkt message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player Player = ctx.get().getSender();
            CombinerTile tile = (CombinerTile) Player.level.getBlockEntity(message.pos);

            if (message.lock) {
                tile.recipeIsLocked = !(tile.recipeIsLocked);
                if (!tile.recipeIsLocked) tile.currentRecipe = null;
            } else if (message.pause) {
                tile.paused = !(tile.paused);
            }
            if (!tile.recipeIsLocked) tile.clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY);
            //tile.markDirtyClient();
        });
        ctx.get().setPacketHandled(true);
    }
}
