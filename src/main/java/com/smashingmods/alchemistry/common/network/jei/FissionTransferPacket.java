package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fission.FissionControllerMenu;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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

import java.util.List;
import java.util.Optional;

public class FissionTransferPacket implements AlchemyPacket {

    public static final Type<FissionTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "fission_transfer"));

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

                // A locked machine's recipe must not change: setRecipe below would repoint it, and the
                // moved items would feed a recipe the machine refuses to run. Same-recipe transfers
                // (refilling the locked recipe's inputs) stay allowed.
                AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
                if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(recipe.getId()))) {
                    return;
                }

                FissionRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHander.emptyToInventory(inventory);

                List<IngredientStack> recipeIngredients = buildRecipeIngredients(recipeCopy);
                List<TransferUtils.SlotMatch> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.getNonEquipmentItems(), recipeIngredients);

                boolean creative = player.gameMode.isCreative();
                boolean fullMatch = TransferUtils.isFullMatch(inventoryInput);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty() && outputHander.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        int maxOperations = TransferUtils.getMaxOperations(recipeCopy.getInput(), maxTransfer);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput().getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    } else {
                        int maxOperations = TransferUtils.getMaxOperations(inventoryInput, recipeIngredients, maxTransfer);
                        // Remove by the slot the input claimed during matching; the matcher bounded the
                        // claim by that slot's count, so the removal always succeeds in full.
                        inventory.removeItem(inventoryInput.get(0).slot(), recipeCopy.getInput().getCount() * maxOperations);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput().getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                }
            });
    }

    /**
     * The recipe's single input as a one-element joint-matchable ingredient list carrying the
     * input's count. Running the same list matcher the multi-input packets use keeps the gate and
     * the removal on one walk over the main inventory: the old shape gated on
     * {@code Inventory#contains}, which scans every compartment (offhand included), but removed via
     * {@code Inventory#findSlotMatchingItem}, which only scans the main inventory -- so an input
     * held only in the offhand passed the gate, the slot lookup returned -1, and
     * {@code getItem(-1)} disconnected the player. An {@code EMPTY} match (absent or count-short
     * input) now simply fails the full-match gate instead.
     *
     * <p>Package-visible so the matching shape is unit-testable without a live server.</p>
     */
    static List<IngredientStack> buildRecipeIngredients(FissionRecipe pRecipe) {
        return List.of(new IngredientStack(pRecipe.getInput()));
    }

    public static class TransferHandler implements IRecipeTransferHandler<FissionControllerMenu, FissionRecipe> {

        private final IRecipeTransferHandlerHelper transferHelper;

        public TransferHandler(IRecipeTransferHandlerHelper pTransferHelper) {
            this.transferHelper = pTransferHelper;
        }

        @Override
        public Class<FissionControllerMenu> getContainerClass() {
            return FissionControllerMenu.class;
        }

        @Override
        public Optional<MenuType<FissionControllerMenu>> getMenuType() {
            return Optional.of(MenuRegistry.FISSION_CONTROLLER_MENU.get());
        }

        @Override
        public IRecipeType<FissionRecipe> getRecipeType() {
            return RecipeTypes.FISSION;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(FissionControllerMenu pContainer, FissionRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            // Mirror of the server handler's lock guard: the server refuses a transfer that would
            // change a locked machine's recipe, so the client must not repaint its display recipe
            // either -- the screen would show the new recipe while the machine runs the locked one.
            // Sits outside the pDoTransfer branch so JEI's gating pass greys the transfer button,
            // and returns a user error rather than null -- JEI reads null as success, which left
            // the button looking alive while the click did nothing.
            AbstractProcessingBlockEntity blockEntity = pContainer.getBlockEntity();
            AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
            if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(pRecipe.getId()))) {
                return transferHelper.createUserErrorWithTooltip(Component.translatable("alchemistry.jei.recipe_locked"));
            }
            if (pDoTransfer) {
                blockEntity.setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new FissionTransferPacket(blockEntity.getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}