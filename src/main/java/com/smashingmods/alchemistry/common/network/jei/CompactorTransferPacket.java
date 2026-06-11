package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorMenu;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
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

public class CompactorTransferPacket implements AlchemyPacket {

    public static final Type<CompactorTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "compactor_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompactorTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.output,
            ByteBufCodecs.BOOL, packet -> packet.maxTransfer,
            CompactorTransferPacket::new
    );

    private final BlockPos blockPos;
    private final ItemStack output;
    private final boolean maxTransfer;

    public CompactorTransferPacket(BlockPos pBlockPos, ItemStack pOutput, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.output = pOutput;
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
        if (!(player.level().getBlockEntity(blockPos) instanceof CompactorBlockEntity blockEntity)) {
            return;
        }

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getCompactorRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getOutput(), output), player.level())
            .ifPresent(recipe -> {

                CompactorRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                ItemStack inventoryInput = TransferUtils.matchIngredientToItemStack(inventory.items, recipeCopy.getInput());
                ItemStack recipeInput = new ItemStack(inventoryInput.getItem(), recipeCopy.getInput().getCount());
                boolean creative = player.gameMode.isCreative();
                boolean canTransfer = (!inventoryInput.isEmpty() || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        ItemStack[] ingredientItems = recipeCopy.getInput().getIngredient().getItems();
                        ItemStack creativeInput = ingredientItems.length > 0 ? new ItemStack(ingredientItems[0].getItem(), recipeCopy.getInput().getCount()) : ItemStack.EMPTY;
                        if (creativeInput.isEmpty()) {
                            Alchemistry.LOGGER.warn("Skipping JEI transfer of recipe {}: input ingredient resolves to no items", recipeCopy.getId());
                            return;
                        }
                        int maxOperations = TransferUtils.getMaxOperations(creativeInput, maxTransfer);
                        inputHandler.setOrIncrement(0, new ItemStack(creativeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    } else {
                        int slot = inventory.findSlotMatchingItem(inventoryInput);
                        int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventory.getItem(slot), maxTransfer, false);
                        inventory.removeItem(slot, recipeCopy.getInput().getCount() * maxOperations);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    }
                    // A JEI transfer is an explicit player choice of one recipe, so it must register as a
                    // selection (progress reset + selection marker): a plain setRecipe would be replaced by
                    // the first cellulose match (acacia log) as soon as updateRecipe next ran -- which the
                    // setOrIncrement above already triggered through onContentsChanged.
                    blockEntity.selectRecipe(recipe);
                    blockEntity.setCanProcess(true);
                }
            });
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
                pContainer.getBlockEntity().setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new CompactorTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getOutput(), pMaxTransfer));
            }
            return null;
        }
    }
}
