package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.AbstractFluidBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class LiquifierTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack input;

    public LiquifierTransferPacket(BlockPos pBlockPos, ItemStack pInput) {
        this.blockPos = pBlockPos;
        this.input = pInput;
    }

    public LiquifierTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input);
    }

    public static void handle(final LiquifierTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);
            AbstractFluidBlockEntity blockEntity = (AbstractFluidBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);
            CustomItemStackHandler inputHandler = blockEntity.getItemHandler();
            ItemStack input = pPacket.input;
            ItemStack handlerStack = inputHandler.getStackInSlot(0);

            if (player.getInventory().contains(input) && (ItemStack.isSameItemSameTags(input, handlerStack) || handlerStack.isEmpty())) {
                RecipeRegistry.getRecipesByType(RecipeRegistry.LIQUIFIER_TYPE, player.getLevel()).stream()
                        .filter(recipe -> ItemStack.isSameItemSameTags(input, recipe.getInput()))
                        .findFirst()
                        .ifPresent(recipe -> {

                            if ((handlerStack.getCount() + recipe.getInput().getCount()) <= handlerStack.getMaxStackSize()) {
                                player.getInventory().removeItem(input);
                                inputHandler.setOrIncrement(0, input);
                                blockEntity.setRecipe(recipe);
                            }
                        });
            }

        });
        pContext.get().setPacketHandled(true);
    }

    public static class TransferHandler implements IRecipeTransferHandler<LiquifierMenu, LiquifierRecipe> {

        public TransferHandler() {}

        @Override
        public Class<LiquifierMenu> getContainerClass() {
            return LiquifierMenu.class;
        }

        @Override
        public Class<LiquifierRecipe> getRecipeClass() {
            return LiquifierRecipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(LiquifierMenu pContainer, LiquifierRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                AlchemistryPacketHandler.INSTANCE.sendToServer(new LiquifierTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput()));
            }
            return null;
        }
    }
}
