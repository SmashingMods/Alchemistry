package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerMenu;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class FusionTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack input1;
    private final ItemStack input2;
    private final boolean maxTransfer;

    public FusionTransferPacket(BlockPos pBlockPos, ItemStack pInput1, ItemStack pInput2, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input1 = pInput1;
        this.input2 = pInput2;
        this.maxTransfer = pMaxTransfer;
    }

    public FusionTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input1 = pBuffer.readItem();
        this.input2 = pBuffer.readItem();
        this.maxTransfer = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(input1);
        pBuffer.writeItem(input2);
        pBuffer.writeBoolean(maxTransfer);
    }

    public static void handle(final FusionTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);

            FusionControllerBlockEntity blockEntity = (FusionControllerBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);

            ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
            ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getFusionRecipe(recipe -> ItemStack.isSameItemSameTags(recipe.getInput1(), pPacket.input1) && ItemStack.isSameItemSameTags(recipe.getInput2(), pPacket.input2), player.getLevel())
                .ifPresent(recipe -> {

                    inputHandler.emptyToInventory(inventory);
                    outputHandler.emptyToInventory(inventory);

                    boolean creative = player.gameMode.isCreative();
                    boolean inventoryContains = inventory.contains(pPacket.input1) && inventory.contains(pPacket.input2);
                    boolean canTransfer = (inventoryContains || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                    if (canTransfer) {
                        List<ItemStack> recipeInputs = List.of(recipe.getInput1(), recipe.getInput2());
                        if (creative) {
                            int maxOperations = TransferUtils.getMaxOperations(recipeInputs, pPacket.maxTransfer);

                            inputHandler.setOrIncrement(0, new ItemStack(recipe.getInput1().getItem(), recipe.getInput1().getCount() * maxOperations));
                            inputHandler.setOrIncrement(1, new ItemStack(recipe.getInput2().getItem(), recipe.getInput2().getCount() * maxOperations));
                        } else {
                            int slot1 = inventory.findSlotMatchingItem(recipe.getInput1());
                            int slot2 = inventory.findSlotMatchingItem(recipe.getInput2());
                            List<ItemStack> inventoryInputs = List.of(inventory.getItem(slot1), inventory.getItem(slot2));

                            int maxOperations = TransferUtils.getMaxOperations(recipeInputs, inventoryInputs, pPacket.maxTransfer, false);

                            inventory.removeItem(slot1, recipe.getInput1().getCount() * maxOperations);
                            inventory.removeItem(slot1, recipe.getInput2().getCount() * maxOperations);

                            inputHandler.setOrIncrement(0, new ItemStack(recipe.getInput1().getItem(), recipe.getInput1().getCount() * maxOperations));
                            inputHandler.setOrIncrement(1, new ItemStack(recipe.getInput2().getItem(), recipe.getInput2().getCount() * maxOperations));
                        }
                        blockEntity.setProgress(0);
                        blockEntity.setRecipe(recipe);
                    }
                });
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
        public Optional<MenuType<FusionControllerMenu>> getMenuType() {
            return Optional.of(MenuRegistry.FUSION_CONTROLLER_MENU.get());
        }

        @Override
        public RecipeType<FusionRecipe> getRecipeType() {
            return RecipeTypes.FUSION;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(FusionControllerMenu pContainer, FusionRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                pContainer.getBlockEntity().setRecipe(pRecipe);
                PacketHandler.INSTANCE.sendToServer(new FusionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput1(), pRecipe.getInput2(), pMaxTransfer));
            }
            return null;
        }
    }
}
