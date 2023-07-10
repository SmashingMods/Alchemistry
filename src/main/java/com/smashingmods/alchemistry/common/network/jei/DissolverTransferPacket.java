package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DissolverTransferPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final IngredientStack input;
    private final boolean maxTransfer;

    public DissolverTransferPacket(BlockPos pBlockPos, IngredientStack pInput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input = pInput;
        this.maxTransfer = pMaxTransfer;
    }

    public DissolverTransferPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.input = IngredientStack.fromNetwork(pBuffer);
        this.maxTransfer = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        input.toNetwork(pBuffer);
        pBuffer.writeBoolean(maxTransfer);
    }

    public void handle(NetworkEvent.Context pContext) {
        ServerPlayer player = pContext.getSender();
        Objects.requireNonNull(player);

        DissolverBlockEntity blockEntity = (DissolverBlockEntity) player.level().getBlockEntity(blockPos);
        Objects.requireNonNull(blockEntity);

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getDissolverRecipe(recipe -> Arrays.stream(recipe.getInput().getIngredient().getItems()).allMatch(input.getIngredient()), player.level())
            .ifPresent(recipe -> {

                DissolverRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                ItemStack inventoryInput = TransferUtils.matchIngredientToItemStack(inventory.items, recipeCopy.getInput());
                ItemStack recipeInput = new ItemStack(inventoryInput.getItem(), recipeCopy.getInput().getCount());
                boolean creative = player.gameMode.isCreative();
                boolean canTransfer = (!inventoryInput.isEmpty() || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        ItemStack creativeInput = new ItemStack(recipeCopy.getInput().getIngredient().getItems()[0].getItem(), recipeCopy.getInput().getCount());
                        int maxOperations = TransferUtils.getMaxOperations(creativeInput, maxTransfer);
                        inputHandler.setOrIncrement(0, new ItemStack(creativeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    } else {
                        int slot = inventory.findSlotMatchingItem(inventoryInput);
                        int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventory.getItem(slot), maxTransfer, false);
                        inventory.removeItem(slot, recipeCopy.getInput().getCount() * maxOperations);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                }
            });
    }

    public static class TransferHandler implements IRecipeTransferHandler<DissolverMenu, DissolverRecipe> {

        public TransferHandler() {}

        @Override
        public Class<DissolverMenu> getContainerClass() {
            return DissolverMenu.class;
        }

        @Override
        public Optional<MenuType<DissolverMenu>> getMenuType() {
            return Optional.of(MenuRegistry.DISSOLVER_MENU.get());
        }

        @Override
        public RecipeType<DissolverRecipe> getRecipeType() {
            return RecipeTypes.DISSOLVER;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(DissolverMenu pContainer, DissolverRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                pContainer.getBlockEntity().setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new DissolverTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}