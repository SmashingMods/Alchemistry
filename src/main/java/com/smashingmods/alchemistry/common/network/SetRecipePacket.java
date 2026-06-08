package com.smashingmods.alchemistry.common.network;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SetRecipePacket implements AlchemyPacket {

    public static final Type<SetRecipePacket> TYPE = new Type<>(new ResourceLocation(Alchemistry.MODID, "set_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetRecipePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ResourceLocation.STREAM_CODEC, packet -> packet.recipeId,
            ByteBufCodecs.STRING_UTF8, packet -> packet.group,
            SetRecipePacket::new
    );

    private final BlockPos blockPos;
    private final ResourceLocation recipeId;
    private final String group;

    public SetRecipePacket(BlockPos pBlockPos, ResourceLocation pRecipeId, String pGroup) {
        this.blockPos = pBlockPos;
        this.recipeId = pRecipeId;
        this.group = pGroup;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        Level level = pContext.player().level();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        RecipeRegistry.getRecipeByGroupAndId(group, recipeId, level).ifPresent(recipe -> {
            if (blockEntity instanceof AbstractProcessingBlockEntity processingBlockEntity) {
                processingBlockEntity.setProgress(0);
                processingBlockEntity.setRecipe(recipe);
                processingBlockEntity.setChanged();
            }
        });
    }
}
