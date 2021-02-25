package al132.alchemistry.network;


import al132.alchemistry.blocks.combiner.CombinerRegistry;
import al132.alchemistry.blocks.combiner.CombinerTile;
import al132.alchemistry.blocks.combiner.CombinerRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CombinerTransferPkt {

    private ItemStack outputStack;
    private BlockPos pos;

    public CombinerTransferPkt(ItemStack stack, BlockPos pos) {
        this.outputStack = stack;
        this.pos = pos;
    }

    public static void encode(CombinerTransferPkt pkt, PacketBuffer buf) {
        buf.writeItemStack(pkt.outputStack);
        buf.writeBlockPos(pkt.pos);
    }

    public static CombinerTransferPkt decode(PacketBuffer buf) {
        return new CombinerTransferPkt(buf.readItemStack(), buf.readBlockPos());
    }


    public static class Handler {

        public static void handle(final CombinerTransferPkt message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity playerEntity = ctx.get().getSender();//.player

                CombinerTile tile = (CombinerTile) playerEntity.world.getTileEntity(message.pos);
                ItemStack output = message.outputStack;
                if (!output.isEmpty()) {
                    tile.clientRecipeTarget.setStackInSlot(0, output.copy());
                    tile.recipeIsLocked = true;
                    tile.currentRecipe = CombinerRegistry.matchOutput(playerEntity.world, output.copy());
                }
                //System.out.println("Handling packet: {" + message.outputStack + "}");
                //tile.markDirtyClient();
            });
            ctx.get().setPacketHandled(true);
        }

    }
}