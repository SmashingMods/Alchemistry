package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
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

public class FissionTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack input;

    public FissionTransferPacket(BlockPos pBlockPos, ItemStack pInput) {
        this.blockPos = pBlockPos;
        this.input = pInput;
    }

    public FissionTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input);
    }

    public static void handle(final FissionTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);
            AbstractInventoryBlockEntity blockEntity = (AbstractInventoryBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);
            CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
            ItemStack input = pPacket.input;
            ItemStack handlerStack = inputHandler.getStackInSlot(0);

            if (player.getInventory().contains(input) && (ItemStack.isSameItemSameTags(input, handlerStack) || handlerStack.isEmpty())) {
                RecipeRegistry.getRecipesByType(RecipeRegistry.FISSION_TYPE, player.getLevel()).stream()
                        .filter(recipe -> ItemStack.isSameItemSameTags(input, recipe.getInput()))
                        .findFirst()
                        .ifPresent(recipe -> {
                            if (handlerStack.getCount() + recipe.getInput().getCount() <= handlerStack.getMaxStackSize()) {
                                player.getInventory().removeItem(input);
                                inputHandler.setOrIncrement(0, input);
                                blockEntity.setRecipe(recipe);
                            }
                        });
            }
        });
        pContext.get().setPacketHandled(true);
    }

    public static class TransferHandler implements IRecipeTransferHandler<FissionControllerMenu, FissionRecipe> {

        public TransferHandler() {}

        @Override
        public Class<FissionControllerMenu> getContainerClass() {
            return FissionControllerMenu.class;
        }

        @Override
        public Class<FissionRecipe> getRecipeClass() {
            return FissionRecipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(FissionControllerMenu pContainer, FissionRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                AlchemistryPacketHandler.INSTANCE.sendToServer(new FissionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput()));
            }
            return null;
        }
    }
}
