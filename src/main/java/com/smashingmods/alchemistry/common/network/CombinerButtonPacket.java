//package com.smashingmods.alchemistry.common.network;
//
//import com.smashingmods.alchemistry.common.block.oldblocks.combiner.CombinerBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.network.NetworkEvent;
//
//import java.util.function.Supplier;
//
//
//public class CombinerButtonPacket {
//
//    private final BlockPos blockPos;
//    private final boolean lock;
//    private final boolean pause;
//
//    public CombinerButtonPacket(BlockPos pBlockPos, boolean pLock, boolean pPause) {
//        this.blockPos = pBlockPos;
//        this.lock = pLock;
//        this.pause = pPause;
//    }
//
//    public CombinerButtonPacket(FriendlyByteBuf pBuffer) {
//        this.blockPos = pBuffer.readBlockPos();
//        this.lock = pBuffer.readBoolean();
//        this.pause = pBuffer.readBoolean();
//    }
//
//    public static void toBytes(CombinerButtonPacket pPacket, FriendlyByteBuf pBuffer) {
//        pBuffer.writeBlockPos(pPacket.blockPos);
//        pBuffer.writeBoolean(pPacket.lock);
//        pBuffer.writeBoolean(pPacket.pause);
//    }
//
//    public static CombinerButtonPacket decode(FriendlyByteBuf pBuffer) {
//        return new CombinerButtonPacket(pBuffer.readBlockPos(), pBuffer.readBoolean(), pBuffer.readBoolean());
//    }
//
//    public static void handle(final CombinerButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
//        pContext.get().enqueueWork(() -> {
//            Player Player = pContext.get().getSender();
//            CombinerBlockEntity tile = (CombinerBlockEntity) Player.level.getBlockEntity(pPacket.blockPos);
//
//            if (pPacket.lock) {
//                tile.recipeIsLocked = !(tile.recipeIsLocked);
//                if (!tile.recipeIsLocked) tile.currentRecipe = null;
//            } else if (pPacket.pause) {
//                tile.paused = !(tile.paused);
//            }
//            if (!tile.recipeIsLocked) tile.clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY);
//            //tile.markDirtyClient();
//        });
//        pContext.get().setPacketHandled(true);
//    }
//}
