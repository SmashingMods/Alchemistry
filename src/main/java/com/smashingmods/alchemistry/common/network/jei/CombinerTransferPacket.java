package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public record CombinerTransferPacket(BlockPos blockPos, ItemStack output, boolean maxTransfer) implements CustomPacketPayload {
    public static final Type<CombinerTransferPacket> TYPE = new Type<>(Alchemistry.modLoc("combiner_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CombinerTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            CombinerTransferPacket::blockPos,

            ItemStack.STREAM_CODEC,
            CombinerTransferPacket::output,

            ByteBufCodecs.BOOL,
            CombinerTransferPacket::maxTransfer,

            CombinerTransferPacket::new
    );
    
    public static void handle(CombinerTransferPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) pContext.player();

            CombinerBlockEntity blockEntity = (CombinerBlockEntity) player.level().getBlockEntity(packet.blockPos);
            Objects.requireNonNull(blockEntity);

            ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
            ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
            Inventory inventory = player.getInventory();

            RecipeRegistry.getCombinerRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getOutput(), packet.output), player.level())
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

                                int maxOperations = TransferUtils.getMaxOperations(creativeInput, packet.maxTransfer);
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

                                int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventoryStacks, packet.maxTransfer, false);
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
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
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
                PacketDistributor.sendToServer(new CombinerTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getOutput(), pMaxTransfer));
            }
            return null;
        }
    }
}