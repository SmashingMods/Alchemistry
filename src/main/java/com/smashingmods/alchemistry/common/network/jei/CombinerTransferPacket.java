package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class CombinerTransferPacket implements AlchemyPacket {

    public static final Type<CombinerTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "combiner_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CombinerTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.output,
            ByteBufCodecs.BOOL, packet -> packet.maxTransfer,
            CombinerTransferPacket::new
    );

    private final BlockPos blockPos;
    private final ItemStack output;
    private final boolean maxTransfer;

    public CombinerTransferPacket(BlockPos pBlockPos, ItemStack pOutput, boolean pMaxTransfer) {
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
        if (!(player.level().getBlockEntity(blockPos) instanceof CombinerBlockEntity blockEntity)) {
            return;
        }

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getCombinerRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getOutput(), output), player.level())
            .ifPresent(recipe -> {

                // A locked machine's recipe must not change: selectRecipe below refuses under the lock,
                // so the moved items would feed a recipe the machine refuses to run. Same-recipe
                // transfers (refilling the locked recipe's inputs) stay allowed.
                AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
                if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(recipe.getId()))) {
                    return;
                }

                CombinerRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                List<TransferUtils.SlotMatch> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.items, recipeCopy.getInput());

                boolean creative = player.gameMode.isCreative();
                boolean fullMatch = TransferUtils.isFullMatch(inventoryInput);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        List<ItemStack> creativeTransfer = buildCreativeTransfer(recipeCopy, maxTransfer);
                        if (creativeTransfer.isEmpty()) {
                            return;
                        }
                        for (int i = 0; i < creativeTransfer.size(); i++) {
                            if (!creativeTransfer.get(i).isEmpty()) {
                                inputHandler.setOrIncrement(i, creativeTransfer.get(i));
                            }
                        }
                    } else {
                        // Items snapshot from the matched stacks before any removal -- removing an
                        // earlier ingredient's share can empty a stack a later index still places from.
                        List<ItemStack> recipeInput = new ArrayList<>();
                        IntStream.range(0, inventoryInput.size()).forEach(i -> recipeInput.add(new ItemStack(inventoryInput.get(i).itemStack().getItem(), recipeCopy.getInput().get(i).getCount())));

                        int maxOperations = TransferUtils.getMaxOperations(inventoryInput, recipeCopy.getInput(), maxTransfer);
                        // Remove by the slot each ingredient claimed during matching. The joint match
                        // already bounded the claims by the slots' counts, so every removal succeeds in
                        // full and the placement below mirrors exactly what left the inventory.
                        IntStream.range(0, inventoryInput.size()).forEach(i ->
                                inventory.removeItem(inventoryInput.get(i).slot(), recipeInput.get(i).getCount() * maxOperations));

                        for (int i = 0; i < recipeCopy.getInput().size(); i++) {
                            inputHandler.setOrIncrement(i, new ItemStack(recipeInput.get(i).getItem(), recipeCopy.getInput().get(i).getCount() * maxOperations));
                        }
                    }
                    // A JEI transfer is an explicit player choice of one recipe, so it must register as a
                    // selection (progress reset + selection marker): a plain setRecipe would be replaced by
                    // the first-sorted auto-pick on the next tick, since the transferred inputs (e.g.
                    // oxygen + cellulose) are shared by every sapling recipe.
                    blockEntity.selectRecipe(recipe);
                    blockEntity.setCanProcess(true);
                }
            });
    }

    /**
     * Computes the stacks a creative transfer places, index-parallel to the recipe input slots
     * (EMPTY = leave that slot untouched). An input whose ingredient resolves to no items (empty
     * tag, custom ingredient resolving empty) logs a warning and stays an EMPTY placeholder so the
     * remaining inputs keep their slot indices; the placeholders are excluded from
     * getMaxOperations, where a zero-count stack would zero out every slot. Returns an empty list
     * when no input resolves at all -- nothing to transfer.
     *
     * <p>Package-visible so the slot alignment and operation math are unit-testable without a live server.</p>
     */
    static List<ItemStack> buildCreativeTransfer(CombinerRecipe pRecipe, boolean pMaxTransfer) {
        List<ItemStack> creativeInput = new ArrayList<>();

        for (int i = 0; i < pRecipe.getInput().size(); i++) {
            IngredientStack ingredientStack = pRecipe.getInput().get(i);
            ItemStack[] ingredientItems = ingredientStack.getIngredient().getItems();
            ItemStack item = ingredientItems.length > 0 ? new ItemStack(ingredientItems[0].getItem(), ingredientStack.getCount()) : ItemStack.EMPTY;
            if (item.isEmpty()) {
                Alchemistry.LOGGER.warn("Skipping input {} of recipe {} in JEI transfer: ingredient resolves to no items", i, pRecipe.getId());
            }
            creativeInput.add(i, item);
        }

        List<ItemStack> resolvedInput = creativeInput.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        if (resolvedInput.isEmpty()) {
            return List.of();
        }

        int maxOperations = TransferUtils.getMaxOperations(resolvedInput, pMaxTransfer);
        List<ItemStack> toPlace = new ArrayList<>();
        for (int i = 0; i < pRecipe.getInput().size(); i++) {
            ItemStack item = creativeInput.get(i);
            toPlace.add(item.isEmpty() ? ItemStack.EMPTY : new ItemStack(item.getItem(), pRecipe.getInput().get(i).getCount() * maxOperations));
        }
        return toPlace;
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
                // Mirror of the server handler's lock guard: the server refuses a transfer that would
                // change a locked machine's recipe, so the client must not repaint its display recipe
                // either -- the screen would show the new recipe while the machine runs the locked one.
                AbstractProcessingBlockEntity blockEntity = pContainer.getBlockEntity();
                AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
                if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(pRecipe.getId()))) {
                    return null;
                }
                blockEntity.setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new CombinerTransferPacket(blockEntity.getBlockPos(), pRecipe.getOutput(), pMaxTransfer));
            }
            return null;
        }
    }
}