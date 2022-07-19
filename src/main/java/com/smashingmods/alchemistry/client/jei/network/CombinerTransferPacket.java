package com.smashingmods.alchemistry.client.jei.network;

import com.smashingmods.alchemistry.api.blockentity.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class CombinerTransferPacket {

    private final BlockPos blockPos;
    private final List<ItemStack> items;

    public CombinerTransferPacket(BlockPos pBlockPos, List<ItemStack> pItems) {
        this.blockPos = pBlockPos;
        this.items = pItems;
    }

    public CombinerTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.items = new ArrayList<>();

        int count = pBuffer.readInt();
        for (int i = 0; i < count; i++) {
            items.add(pBuffer.readItem());
        }
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeInt(items.size());
        for (ItemStack item : items) {
            pBuffer.writeItem(item);
        }
    }

    public static void handle(final CombinerTransferPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            ServerPlayer player = pContext.get().getSender();
            Objects.requireNonNull(player);
            AbstractInventoryBlockEntity blockEntity = (AbstractInventoryBlockEntity) player.getLevel().getBlockEntity(pPacket.blockPos);
            Objects.requireNonNull(blockEntity);
            CustomItemStackHandler inputHandler = blockEntity.getInputHandler();

            int counta = 0;
            for (int i = 0; i < pPacket.items.size(); i++) {
                if ((ItemStack.isSameItemSameTags(pPacket.items.get(i), inputHandler.getStackInSlot(i)) || inputHandler.getStackInSlot(i).isEmpty()) && player.getInventory().contains(pPacket.items.get(i))) {
                    counta++;
                }
            }

            if (counta == pPacket.items.size()) {
                RecipeRegistry.getRecipesByType(RecipeRegistry.COMBINER_TYPE, player.getLevel()).stream()
                        .filter(recipe -> recipe.matchInputs(pPacket.items))
                        .findFirst()
                        .ifPresent(recipe -> {

                            int count = 0;
                            for (int j = 0; j < recipe.getInput().size(); j++) {
                                if (inputHandler.getStackInSlot(j).getCount() + recipe.getInput().get(j).getCount() <= inputHandler.getStackInSlot(j).getMaxStackSize()) {
                                    count++;
                                }
                            }

                            if (count == recipe.getInput().size()) {
                                for (int k = 0; k < recipe.getInput().size(); k++) {
                                    player.getInventory().removeItem(recipe.getInput().get(k).copy());
                                    inputHandler.setOrIncrement(k, recipe.getInput().get(k).copy());
                                }
                            }
                            blockEntity.setRecipe(recipe);
                        });
            }
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
        public Class<CombinerRecipe> getRecipeClass() {
            return CombinerRecipe.class;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(CombinerMenu pContainer, CombinerRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                AlchemistryPacketHandler.INSTANCE.sendToServer(new CombinerTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput()));
            }
            return null;
        }
    }
}
