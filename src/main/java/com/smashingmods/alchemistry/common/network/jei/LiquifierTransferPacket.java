package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierMenu;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public class LiquifierTransferPacket implements AlchemyPacket {

    public static final Type<LiquifierTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "liquifier_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LiquifierTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            AlchemistryRecipeCodecs.INGREDIENT_STACK_STREAM_CODEC, packet -> packet.input,
            ByteBufCodecs.BOOL, packet -> packet.maxTransfer,
            LiquifierTransferPacket::new
    );

    private final BlockPos blockPos;
    private final IngredientStack input;
    private final boolean maxTransfer;

    public LiquifierTransferPacket(BlockPos pBlockPos, IngredientStack pInput, boolean pMaxTransfer) {
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
        if (!(player.level().getBlockEntity(blockPos) instanceof LiquifierBlockEntity blockEntity)) {
            return;
        }
        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getLiquifierRecipe(recipe -> matchesTransferredInput(recipe, input), player.level())
            .ifPresent(recipe -> {

                // A locked machine's recipe must not change: setRecipe below would repoint it, and the
                // moved items would feed a recipe the machine refuses to run. Same-recipe transfers
                // (refilling the locked recipe's inputs) stay allowed.
                AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
                if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(recipe.getId()))) {
                    return;
                }

                LiquifierRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);

                List<IngredientStack> recipeIngredients = buildRecipeIngredients(recipeCopy);
                List<TransferUtils.SlotMatch> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.items, recipeIngredients);

                boolean creative = player.gameMode.isCreative();
                boolean fullMatch = TransferUtils.isFullMatch(inventoryInput);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty()
                        && tankAccepts(blockEntity.getFluidStorage().getFluidStack(), recipeCopy.getOutput());

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
                }
            });
    }

    /**
     * The recipe-lookup predicate: whether every item the candidate recipe's ingredient resolves to
     * matches the ingredient the packet carried. allMatch is vacuously true on an empty resolution,
     * and the registry lookup takes the first hit -- without the findAny check one empty-resolving
     * recipe would shadow every real recipe.
     *
     * <p>Package-visible so the lookup is unit-testable across the packet's real network round-trip
     * without a live server.</p>
     */
    static boolean matchesTransferredInput(LiquifierRecipe pRecipe, IngredientStack pInput) {
        Ingredient recipeIngredient = pRecipe.getInput().getIngredient();
        return recipeIngredient.items().findAny().isPresent()
                && recipeIngredient.items().map(holder -> new ItemStack(holder.value())).allMatch(pInput.getIngredient());
    }

    /**
     * Whether the output tank's content permits transferring this recipe: the tank is empty or
     * already holds the recipe's output fluid -- the same fluid acceptance
     * {@code LiquifierBlockEntity#canProcessRecipe} runs, so a transfer is refused exactly when the
     * machine could not process the recipe anyway (a different fluid occupying the tank). The old
     * gate required the tank to be EMPTY outright: unlike the item machines, whose output slots the
     * handler first empties into the player inventory, a fluid tank cannot be emptied that way, so
     * the fluid produced by the machine's very first operation permanently vetoed every later JEI
     * transfer -- survival and creative alike -- until the tank was piped out.
     *
     * <p>Package-visible so the gate that killed the transfer button is unit-testable without a
     * live block entity.</p>
     */
    static boolean tankAccepts(FluidStack pTankFluid, FluidStack pRecipeOutput) {
        return pTankFluid.isEmpty() || FluidStack.isSameFluidSameComponents(pTankFluid, pRecipeOutput);
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
     * shortfall. Every shipped liquifier recipe takes 8 of its element, so any partial stack
     * exposed it. Removing by the claimed {@link TransferUtils.SlotMatch#slot()} debits exactly
     * the stack that passed the gate.
     *
     * <p>Package-visible so the matching shape is unit-testable without a live server.</p>
     */
    static List<IngredientStack> buildRecipeIngredients(LiquifierRecipe pRecipe) {
        return List.of(pRecipe.getInput());
    }

    public static class TransferHandler implements IRecipeTransferHandler<LiquifierMenu, LiquifierRecipe> {

        public TransferHandler() {}

        @Override
        public Class<LiquifierMenu> getContainerClass() {
            return LiquifierMenu.class;
        }

        @Override
        public Optional<MenuType<LiquifierMenu>> getMenuType() {
            return Optional.of(MenuRegistry.LIQUIFIER_MENU.get());
        }

        @Override
        public IRecipeType<LiquifierRecipe> getRecipeType() {
            return RecipeTypes.LIQUIFIER;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(LiquifierMenu pContainer, LiquifierRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
            if (pDoTransfer) {
                // Mirror of the server handler's lock guard: the server refuses a transfer that would
                // change a locked machine's recipe, so the client must not repaint its display recipe
                // either -- the screen would show the new recipe while the machine runs the locked one.
                AbstractProcessingBlockEntity blockEntity = pContainer.getBlockEntity();
                AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
                if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(pRecipe.getId()))) {
                    return null;
                }
                blockEntity.setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new LiquifierTransferPacket(blockEntity.getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}
