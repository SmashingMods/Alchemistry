package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public record DissolverTransferPacket(BlockPos blockPos, IngredientStack input, boolean maxTransfer) implements CustomPacketPayload {
    public static final Type<DissolverTransferPacket> TYPE = new Type<>(Alchemistry.modLoc("dissolver_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DissolverTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            DissolverTransferPacket::blockPos,

            IngredientStack.STREAM_CODEC,
            DissolverTransferPacket::input,

            ByteBufCodecs.BOOL,
            DissolverTransferPacket::maxTransfer,

            DissolverTransferPacket::new
    );

    public static void handle(DissolverTransferPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Objects.requireNonNull(player);

            DissolverBlockEntity blockEntity = (DissolverBlockEntity) player.level().getBlockEntity(packet.blockPos);
            Objects.requireNonNull(blockEntity);

            ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
            ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getDissolverRecipe(recipe -> Arrays.stream(recipe.getInput().getIngredient().getItems()).allMatch(packet.input.getIngredient()), player.level())
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
                                int maxOperations = TransferUtils.getMaxOperations(creativeInput, packet.maxTransfer);
                                inputHandler.setOrIncrement(0, new ItemStack(creativeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                            } else {
                                int slot = inventory.findSlotMatchingItem(inventoryInput);
                                int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventory.getItem(slot), packet.maxTransfer, false);
                                inventory.removeItem(slot, recipeCopy.getInput().getCount() * maxOperations);
                                inputHandler.setOrIncrement(0, new ItemStack(recipeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
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

    public static class TransferHandler implements IRecipeTransferHandler<DissolverMenu, DissolverRecipe> {

        public TransferHandler() {
        }

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
        public @Nullable IRecipeTransferError transferRecipe(DissolverMenu container, DissolverRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
            if (doTransfer) {
                container.getBlockEntity().setRecipe(recipe);
                PacketDistributor.sendToServer(new DissolverTransferPacket(container.getBlockEntity().getBlockPos(), recipe.getInput(), maxTransfer));
            }
            return null;
        }
    }
}
