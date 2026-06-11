package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverBlockEntity;
import com.smashingmods.alchemistry.common.block.dissolver.DissolverMenu;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DissolverTransferPacket implements AlchemyPacket {

    public static final Type<DissolverTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "dissolver_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DissolverTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            AlchemistryRecipeCodecs.INGREDIENT_STACK_STREAM_CODEC, packet -> packet.input,
            ByteBufCodecs.BOOL, packet -> packet.maxTransfer,
            DissolverTransferPacket::new
    );

    private final BlockPos blockPos;
    private final IngredientStack input;
    private final boolean maxTransfer;

    public DissolverTransferPacket(BlockPos pBlockPos, IngredientStack pInput, boolean pMaxTransfer) {
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
        if (!(player.level().getBlockEntity(blockPos) instanceof DissolverBlockEntity blockEntity)) {
            return;
        }

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        // allMatch is vacuously true on an empty resolution, and the registry lookup takes the first
        // hit -- without the length check one empty-resolving recipe would shadow every real recipe.
        RecipeRegistry.getDissolverRecipe(recipe -> {
            ItemStack[] recipeItems = recipe.getInput().getIngredient().getItems();
            return recipeItems.length > 0 && Arrays.stream(recipeItems).allMatch(input.getIngredient());
        }, player.level())
            .ifPresent(recipe -> {

                DissolverRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                List<IngredientStack> recipeIngredients = buildRecipeIngredients(recipeCopy);
                List<TransferUtils.SlotMatch> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.items, recipeIngredients);

                boolean creative = player.gameMode.isCreative();
                boolean fullMatch = TransferUtils.isFullMatch(inventoryInput);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

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
     * The recipe's single ingredient input as a one-element joint-matchable list. Running the same
     * list matcher the multi-input packets use keeps the gate and the removal on one walk over the
     * main inventory: the old shape gated through {@code TransferUtils#matchIngredientToItemStack},
     * which only accepts a slot holding the full per-operation count, but located the removal slot
     * with the count-blind {@code Inventory#findSlotMatchingItem}, which returns the FIRST slot
     * holding the item -- with the input split across a partial stack at a low index and a full
     * stack behind it, the gate passed on the full stack while the removal drained the partial one
     * short of the recipe count, and the placement still inserted the full count, creating the
     * shortfall. Every shipped dissolver recipe takes a single input item, so the mismatch never
     * created items here in practice; a datapack recipe with count >= 2 would have, and the port
     * keeps all six transfer packets on the one joint-claim pattern. Removing by the claimed
     * {@link TransferUtils.SlotMatch#slot()} debits exactly the stack that passed the gate.
     *
     * <p>Package-visible so the matching shape is unit-testable without a live server.</p>
     */
    static List<IngredientStack> buildRecipeIngredients(DissolverRecipe pRecipe) {
        return List.of(pRecipe.getInput());
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