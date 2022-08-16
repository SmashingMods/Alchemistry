package com.smashingmods.alchemistry.common.network.recipe;

import com.smashingmods.alchemistry.api.blockentity.ProcessingBlockEntity;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientFissionRecipePacket {

    private final BlockPos blockPos;
    private final ItemStack input;

    public ClientFissionRecipePacket(BlockPos pBlockPos, ItemStack pInput) {
        this.blockPos = pBlockPos;
        this.input = pInput;
    }

    public ClientFissionRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input);
    }

    public static void handle(final ClientFissionRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {

            if (pContext.get().getDirection().getReceptionSide().isClient()) {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    ProcessingBlockEntity blockEntity = (ProcessingBlockEntity) level.getBlockEntity(pPacket.blockPos);
                    if (blockEntity != null) {
                        RecipeRegistry.getRecipesByType(RecipeRegistry.FISSION_TYPE, level).stream()
                                .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), pPacket.input))
                                .findFirst()
                                .ifPresent(blockEntity::setRecipe);
                    }
                }
            }
        });
        pContext.get().setPacketHandled(true);
    }
}