package com.smashingmods.alchemistry.network;


import com.smashingmods.alchemistry.block.combiner.CombinerRegistry;
import com.smashingmods.alchemistry.block.combiner.CombinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CombinerTransferPkt {

    private ItemStack outputStack;
    private BlockPos pos;

    public CombinerTransferPkt(ItemStack stack, BlockPos pos) {
        this.outputStack = stack;
        this.pos = pos;
    }

    public CombinerTransferPkt(FriendlyByteBuf buf) {
        this.outputStack = buf.readItem();
        this.pos = buf.readBlockPos();
    }

    public static void toBytes(CombinerTransferPkt pkt, FriendlyByteBuf buf) {
        buf.writeItem(pkt.outputStack);
        buf.writeBlockPos(pkt.pos);
    }

    public static CombinerTransferPkt decode(FriendlyByteBuf buf) {
        return new CombinerTransferPkt(buf.readItem(), buf.readBlockPos());
    }

    public static void handle(final CombinerTransferPkt message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player Player = ctx.get().getSender();//.player

            CombinerBlockEntity tile = (CombinerBlockEntity) Player.level.getBlockEntity(message.pos);
            ItemStack output = message.outputStack;
            if (!output.isEmpty()) {
                tile.clientRecipeTarget.setStackInSlot(0, output.copy());
                tile.recipeIsLocked = true;
                tile.currentRecipe = CombinerRegistry.matchOutput(Player.level, output.copy());
            }
            tile.setChanged();
            //System.out.println("Handling packet: {" + message.outputStack + "}");
            //tile.markDirtyClient();
        });
        ctx.get().setPacketHandled(true);
    }
}