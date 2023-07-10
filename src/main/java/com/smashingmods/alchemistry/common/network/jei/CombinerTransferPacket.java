package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class CombinerTransferPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final ItemStack output;
    private final boolean maxTransfer;

    public CombinerTransferPacket(BlockPos pBlockPos, ItemStack pOutput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.output = pOutput;
        this.maxTransfer = pMaxTransfer;
    }

    public CombinerTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.output = pBuffer.readItem();
        this.maxTransfer = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(output);
        pBuffer.writeBoolean(maxTransfer);
    }

    public void handle(NetworkEvent.Context pContext) {
        ServerPlayer player = pContext.getSender();
        Objects.requireNonNull(player);

        CombinerBlockEntity blockEntity = (CombinerBlockEntity) player.level().getBlockEntity(blockPos);
        Objects.requireNonNull(blockEntity);

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getCombinerRecipe(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), output), player.level())
            .ifPresent(recipe -> {

                CombinerRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                List<ItemStack> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.items, recipeCopy.getInput());
                List<ItemStack> recipeInput = new ArrayList<>();
                IntStream.range(0, inventoryInput.size()).forEach(i -> recipeInput.add(new ItemStack(inventoryInput.get(i).getItem(), recipeCopy.getInput().get(i).getCount())));

                boolean creative = player.gameMode.isCreative();
                boolean canTransfer = (!inventoryInput.isEmpty() || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        List<ItemStack> creativeInput = new ArrayList<>();

                        for (int i = 0; i < recipeCopy.getInput().size(); i++) {
                            ItemStack item = new ItemStack(recipeCopy.getInput().get(i).getIngredient().getItems()[0].getItem(), recipeCopy.getInput().get(i).getCount());
                            creativeInput.add(i, item);
                        }

                        int maxOperations = TransferUtils.getMaxOperations(creativeInput, maxTransfer);
                        for (int i = 0; i < recipeCopy.getInput().size(); i++) {
                            inputHandler.setOrIncrement(i, new ItemStack(creativeInput.get(i).getItem(), recipeCopy.getInput().get(i).getCount() * maxOperations));
                        }
                    } else {
                        List<ItemStack> inventoryStacks = new ArrayList<>();
                        inventoryInput.stream().map(inventory::findSlotMatchingItem).forEach(slot -> {
                            if (slot != -1) {
                                inventoryStacks.add(inventory.getItem(slot));
                            }
                        });

                        int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventoryStacks, maxTransfer, false);
                        recipeInput.forEach(itemStack -> {
                            int slot = player.getInventory().findSlotMatchingItem(itemStack);
                            player.getInventory().removeItem(slot, itemStack.getCount() * maxOperations);
                        });

                        for (int i = 0; i < recipeCopy.getInput().size(); i++) {
                            inputHandler.setOrIncrement(i, new ItemStack(recipeInput.get(i).getItem(), recipeCopy.getInput().get(i).getCount() * maxOperations));
                        }
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                    blockEntity.setCanProcess(true);
                }
            });
    }

    public static class TransferHandler implements IRecipeTransferHandler<CombinerMenu, CombinerRecipe> {

        public TransferHandler() {}

        @Override
        public Class<CombinerMenu> getContainerClass() {
            return CombinerMenu.class;
        }

        @Override
        public Optional<MenuType<CombinerMenu>> getMenuType() {
            return Optional.of(MenuRegistry.COMBINER_MENU.get());
        }

        @Override
        public RecipeType<CombinerRecipe> getRecipeType() {
            return RecipeTypes.COMBINER;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(CombinerMenu pContainer, CombinerRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                pContainer.getBlockEntity().setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new CombinerTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getOutput(), pMaxTransfer));
            }
            return null;
        }
    }
}