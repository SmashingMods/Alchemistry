package com.smashingmods.alchemistry.common.network.recipe;

import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientCompactorRecipePacket {

        private final BlockPos blockPos;
        private final ItemStack output;

        public ClientCompactorRecipePacket(BlockPos pBlockPos, ItemStack pOutput) {
            this.blockPos = pBlockPos;
            this.output = pOutput;
        }

        public ClientCompactorRecipePacket(FriendlyByteBuf pBuffer) {
            this.blockPos = pBuffer.readBlockPos();
            this.output = pBuffer.readItem();
        }

        public void encode(FriendlyByteBuf pBuffer) {
            pBuffer.writeBlockPos(blockPos);
            pBuffer.writeItem(output);
        }

        public static void handle(final ClientCompactorRecipePacket pPacket, Supplier<NetworkEvent.Context> pContext) {
            pContext.get().enqueueWork(() -> {

                if (pContext.get().getDirection().getReceptionSide().isClient()) {
                    Level level = Minecraft.getInstance().level;
                    if (level != null) {
                        CompactorBlockEntity blockEntity = (CompactorBlockEntity) level.getBlockEntity(pPacket.blockPos);
                        if (blockEntity != null) {
                            RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream()
                                    .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), pPacket.output))
                                    .findFirst()
                                    .ifPresent(recipe -> {
                                        blockEntity.setRecipe(recipe);
                                        blockEntity.setTarget(pPacket.output);
                                    });
                        }
                    }
                }
            });
            pContext.get().setPacketHandled(true);
        }
}
