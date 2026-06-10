package com.smashingmods.alchemistry.common.network.jei;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierBlockEntity;
import com.smashingmods.alchemistry.common.block.liquifier.LiquifierMenu;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import javax.annotation.Nullable;

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

        RecipeRegistry.getLiquifierRecipe(recipe -> recipe.getInput().getIngredient().items().map(holder -> new ItemStack(holder.value())).allMatch(input.getIngredient()), player.level())
            .ifPresent(recipe -> {

                LiquifierRecipe recipeCopy = recipe.copy();

                inputHandler.emptyToInventory(inventory);

                ItemStack inventoryInput = TransferUtils.matchIngredientToItemStack(inventory.getNonEquipmentItems(), recipeCopy.getInput());
                ItemStack recipeInput = new ItemStack(inventoryInput.getItem(), recipeCopy.getInput().getCount());
                boolean creative = player.gameMode.isCreative();
                boolean canTransfer = (!inventoryInput.isEmpty() || creative) && inputHandler.isEmpty() && blockEntity.getFluidStorage().isEmpty();

                if (canTransfer) {
                    if (creative) {
                        ItemStack creativeInput = new ItemStack(recipeCopy.getInput().getIngredient().items().findFirst().orElseThrow().value(), recipeCopy.getInput().getCount());
                        int maxOperations = TransferUtils.getMaxOperations(creativeInput, maxTransfer);
                        inputHandler.setOrIncrement(0, new ItemStack(creativeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    } else {
                        int slot = inventory.findSlotMatchingItem(inventoryInput);
                        int maxOperations = TransferUtils.getMaxOperations(recipeInput, inventory.getItem(slot), maxTransfer, false);
                        inventory.removeItem(slot, recipeCopy.getInput().getCount() * maxOperations);
                        inputHandler.setOrIncrement(0, new ItemStack(recipeInput.getItem(), recipeCopy.getInput().getCount() * maxOperations));
                    }
                    blockEntity.setProgress(0);
                    blockEntity.setRecipe(recipe);
                }
            });
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
                pContainer.getBlockEntity().setRecipe(pRecipe);
                Alchemistry.PACKET_HANDLER.sendToServer(new LiquifierTransferPacket(pContainer.getBlockEntity().getBlockPos(), pRecipe.getInput(), pMaxTransfer));
            }
            return null;
        }
    }
}
