package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerMenu;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record FusionTransferPacket(BlockPos blockPos, ItemStack input1, ItemStack input2, boolean maxTransfer) implements AlchemyPacket {

    public static final Type<FusionTransferPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "fusion_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FusionTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, FusionTransferPacket::blockPos,
            ItemStack.OPTIONAL_STREAM_CODEC, FusionTransferPacket::input1,
            ItemStack.OPTIONAL_STREAM_CODEC, FusionTransferPacket::input2,
            ByteBufCodecs.BOOL, FusionTransferPacket::maxTransfer,
            FusionTransferPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            if (!(pContext.player() instanceof ServerPlayer player)) return;
            if (!(player.level().getBlockEntity(blockPos) instanceof FusionControllerBlockEntity blockEntity)) return;

            ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
            ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getFusionRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getInput1(), input1) && ItemStack.isSameItemSameComponents(recipe.getInput2(), input2), player.level())
                .ifPresent(recipe -> {
                    FusionRecipe recipeCopy = recipe.copy();

                    inputHandler.emptyToInventory(inventory);
                    outputHandler.emptyToInventory(inventory);

                    boolean creative = player.gameMode.isCreative();
                    boolean inventoryContains = inventory.contains(input1) && inventory.contains(input2);
                    boolean canTransfer = (inventoryContains || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                    if (canTransfer) {
                        List<ItemStack> recipeInputs = List.of(recipeCopy.getInput1(), recipeCopy.getInput2());
                        if (creative) {
                            int maxOperations = TransferUtils.getMaxOperations(recipeInputs, maxTransfer);
                            inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput1().getItem(), recipeCopy.getInput1().getCount() * maxOperations));
                            inputHandler.setOrIncrement(1, new ItemStack(recipeCopy.getInput2().getItem(), recipeCopy.getInput2().getCount() * maxOperations));
                        } else {
                            int slot1 = inventory.findSlotMatchingItem(recipeCopy.getInput1());
                            int slot2 = inventory.findSlotMatchingItem(recipeCopy.getInput2());
                            List<ItemStack> inventoryInputs = List.of(inventory.getItem(slot1), inventory.getItem(slot2));

                            int maxOperations = TransferUtils.getMaxOperations(recipeInputs, inventoryInputs, maxTransfer, false);

                            inventory.removeItem(slot1, recipeCopy.getInput1().getCount() * maxOperations);
                            inventory.removeItem(slot1, recipeCopy.getInput2().getCount() * maxOperations);

                            inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput1().getItem(), recipeCopy.getInput1().getCount() * maxOperations));
                            inputHandler.setOrIncrement(1, new ItemStack(recipeCopy.getInput2().getItem(), recipeCopy.getInput2().getCount() * maxOperations));
                        }
                        blockEntity.setProgress(0);
                        blockEntity.setRecipe(recipe);
                    }
                });
        });
    }

    public static class TransferHandler implements IRecipeTransferHandler<FusionControllerMenu, FusionRecipe> {

        @Override
        public Class<? extends FusionControllerMenu> getContainerClass() {
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
                Alchemistry.PACKET_HANDLER.sendToServer(new FusionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput1(), pRecipe.getInput2(), pMaxTransfer));
            }
            return null;
        }
    }
}
