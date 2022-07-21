package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class CombinerRecipePacket {

    private final BlockPos blockPos;
    private final int recipeIndex;

    public CombinerRecipePacket(BlockPos pBlockPos, int pIndex) {
        this.blockPos = pBlockPos;
        this.recipeIndex = pIndex;
    }

    public CombinerRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.recipeIndex = pBuffer.readInt();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeInt(recipeIndex);
    }

    public static void handle(final CombinerRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            Objects.requireNonNull(player);
            CombinerBlockEntity blockEntity = (CombinerBlockEntity) player.level.getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);
            CombinerRecipe newRecipe = blockEntity.getRecipes().get(pPacket.recipeIndex);
            //noinspection ConstantConditions
            if (blockEntity.getRecipe() == null || !blockEntity.getRecipe().equals(newRecipe)) {
                blockEntity.setProgress(0);
                blockEntity.setRecipe(newRecipe);
            }
        });
        pContext.get().setPacketHandled(true);
    }
}
