package com.smashingmods.alchemistry.network;


import com.smashingmods.alchemistry.block.combiner.CombinerRegistry;
import com.smashingmods.alchemistry.block.combiner.CombinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CombinerTransferPacket {

    private final ItemStack outputStack;
    private final BlockPos blockPos;

    public CombinerTransferPacket(ItemStack pItemStack, BlockPos pBlockPos) {
        this.outputStack = pItemStack;
        this.blockPos = pBlockPos;
    }

    public CombinerTransferPacket(FriendlyByteBuf pBuffer) {
        this.outputStack = pBuffer.readItem();
        this.blockPos = pBuffer.readBlockPos();
    }

    public static void toBytes(CombinerTransferPacket pPacket, FriendlyByteBuf pBuffer) {
        pBuffer.writeItem(pPacket.outputStack);
        pBuffer.writeBlockPos(pPacket.blockPos);
    }

    public static CombinerTransferPacket decode(FriendlyByteBuf pBuffer) {
        return new CombinerTransferPacket(pBuffer.readItem(), pBuffer.readBlockPos());
    }

    public static void handle(final CombinerTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Level level = Objects.requireNonNull(pContext.get().getSender()).level;
            CombinerBlockEntity blockEntity = (CombinerBlockEntity) level.getBlockEntity(pPacket.blockPos);
            ItemStack output = pPacket.outputStack;
            if (!output.isEmpty()) {
                blockEntity.clientRecipeTarget.setStackInSlot(0, output.copy());
                blockEntity.recipeIsLocked = true;
                blockEntity.currentRecipe = CombinerRegistry.matchOutput(level, output.copy());
            }
            blockEntity.setChanged();
        });
        pContext.get().setPacketHandled(true);
    }
}