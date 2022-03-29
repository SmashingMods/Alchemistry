package al132.alchemistry.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockEntityPacket {


    private BlockPos pos;
    private CompoundTag tag;

    public BlockEntityPacket(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public BlockEntityPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.tag = buf.readNbt();
    }

    public static void toBytes(BlockEntityPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeNbt(pkt.tag);
    }

    public static BlockEntityPacket decode(FriendlyByteBuf buf) {
        return new BlockEntityPacket(buf.readBlockPos(), buf.readNbt());
    }

    public static void handle(final BlockEntityPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            BlockEntity entity = world.getBlockEntity(message.pos);
            //System.out.println("Before: " + entity.getUpdateTag());
            entity.load(message.tag);
            //System.out.println("Data: " + message.tag);
            //System.out.println("After: " + entity.getUpdateTag());
            //System.out.println("Target: " + ((CombinerTile)entity).clientRecipeTarget.getStackInSlot(0));

        });
        ctx.get().setPacketHandled(true);
    }
}