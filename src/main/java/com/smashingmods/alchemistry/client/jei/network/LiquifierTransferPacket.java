package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.recipe.ClientLiquifierRecipePacket;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
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

public class LiquifierTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack input;
    private final boolean maxTransfer;

    public LiquifierTransferPacket(BlockPos pBlockPos, ItemStack pInput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input = pInput;
        this.maxTransfer = pMaxTransfer;
    }

    public LiquifierTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = pBuffer.readItem();
        this.maxTransfer = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input);
        pBuffer.writeBoolean(maxTransfer);
    }

    public static void handle(final LiquifierTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);

            LiquifierBlockEntity blockEntity = (LiquifierBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);

            CustomItemStackHandler inputHandler = blockEntity.getItemHandler();

            Inventory inventory = player.getInventory();

            RecipeRegistry.getRecipesByType(RecipeRegistry.LIQUIFIER_TYPE, player.getLevel()).stream()
                    .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), pPacket.input))
                    .findFirst()
                    .ifPresent(recipe -> {

                        inputHandler.emptyToInventory(inventory);

                        boolean creative = player.gameMode.isCreative();
                        boolean canTransfer = (inventory.contains(recipe.getInput()) || creative) && inputHandler.isEmpty() && blockEntity.getFluidStorage().isEmpty();

                        if (canTransfer) {
                            if (creative) {
                                int maxOperations = TransferUtils.getMaxOperations(recipe.getInput(), pPacket.maxTransfer);
                                inputHandler.setOrIncrement(0, new ItemStack(recipe.getInput().getItem(), recipe.getInput().getCount() * maxOperations));
                            } else {
                                int slot = inventory.findSlotMatchingItem(recipe.getInput());
                                int maxOperations = TransferUtils.getMaxOperations(recipe.getInput(), inventory.getItem(slot), pPacket.maxTransfer, false);
                                inventory.removeItem(slot, recipe.getInput().getCount() * maxOperations);
                                inputHandler.setOrIncrement(0, new ItemStack(recipe.getInput().getItem(), recipe.getInput().getCount() * maxOperations));
                            }
                            blockEntity.setProgress(0);
                            blockEntity.setRecipe(recipe);
                            AlchemistryPacketHandler.sendToNear(new ClientLiquifierRecipePacket(pPacket.blockPos, pPacket.input), player.getLevel(), pPacket.blockPos, 64);
                        }
                    });
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
                AlchemistryPacketHandler.INSTANCE.sendToServer(new LiquifierTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}
