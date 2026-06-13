package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingMenu;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.LinkedList;

public class CombinerMenu extends AbstractProcessingMenu {

    private final Level level;
    private final CombinerBlockEntity blockEntity;
    private final LinkedList<CombinerRecipe> displayedRecipes = new LinkedList<>();

    public CombinerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, pInventory.player.level().getBlockEntity((pBuffer != null ? pBuffer.readBlockPos() : pInventory.player.blockPosition())));
    }

    protected CombinerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.COMBINER_MENU.get(), pContainerId, pInventory, pBlockEntity, 4, 1);

        this.level = pInventory.player.level();
        this.blockEntity = (pBlockEntity instanceof CombinerBlockEntity be) ? be : null;
        ProcessingSlotHandler inputHandler = (this.blockEntity != null) ? this.blockEntity.getInputHandler() : new ProcessingSlotHandler(4);
        ProcessingSlotHandler outputHandler = (this.blockEntity != null) ? this.blockEntity.getOutputHandler() : new ProcessingSlotHandler(1);
        addSlots(SlotItemHandler::new, inputHandler, 2, 2, 0, inputHandler.getSlots(), 48, 22);
        addSlots(SlotItemHandler::new, outputHandler, 1, 1, 0, outputHandler.getSlots(), 120, 31);
        if (this.blockEntity == null) return;
        setupRecipeList();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.isSpectator()) return false;
        if (this.getBlockEntity() == null) return false;
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, BlockRegistry.COMBINER.get());
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        return !getBlockEntity().isRecipeLocked() && isValidRecipeIndex(pId);
    }

    private boolean isValidRecipeIndex(int pSlot) {
        return pSlot >= 0 && pSlot < this.displayedRecipes.size();
    }

    private void setupRecipeList() {
        if (displayedRecipes.isEmpty()) {
            resetDisplayedRecipes();
        }
    }

    public void resetDisplayedRecipes() {
        displayedRecipes.clear();
        displayedRecipes.addAll(RecipeRegistry.getCombinerRecipes(level));
    }
}
