package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SetRecipePacket {

    private final BlockPos blockPos;
    private final ResourceLocation recipeId;
    private final String group;

    public SetRecipePacket(BlockPos pBlockPos, ResourceLocation pRecipeId, String pGroup) {
        this.blockPos = pBlockPos;
        this.recipeId = pRecipeId;
        this.group = pGroup;
    }

    public SetRecipePacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.recipeId = pBuffer.readResourceLocation();
        this.group = pBuffer.readUtf();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeResourceLocation(recipeId);
        pBuffer.writeUtf(group);
    }

    public static void handle(final SetRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {

            Player player = pContext.get().getSender();

            Objects.requireNonNull(player);
            Objects.requireNonNull(player.getLevel());

            Level level = player.getLevel();
            BlockEntity blockEntity = player.getLevel().getBlockEntity(pPacket.blockPos);
            RecipeRegistry.getRecipeByGroupAndId(pPacket.group, pPacket.recipeId, level).ifPresent(recipe -> {
                if (blockEntity instanceof AbstractProcessingBlockEntity processingBlockEntity) {
                    processingBlockEntity.setProgress(0);
                    processingBlockEntity.setRecipe(recipe);
                    processingBlockEntity.setChanged();
                }
            });
        });
        pContext.get().setPacketHandled(true);
    }
}
