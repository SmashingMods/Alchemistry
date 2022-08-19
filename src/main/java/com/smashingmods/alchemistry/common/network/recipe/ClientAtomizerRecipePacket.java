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

public class ClientAtomizerRecipePacket {

    private final BlockPos blockPos;
    private final ItemStack output;

    public ClientAtomizerRecipePacket(BlockPos pBlockPos, ItemStack pOutput) {
        this.blockPos = pBlockPos;
        this.output = pOutput;
    }

    public ClientAtomizerRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.output = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(output);
    }

    public static void handle(final ClientAtomizerRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {

            if (pContext.get().getDirection().getReceptionSide().isClient()) {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    ProcessingBlockEntity blockEntity = (ProcessingBlockEntity) level.getBlockEntity(pPacket.blockPos);
                    if (blockEntity != null) {
                        RecipeRegistry.getRecipesByType(RecipeRegistry.ATOMIZER_TYPE.get(), level).stream()
                                .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), pPacket.output))
                                .findFirst()
                                .ifPresent(blockEntity::setRecipe);
                    }
                }
            }
        });
        pContext.get().setPacketHandled(true);
    }
}
