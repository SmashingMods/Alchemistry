package al132.alchemistry.network;

import al132.alchemistry.blocks.combiner.CombinerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static void encode(CombinerButtonPkt pkt, PacketBuffer buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeBoolean(pkt.lock);
        buf.writeBoolean(pkt.pause);
    }

    public static CombinerButtonPkt decode(PacketBuffer buf) {
        return new CombinerButtonPkt(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean());
    }

    public static class Handler {

        public static void handle(final CombinerButtonPkt message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity playerEntity = ctx.get().getSender();
                CombinerTile tile = (CombinerTile) playerEntity.world.getTileEntity(message.pos);

                if (message.lock) {
                    tile.recipeIsLocked = !(tile.recipeIsLocked);
                    if (!tile.recipeIsLocked) tile.currentRecipe = null;
                } else if (message.pause) {
                    tile.paused = !(tile.paused);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}