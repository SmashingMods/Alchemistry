package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.api.blockentity.container.AbstractProcessingMenu;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
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
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())));
    }

    protected CombinerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity) {
        super(MenuRegistry.COMBINER_MENU.get(), pContainerId, pInventory, pBlockEntity, 4, 1);

        this.level = pInventory.player.getLevel();
        this.blockEntity = (CombinerBlockEntity) pBlockEntity;
        ProcessingSlotHandler inputHandler = blockEntity.getInputHandler();
        ProcessingSlotHandler outputHandler = blockEntity.getOutputHandler();

        setupRecipeList();
        // input 2x2 grid
        addSlots(SlotItemHandler::new, inputHandler, 2, 2, 0, inputHandler.getSlots(), 12, 63);
        // output
        addSlots(SlotItemHandler::new, outputHandler, 1, 1, 0, outputHandler.getSlots(), 102, 81);
    }

    @Override
    public void addPlayerInventorySlots(Inventory pInventory) {
        addSlots(Slot::new, pInventory, 3, 9, 9, 27,12, 106);
        addSlots(Slot::new, pInventory, 1, 9, 0,9, 12, 164);
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

    public LinkedList<CombinerRecipe> getDisplayedRecipes() {
        return displayedRecipes;
    }

    public void searchRecipeList(String pKeyword) {
        displayedRecipes.clear();
        displayedRecipes.addAll(RecipeRegistry.getCombinerRecipes(level).stream()
                .filter(recipe -> {
                    Item item = recipe.getOutput().getItem();
                    if (pKeyword.charAt(0) == '@') {
                        Objects.requireNonNull(item.getRegistryName());
                        boolean space = pKeyword.contains(" ");
                        boolean namespace = item.getRegistryName().getNamespace().contains(space ? pKeyword.split(" ")[0] : pKeyword.substring(1));
                        boolean path = item.getRegistryName().getPath().contains(pKeyword.split(" ")[1]);
                        if (space) {
                            return namespace && path;
                        } else {
                            return namespace;
                        }
                    }
                    return recipe.getOutput().getItem().getDescription().getString().toLowerCase().contains(pKeyword.toLowerCase());
                })
                .toList());
    }
}
