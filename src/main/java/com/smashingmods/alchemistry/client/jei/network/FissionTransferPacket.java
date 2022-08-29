package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerMenu;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
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

public class FissionTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack input;
    private final boolean maxTransfer;

    public FissionTransferPacket(BlockPos pBlockPos, ItemStack pInput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input = pInput;
        this.maxTransfer = pMaxTransfer;
    }

    public FissionTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = pBuffer.readItem();
        this.maxTransfer = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input);
        pBuffer.writeBoolean(maxTransfer);
    }

    public static void handle(final FissionTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);

            FissionControllerBlockEntity blockEntity = (FissionControllerBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);

            ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
            ProcessingSlotHandler outputHander = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getRecipesByType(RecipeRegistry.FISSION_TYPE, player.getLevel()).stream()
                    .filter(recipe -> ItemStack.isSameItemSameTags(pPacket.input, recipe.getInput()))
                    .findFirst()
                    .ifPresent(recipe -> {

                        inputHandler.emptyToInventory(inventory);
                        outputHander.emptyToInventory(inventory);

                        boolean creative = player.gameMode.isCreative();
                        boolean canTransfer = (inventory.contains(recipe.getInput()) || creative) && inputHandler.isEmpty() && outputHander.isEmpty();

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
                        }
                    });
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
                pContainer.getBlockEntity().setRecipe(pRecipe);
                PacketHandler.INSTANCE.sendToServer(new FissionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}
