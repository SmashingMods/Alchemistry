package com.smashingmods.alchemistry.common.network.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientFusionRecipePacket {

    private final BlockPos blockPos;
    private final ItemStack input1;
    private final ItemStack input2;

    public ClientFusionRecipePacket(BlockPos pBlockPos, ItemStack pInput1, ItemStack pInput2) {
        this.blockPos = pBlockPos;
        this.input1 = pInput1;
        this.input2 = pInput2;
    }

    public ClientFusionRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input1 = pBuffer.readItem();
        this.input2 = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input1);
        pBuffer.writeItem(input2);
    }

    public static void handle(final ClientFusionRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
//            if (pContext.get().getDirection().getReceptionSide().isClient()) {
//                Level level = Objects.requireNonNull(pContext.get().getSender()).level;
//                ProcessingBlockEntity blockEntity = (ProcessingBlockEntity) level.getBlockEntity(pPacket.blockPos);
//                if (blockEntity != null) {
//                    RecipeRegistry.getRecipesByType(RecipeRegistry.FUSION_TYPE, level).stream()
//                            .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput1(), pPacket.input1) && ItemStack.isSameItemSameTags(recipe.getInput2(), pPacket.input2))
//                            .findFirst()
//                            .ifPresent(blockEntity::setRecipe);
//                }
//            }
        });
        pContext.get().setPacketHandled(true);
    }
}