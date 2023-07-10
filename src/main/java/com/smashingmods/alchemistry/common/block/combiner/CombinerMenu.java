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
import net.minecraftforge.items.SlotItemHandler;

import java.util.LinkedList;
import java.util.Objects;

public class CombinerMenu extends AbstractProcessingMenu {

    private final Level level;
    private final CombinerBlockEntity blockEntity;
    private final LinkedList<CombinerRecipe> displayedRecipes = new LinkedList<>();

    public CombinerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level().getBlockEntity(pBuffer.readBlockPos())));
    }

    protected CombinerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.COMBINER_MENU.get(), pContainerId, pInventory, pBlockEntity, 4, 1);

        this.level = pInventory.player.level();
        this.blockEntity = (CombinerBlockEntity) pBlockEntity;
        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();

        setupRecipeList();
        // input 2x2 grid
        addSlots(SlotItemHandler::new, inputHandler, 2, 2, 0, inputHandler.getSlots(), 48, 22);
        // output
        addSlots(SlotItemHandler::new, outputHandler, 1, 1, 0, outputHandler.getSlots(), 120, 31);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        Objects.requireNonNull(this.getBlockEntity().getLevel());
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
