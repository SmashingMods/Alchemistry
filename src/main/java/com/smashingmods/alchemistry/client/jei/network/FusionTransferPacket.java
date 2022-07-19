package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class FusionTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack input1;
    private final ItemStack input2;

    public FusionTransferPacket(BlockPos pBlockPos, ItemStack pInput1, ItemStack pInput2) {
        this.blockPos = pBlockPos;
        this.input1 = pInput1;
        this.input2 = pInput2;
    }

    public FusionTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input1 = pBuffer.readItem();
        this.input2 = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input1);
        pBuffer.writeItem(input2);
    }

    public static void handle(final FusionTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);
            AbstractInventoryBlockEntity blockEntity = (AbstractInventoryBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);
            CustomItemStackHandler inputHandler = blockEntity.getInputHandler();

            ItemStack input1 = pPacket.input1;
            ItemStack handler1 = inputHandler.getStackInSlot(0);
            ItemStack input2 = pPacket.input2;
            ItemStack handler2 = inputHandler.getStackInSlot(1);
            Inventory inventory = player.getInventory();

            if (inventory.contains(input1) && inventory.contains(input2)
                    && (ItemStack.isSameItemSameTags(input1, handler1) || handler1.isEmpty())
                    && (ItemStack.isSameItemSameTags(input2, handler2) || handler2.isEmpty())) {

                RecipeRegistry.getRecipesByType(RecipeRegistry.FUSION_TYPE, player.getLevel()).stream()
                        .filter(recipe -> ItemStack.isSameItemSameTags(input1, recipe.getInput1()) && ItemStack.isSameItemSameTags(input2, recipe.getInput2()))
                        .findFirst()
                        .ifPresent(recipe -> {

                            if ((handler1.getCount() + recipe.getInput1().getCount()) <= handler1.getMaxStackSize()
                                    && (handler2.getCount() + recipe.getInput2().getCount()) <= handler2.getMaxStackSize()) {
                                player.getInventory().removeItem(input1);
                                player.getInventory().removeItem(input2);
                                inputHandler.setOrIncrement(0, input1);
                                inputHandler.setOrIncrement(1, input2);
                            }
                        });
            }
        });
        pContext.get().setPacketHandled(true);
    }

    public static class TransferHandler implements IRecipeTransferHandler<FusionControllerMenu, FusionRecipe> {

        public TransferHandler() {}

        @Override
        public Class<FusionControllerMenu> getContainerClass() {
            return FusionControllerMenu.class;
        }

        @Override
        public Class<FusionRecipe> getRecipeClass() {
            return FusionRecipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(FusionControllerMenu pContainer, FusionRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                AlchemistryPacketHandler.INSTANCE.sendToServer(new FusionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput1(), pRecipe.getInput2()));
            }
            return null;
        }
    }
}
