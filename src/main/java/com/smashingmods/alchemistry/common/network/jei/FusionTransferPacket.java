package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerMenu;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public class FusionTransferPacket implements AlchemyPacket {

    public static final Type<FusionTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "fusion_transfer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FusionTransferPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.input1,
            ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.input2,
            ByteBufCodecs.BOOL, packet -> packet.maxTransfer,
            FusionTransferPacket::new
    );

    private final BlockPos blockPos;
    private final ItemStack input1;
    private final ItemStack input2;
    private final boolean maxTransfer;

    public FusionTransferPacket(BlockPos pBlockPos, ItemStack pInput1, ItemStack pInput2, boolean pMaxTransfer) {
        this.blockPos = pBlockPos;
        this.input1 = pInput1;
        this.input2 = pInput2;
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
        if (!(player.level().getBlockEntity(blockPos) instanceof FusionControllerBlockEntity blockEntity)) {
            return;
        }

        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();
        Inventory inventory = player.getInventory();

        RecipeRegistry.getFusionRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getInput1(), input1) && ItemStack.isSameItemSameComponents(recipe.getInput2(), input2), player.level())
            .ifPresent(recipe -> {

                // A locked machine's recipe must not change: setRecipe below would repoint it, and the
                // moved items would feed a recipe the machine refuses to run. Same-recipe transfers
                // (refilling the locked recipe's inputs) stay allowed.
                AbstractProcessingRecipe lockedRecipe = blockEntity.getRecipe();
                if (blockEntity.isRecipeLocked() && (lockedRecipe == null || !lockedRecipe.getId().equals(recipe.getId()))) {
                    return;
                }

                FusionRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                List<IngredientStack> recipeIngredients = buildRecipeIngredients(recipeCopy);
                List<TransferUtils.SlotMatch> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.getNonEquipmentItems(), recipeIngredients);

                boolean creative = player.gameMode.isCreative();
                boolean fullMatch = TransferUtils.isFullMatch(inventoryInput);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        int maxOperations = TransferUtils.getMaxOperations(List.of(recipeCopy.getInput1(), recipeCopy.getInput2()), maxTransfer);

                        inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput1().getItem(), recipeCopy.getInput1().getCount() * maxOperations));
                        inputHandler.setOrIncrement(1, new ItemStack(recipeCopy.getInput2().getItem(), recipeCopy.getInput2().getCount() * maxOperations));
                    } else {
                        int maxOperations = TransferUtils.getMaxOperations(inventoryInput, recipeIngredients, maxTransfer);

                        // Remove by the slot each input claimed during matching. The joint match bounded
                        // the claims by the slots' counts and the operation bound divides a shared slot by
                        // the TOTAL claim on it, so a same-element recipe (both inputs drawing on one
                        // stack) removes exactly what the placement below inserts -- two independent slot
                        // lookups resolved the same stack twice, let the second removal come back empty,
                        // and inserted items that never left the inventory.
                        inventory.removeItem(inventoryInput.get(0).slot(), recipeCopy.getInput1().getCount() * maxOperations);
                        inventory.removeItem(inventoryInput.get(1).slot(), recipeCopy.getInput2().getCount() * maxOperations);

                        inputHandler.setOrIncrement(0, new ItemStack(recipeCopy.getInput1().getItem(), recipeCopy.getInput1().getCount() * maxOperations));
                        inputHandler.setOrIncrement(1, new ItemStack(recipeCopy.getInput2().getItem(), recipeCopy.getInput2().getCount() * maxOperations));
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                }
            });
    }

    /**
     * The recipe's two inputs as a joint-matchable ingredient list, index-parallel to the machine's
     * input slots and carrying each input's count. Fusion inputs are concrete {@link ItemStack}s
     * rather than the combiner's {@code IngredientStack}s, so this wrap is what lets
     * {@link TransferUtils#matchIngredientListToItemStack} claim main-inventory slots jointly: most
     * shipped fusion recipes fuse an element with itself, and matching each input independently
     * resolved the same stack twice -- authorizing more removals than the stack held and duplicating
     * the shortfall. The matcher also only ever walks the main inventory, unlike the old
     * {@code Inventory#contains} gate, whose offhand hits made the main-only slot lookup return -1
     * and disconnected the player on {@code getItem(-1)}.
     *
     * <p>Package-visible so the joint-claim math is unit-testable without a live server.</p>
     */
    static List<IngredientStack> buildRecipeIngredients(FusionRecipe pRecipe) {
        return List.of(new IngredientStack(pRecipe.getInput1()), new IngredientStack(pRecipe.getInput2()));
    }

    public static class TransferHandler implements IRecipeTransferHandler<FusionControllerMenu, FusionRecipe> {

        public TransferHandler() {}

        @Override
        public Class<FusionControllerMenu> getContainerClass() {
            return FusionControllerMenu.class;
        }

        @Override
        public Optional<MenuType<FusionControllerMenu>> getMenuType() {
            return Optional.of(MenuRegistry.FUSION_CONTROLLER_MENU.get());
        }

        @Override
        public IRecipeType<FusionRecipe> getRecipeType() {
            return RecipeTypes.FUSION;
        }

        @Override
        public @Nullable IRecipeTransferError transferRecipe(FusionControllerMenu pContainer, FusionRecipe pRecipe, IRecipeSlotsView pRecipeSlots, Player pPlayer, boolean pMaxTransfer, boolean pDoTransfer) {
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
                Alchemistry.PACKET_HANDLER.sendToServer(new FusionTransferPacket(blockEntity.getBlockPos(), pRecipe.getInput1(), pRecipe.getInput2(), pMaxTransfer));
            }
            return null;
        }
    }
}