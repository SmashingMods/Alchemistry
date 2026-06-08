package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerMenu;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
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
import javax.annotation.Nullable;

import java.util.Optional;

public class FissionTransferPacket implements AlchemyPacket {

    public static final Type<FissionTransferPacket> TYPE = new Type<>(new ResourceLocation(Alchemistry.MODID, "fission_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FissionTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.input,
            ByteBufCodecs.BOOL, packet -> packet.maxTransfer,
            FissionTransferPacket::new
    );

    private final BlockPos blockPos;
    private final ItemStack input;
    private final boolean maxTransfer;

    public FissionTransferPacket(BlockPos pBlockPos, ItemStack pInput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input = pInput;
        this.maxTransfer = pMaxTransfer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        if (!(pContext.player() instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.level().getBlockEntity(blockPos) instanceof FissionControllerBlockEntity blockEntity)) {
            return;
        }

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHander = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getFissionRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getInput(), input), player.level())
            .ifPresent(recipe -> {

                FissionRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHander.emptyToInventory(inventory);

                boolean creative = player.gameMode.isCreative();
                boolean canTransfer = (inventory.contains(recipeCopy.getInput()) || creative) && inputHandler.isEmpty() && outputHander.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        int maxOperations = TransferUtils.getMaxOperations(recipeCopy.getInput(), maxTransfer);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput().getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    } else {
                        int slot = inventory.findSlotMatchingItem(recipeCopy.getInput());
                        int maxOperations = TransferUtils.getMaxOperations(recipeCopy.getInput(), inventory.getItem(slot), maxTransfer, false);
                        inventory.removeItem(slot, recipeCopy.getInput().getCount() * maxOperations);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput().getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                }
            });
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
                Alchemistry.PACKET_HANDLER.sendToServer(new FissionTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}