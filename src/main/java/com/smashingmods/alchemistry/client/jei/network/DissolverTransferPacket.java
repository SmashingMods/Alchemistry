package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.recipe.ClientDissolverRecipePacket;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class DissolverTransferPacket {

    private final BlockPos blockPos;
    private final Ingredient input;
    private final boolean maxTransfer;

    public DissolverTransferPacket(BlockPos pBlockPos, Ingredient pInput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input = pInput;
        this.maxTransfer = pMaxTransfer;
    }

    public DissolverTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = Ingredient.fromNetwork(pBuffer);
        this.maxTransfer = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        input.toNetwork(pBuffer);
        pBuffer.writeBoolean(maxTransfer);
    }

    public static void handle(final DissolverTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);

            DissolverBlockEntity blockEntity = (DissolverBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);

            CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
            CustomItemStackHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();
            ItemStack input = pPacket.input.getItems()[0];

            RecipeRegistry.getRecipesByType(RecipeRegistry.DISSOLVER_TYPE, player.getLevel()).stream()
                    .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput().getItems()[0], input))
                    .findFirst()
                    .ifPresent(recipe -> {

                        inputHandler.emptyToInventory(inventory);
                        outputHandler.emptyToInventory(inventory);

                        boolean creative = player.gameMode.isCreative();
                        boolean canTransfer = (inventory.contains(input) || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                        if (canTransfer) {
                            if (creative) {
                                int maxOperations = TransferUtils.getMaxOperations(input, pPacket.maxTransfer);
                                inputHandler.setOrIncrement(0, new ItemStack(input.getItem(), input.getCount() * maxOperations));
                            } else {
                                int slot = inventory.findSlotMatchingItem(input);
                                int maxOperations = TransferUtils.getMaxOperations(input, inventory.getItem(slot), pPacket.maxTransfer, false);
                                inventory.removeItem(slot, input.getCount() * maxOperations);
                                inputHandler.setOrIncrement(0, new ItemStack(input.getItem(), input.getCount() * maxOperations));
                            }
                            blockEntity.setProgress(0);
                            blockEntity.setRecipe(recipe);
                            AlchemistryPacketHandler.sendToNear(new ClientDissolverRecipePacket(pPacket.blockPos, input), player.getLevel(), pPacket.blockPos, 64);
                        }
                    });
        });
        pContext.get().setPacketHandled(true);
    }

    public static class TransferHandler implements IRecipeTransferHandler<DissolverMenu, DissolverRecipe> {

        public TransferHandler() {}

        @Override
        public Class<DissolverMenu> getContainerClass() {
            return DissolverMenu.class;
        }

        @Override
        public Class<DissolverRecipe> getRecipeClass() {
            return DissolverRecipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(DissolverMenu pContainer, DissolverRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                AlchemistryPacketHandler.INSTANCE.sendToServer(new DissolverTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}
