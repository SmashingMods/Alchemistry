package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.block.compactor.CompactorMenu;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.types.IRecipeType;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import javax.annotation.Nullable;

import java.util.List;
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

                List<IngredientStack> recipeIngredients = buildRecipeIngredients(recipeCopy);
                List<TransferUtils.SlotMatch> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.getNonEquipmentItems(), recipeIngredients);

                boolean creative = player.gameMode.isCreative();
                boolean fullMatch = TransferUtils.isFullMatch(inventoryInput);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        ItemStack creativeInput = recipeCopy.getInput().getIngredient().items().findFirst()
                                .map(holder -> new ItemStack(holder.value(), recipeCopy.getInput().getCount()))
                                .orElse(ItemStack.EMPTY);
                        if (creativeInput.isEmpty()) {
                            Alchemistry.LOGGER.warn("Skipping JEI transfer of recipe {}: input ingredient resolves to no items", recipeCopy.getId());
                            return;
                        }
                        int maxOperations = TransferUtils.getMaxOperations(creativeInput, maxTransfer);
                        inputHandler.setOrIncrement(0, new ItemStack(creativeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    } else {
                        TransferUtils.SlotMatch slotMatch = inventoryInput.get(0);
                        // The placed item is whichever ingredient member the matched slot holds, captured
                        // before the removal: draining the claimed slot empties the live stack, whose
                        // getItem() then reports AIR.
                        Item matchedItem = slotMatch.itemStack().getItem();
                        int maxOperations = TransferUtils.getMaxOperations(inventoryInput, recipeIngredients, maxTransfer);
                        // Remove by the slot the input claimed during matching; the matcher bounded the
                        // claim by that slot's count, so the removal always succeeds in full.
                        inventory.removeItem(slotMatch.slot(), recipeCopy.getInput().getCount() * maxOperations);
                        inputHandler.setOrIncrement(0, new ItemStack(matchedItem, recipeCopy.getInput().getCount() * maxOperations));
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                    blockEntity.setCanProcess(true);
                }
            });
    }

    /**
     * The recipe's single ingredient input as a one-element joint-matchable list. Running the same
     * list matcher the multi-input packets use keeps the gate and the removal on one walk over the
     * main inventory: the old shape gated through {@code TransferUtils#matchIngredientToItemStack},
     * which only accepts a slot holding the full per-operation count, but located the removal slot
     * with the count-blind {@code Inventory#findSlotMatchingItem}, which returns the FIRST slot
     * holding the item -- with the input split across a partial stack at a low index and a full
     * stack behind it, the gate passed on the full stack while the removal drained the partial one
     * short of the recipe count, and the placement still inserted the full count, creating the
     * shortfall. Removing by the claimed {@link TransferUtils.SlotMatch#slot()} debits exactly the
     * stack that passed the gate.
     *
     * <p>Package-visible so the matching shape is unit-testable without a live server.</p>
     */
    static List<IngredientStack> buildRecipeIngredients(CompactorRecipe pRecipe) {
        return List.of(pRecipe.getInput());
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
        public IRecipeType<CompactorRecipe> getRecipeType() {
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
