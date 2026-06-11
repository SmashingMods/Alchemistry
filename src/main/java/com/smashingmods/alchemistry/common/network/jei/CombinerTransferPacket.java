package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.combiner.CombinerBlockEntity;
import com.smashingmods.alchemistry.common.block.combiner.CombinerMenu;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
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

                CombinerRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);
                outputHandler.emptyToInventory(inventory);

                List<ItemStack> inventoryInput = TransferUtils.matchIngredientListToItemStack(inventory.items, recipeCopy.getInput());

                boolean creative = player.gameMode.isCreative();
                // inventoryInput is index-parallel to the recipe input with EMPTY at every
                // unmatched ingredient. A partial match must not transfer: counts would pair
                // with the wrong ingredients and the placement loop would run past the matches.
                boolean fullMatch = !inventoryInput.isEmpty() && inventoryInput.stream().noneMatch(ItemStack::isEmpty);
                boolean canTransfer = (fullMatch || creative) && inputHandler.isEmpty() && outputHandler.isEmpty();

                if (canTransfer) {
                    if (creative) {
                        List<ItemStack> creativeInput = new ArrayList<>();

                        for (int i = 0; i < recipeCopy.getInput().size(); i++) {
                            IngredientStack ingredientStack = recipeCopy.getInput().get(i);
                            ItemStack[] ingredientItems = ingredientStack.getIngredient().getItems();
                            ItemStack item = ingredientItems.length > 0 ? new ItemStack(ingredientItems[0].getItem(), ingredientStack.getCount()) : ItemStack.EMPTY;
                            if (item.isEmpty()) {
                                Alchemistry.LOGGER.warn("Skipping input {} of recipe {} in JEI transfer: ingredient resolves to no items", i, recipeCopy.getId());
                            }
                            creativeInput.add(i, item);
                        }

                        // Unresolvable inputs stay EMPTY placeholders so slot indices line up; they must not
                        // reach getMaxOperations, where a zero-count stack would zero out every slot.
                        List<ItemStack> resolvedInput = creativeInput.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
                        if (resolvedInput.isEmpty()) {
                            return;
                        }

                        int maxOperations = TransferUtils.getMaxOperations(resolvedInput, maxTransfer);
                        for (int i = 0; i < recipeCopy.getInput().size(); i++) {
                            if (!creativeInput.get(i).isEmpty()) {
                                inputHandler.setOrIncrement(i, new ItemStack(creativeInput.get(i).getItem(), recipeCopy.getInput().get(i).getCount() * maxOperations));
                            }
                        }
                    } else {
                        List<ItemStack> recipeInput = new ArrayList<>();
                        IntStream.range(0, inventoryInput.size()).forEach(i -> recipeInput.add(new ItemStack(inventoryInput.get(i).getItem(), recipeCopy.getInput().get(i).getCount())));

                        List<ItemStack> inventoryStacks = new ArrayList<>();
                        inventoryInput.stream().map(inventory::findSlotMatchingItem).forEach(slot -> {
                            if (slot != -1) {
                                inventoryStacks.add(inventory.getItem(slot));
                            }
                        });

                        int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventoryStacks, maxTransfer, false);
                        recipeInput.forEach(itemStack -> {
                            int slot = player.getInventory().findSlotMatchingItem(itemStack);
                            player.getInventory().removeItem(slot, itemStack.getCount() * maxOperations);
                        });

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
                Alchemistry.PACKET_HANDLER.sendToServer(new CombinerTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getOutput(), pMaxTransfer));
            }
            return null;
        }
    }
}