package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerMenu;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record FissionTransferPacket(BlockPos blockPos, ItemStack input, boolean maxTransfer) implements CustomPacketPayload {
    public static final Type<FissionTransferPacket> TYPE = new Type<>(Alchemistry.modLoc("fission_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FissionTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            FissionTransferPacket::blockPos,

            ItemStack.STREAM_CODEC,
            FissionTransferPacket::input,

            ByteBufCodecs.BOOL,
            FissionTransferPacket::maxTransfer,

            FissionTransferPacket::new
    );

    public static void handle(FissionTransferPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) pContext.player();

            FissionControllerBlockEntity blockEntity = (FissionControllerBlockEntity) player.level().getBlockEntity(packet.blockPos);
            Objects.requireNonNull(blockEntity);

            ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
            ProcessingSlotHandler outputHander = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getFissionRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getInput(), packet.input), player.level())
                    .ifPresent(recipe -> {

                        FissionRecipe recipeCopy = recipe.copy();

                        inputHandler.emptyToInventory(inventory);
                        outputHander.emptyToInventory(inventory);

                        boolean creative = player.gameMode.isCreative();
                        boolean canTransfer = (inventory.contains(recipeCopy.getInput()) || creative) && inputHandler.isEmpty() && outputHander.isEmpty();

                        if (canTransfer) {
                            if (creative) {
                                int maxOperations = TransferUtils.getMaxOperations(recipeCopy.getInput(), packet.maxTransfer);
                                inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput().getItem(), recipeCopy.getInput().getCount() * maxOperations));
                            } else {
                                int slot = inventory.findSlotMatchingItem(recipeCopy.getInput());
                                int maxOperations = TransferUtils.getMaxOperations(recipeCopy.getInput(), inventory.getItem(slot), packet.maxTransfer, false);
                                inventory.removeItem(slot, recipeCopy.getInput().getCount() * maxOperations);
                                inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput().getItem(), recipeCopy.getInput().getCount() * maxOperations));
                            }
                            blockEntity.setProgress(0);
                            blockEntity.setRecipe(recipe);
                        }
                    });
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class TransferHandler implements IRecipeTransferHandler<FissionControllerMenu, FissionRecipe> {

        public TransferHandler() {}

        @Override
        public Class<FissionControllerMenu> getContainerClass() {
            return FissionControllerMenu.class;
        }

        @Override
        public Optional<MenuType<FissionControllerMenu>> getMenuType() {
            return Optional.of(MenuRegistry.FISSION_CONTROLLER_MENU.get());
        }

        @Override
        public RecipeType<FissionRecipe> getRecipeType() {
            return RecipeTypes.FISSION;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(FissionControllerMenu pContainer, FissionRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                pContainer.getBlockEntity().setRecipe(pRecipe);
                PacketDistributor.sendToServer(new FissionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}