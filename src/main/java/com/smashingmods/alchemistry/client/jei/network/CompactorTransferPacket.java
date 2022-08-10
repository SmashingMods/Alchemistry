package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class CompactorTransferPacket {

    private final BlockPos blockPos;
    private final ItemStack output;

    public CompactorTransferPacket(BlockPos pBlockPos, ItemStack pOutput) {
        this.blockPos = pBlockPos;
        this.output = pOutput;
    }

    public CompactorTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.output = pBuffer.readItem();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeItem(output);
    }

    public static void handle(final CompactorTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);

            ServerLevel level = player.getLevel();

            CompactorBlockEntity blockEntity = (CompactorBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);

            CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
            CustomItemStackHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            ItemStack packetOutput = pPacket.output;
            ItemStack handlerInputStack = inputHandler.getStackInSlot(0);
            ItemStack handlerOutputStack = outputHandler.getStackInSlot(0);

            RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE.get(), level).stream()
                    .filter(recipe -> ItemStack.isSameItemSameTags(packetOutput, recipe.getOutput()))
                    .findFirst()
                    .ifPresent(recipe -> {

                        if (player.getInventory().contains(recipe.getInput())) {
                            int itemSlot = inventory.findSlotMatchingItem(recipe.getInput().copy());
                            if (!handlerOutputStack.isEmpty()) {
                                int freeSlot = inventory.findSlotMatchingItem(handlerOutputStack) != -1 ? inventory.findSlotMatchingItem(handlerOutputStack) : inventory.getFreeSlot();
                                inventory.add(freeSlot, handlerOutputStack.copy());
                                outputHandler.setStackInSlot(0, ItemStack.EMPTY);
                            }
                            if (!handlerInputStack.isEmpty()) {
                                if (!ItemStack.isSameItemSameTags(handlerInputStack.copy(), recipe.getInput().copy())) {
                                    int freeSlot = inventory.findSlotMatchingItem(handlerInputStack) != -1 ? inventory.findSlotMatchingItem(handlerInputStack) : inventory.getFreeSlot();
                                    inventory.add(freeSlot, handlerInputStack.copy());
                                    inputHandler.setStackInSlot(0, ItemStack.EMPTY);
                                }
                                if (handlerInputStack.getCount() < recipe.getInput().getCount()) {
                                    int count = recipe.getInput().getCount() - handlerInputStack.getCount();
                                    inventory.removeItem(itemSlot, count);
                                    inputHandler.incrementSlot(0, count);
                                } else {
                                    inventory.removeItem(itemSlot, recipe.getInput().getCount());
                                    inputHandler.setStackInSlot(0, recipe.getInput().copy());
                                }
                            } else {
                                inventory.removeItem(itemSlot, recipe.getInput().getCount());
                                inputHandler.setStackInSlot(0, recipe.getInput().copy());
                            }
                            blockEntity.setRecipe(recipe);
                            blockEntity.setTarget(recipe.getOutput().copy());
                        }
            });
        });
        pContext.get().setPacketHandled(true);
    }

    public static class TransferHandler implements IRecipeTransferHandler<CompactorMenu, CompactorRecipe> {

        public TransferHandler() {}

        @Override
        public Class<CompactorMenu> getContainerClass() {
            return CompactorMenu.class;
        }

        @Override
        public Optional<MenuType<CompactorMenu>> getMenuType() {
            return Optional.of(MenuRegistry.COMPACTOR_MENU.get());
        }

        @Override
        public RecipeType<CompactorRecipe> getRecipeType() {
            return RecipeTypes.COMPACTOR;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(CompactorMenu pContainer, CompactorRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                AlchemistryPacketHandler.INSTANCE.sendToServer(new CompactorTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getOutput()));
            }
            return null;
        }
    }
}
