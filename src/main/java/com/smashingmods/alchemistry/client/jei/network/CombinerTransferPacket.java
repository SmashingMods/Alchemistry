package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.recipe.ClientCombinerRecipePacket;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CombinerTransferPacket {

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

    public static void handle(final CombinerTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);

            CombinerBlockEntity blockEntity = (CombinerBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);

            CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
            CustomItemStackHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getRecipesByType(RecipeRegistry.COMBINER_TYPE, player.getLevel()).stream()
                    .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), pPacket.output))
                    .findFirst()
                    .ifPresent(recipe -> {

                        inputHandler.emptyToInventory(inventory);
                        outputHandler.emptyToInventory(inventory);

                        List<ItemStack> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.items, recipe.getInput());
                        List<ItemStack> recipeInput = new ArrayList<>();
                        IntStream.range(0, inventoryInput.size()).forEach(i -> recipeInput.add(new ItemStack(inventoryInput.get(i).getItem(), recipe.getInput().get(i).getCount())));

                        boolean creative = player.gameMode.isCreative();
                        boolean canTransfer = (!inventoryInput.isEmpty() || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                        if (canTransfer) {
                            if (creative) {
                                List<ItemStack> creativeInput = new ArrayList<>();

                                for (int i = 0; i < recipe.getInput().size(); i++) {
                                    ItemStack[] items = recipe.getInput().get(i).getIngredient().getItems();
                                    creativeInput.add(i, items[(int) (Math.random() * items.length)]);
                                }

                                int maxOperations = TransferUtils.getMaxOperations(creativeInput, pPacket.maxTransfer);
                                for (int i = 0; i < recipe.getInput().size(); i++) {
                                    inputHandler.setOrIncrement(i, new ItemStack(creativeInput.get(i).getItem(), recipe.getInput().get(i).getCount() * maxOperations));
                                }
                            } else {
                                List<ItemStack> inventoryStacks = new ArrayList<>();
                                inventoryInput.stream().map(inventory::findSlotMatchingItem).forEach(slot -> {
                                    if (slot != -1) {
                                        inventoryStacks.add(inventory.getItem(slot));
                                    }
                                });

                                int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventoryStacks, pPacket.maxTransfer, false);
                                recipeInput.forEach(itemStack -> {
                                        int slot = player.getInventory().findSlotMatchingItem(itemStack);
                                        player.getInventory().removeItem(slot, itemStack.getCount() * maxOperations);
                                });

                                for (int k = 0; k < recipe.getInput().size(); k++) {
                                    inputHandler.setOrIncrement(k, new ItemStack(recipeInput.get(k).getItem(), recipe.getInput().get(k).getCount() * maxOperations));
                                }
                            }
                            blockEntity.setProgress(0);
                            blockEntity.setRecipe(recipe);
                            AlchemistryPacketHandler.sendToNear(new ClientCombinerRecipePacket(pPacket.blockPos, pPacket.output), player.getLevel(), pPacket.blockPos, 64);
                        }
                    });
        });
        pContext.get().setPacketHandled(true);
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
                AlchemistryPacketHandler.INSTANCE.sendToServer(new CombinerTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getOutput(), pMaxTransfer));
            }
            return null;
        }
    }
}
