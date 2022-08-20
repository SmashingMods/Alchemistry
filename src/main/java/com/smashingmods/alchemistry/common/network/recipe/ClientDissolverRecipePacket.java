package com.smashingmods.alchemistry.common.network.recipe;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientDissolverRecipePacket {

    private final BlockPos blockPos;
    private final ItemStack input;

    public ClientDissolverRecipePacket(BlockPos pBlockPos, ItemStack pInput) {
        this.blockPos = pBlockPos;
        this.input = pInput;
    }

    public ClientDissolverRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input);
    }

    public static void handle(final ClientDissolverRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {

//            if (pContext.get().getDirection().getReceptionSide().isClient()) {
//                Level level = Minecraft.getInstance().level;
//                if (level != null) {
//                    ProcessingBlockEntity blockEntity = (ProcessingBlockEntity) level.getBlockEntity(pPacket.blockPos);
//                    if (blockEntity != null) {
//                        RecipeRegistry.getRecipesByType(RecipeRegistry.DISSOLVER_TYPE.get(), level).stream()
//                                .filter(recipe -> recipe.matches(pPacket.input))
//                                .findFirst()
//                                .ifPresent(blockEntity::setRecipe);
//                    }
//                }
//            }
        });
        pContext.get().setPacketHandled(true);
    }
}
