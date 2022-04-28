package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CombinerMenu extends AbstractAlchemistryMenu {

    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private final CombinerBlockEntity blockEntity;
    private List<CombinerRecipe> recipes = new ArrayList<>();
    List<CombinerRecipe> displayedRecipes = new ArrayList<>();

    @SuppressWarnings("FieldCanBeLocal")
    private final CustomStackHandler inputHandler;
    private final CustomStackHandler outputHandler;

    public CombinerMenu(int pContainerId, Inventory pInventory, @Nonnull FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())), new SimpleContainerData(4));
    }

    protected CombinerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(MenuRegistry.COMBINER_MENU.get(), pContainerId, pInventory, pBlockEntity, pContainerData, 5);

        this.level = pInventory.player.getLevel();
        this.blockEntity = (CombinerBlockEntity) pBlockEntity;
        inputHandler = ((InventoryBlockEntity) blockEntity).getInputHandler();
        outputHandler= ((InventoryBlockEntity) blockEntity).getOutputHandler();

        setupRecipeList();

        // input 2x2 grid
        addSlots(SlotItemHandler::new, inputHandler, 2, 2, 0, 12, 51);
        // catalyst/solvent
        addSlots(SlotItemHandler::new, inputHandler, 1, 1, 4, 21, 21);
        // ouput
        addSlots(SlotItemHandler::new, outputHandler, 1, 1, 0, 102, 69);
    }

    @Override
    public void addPlayerInventorySlots(Inventory pInventory) {
        addSlots(Slot::new, pInventory, 3, 9, 9,12, 94);
        addSlots(Slot::new, pInventory, 1, 9, 0,12, 152);
    }

    @Override
    public boolean stillValid(@Nonnull Player pPlayer) {
        Objects.requireNonNull(this.getBlockEntity().getLevel());
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, BlockRegistry.COMBINER.get());
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<CombinerRecipe> getRecipes() {
        return this.recipes;
    }

    @Override
    public boolean clickMenuButton(@Nonnull Player pPlayer, int pId) {
        if (pPlayer.level.isClientSide()) {
            if (this.isValidRecipeIndex(pId)) {
                this.selectedRecipeIndex.set(pId);
                this.blockEntity.setCurrentRecipe(this.displayedRecipes.get(this.selectedRecipeIndex.get()));
            }
        }
        return true;
    }

    private boolean isValidRecipeIndex(int pSlot) {
        return pSlot >= 0 && pSlot < this.displayedRecipes.size();
    }

    private void setupRecipeList() {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.outputHandler.setStackInSlot(0, ItemStack.EMPTY);
        this.recipes = level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == RecipeRegistry.COMBINER_TYPE)
                .map(recipe -> (CombinerRecipe) recipe).sorted()
                .collect(Collectors.toList());
        this.resetDisplayedRecipes();
    }

    public void resetDisplayedRecipes() {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(recipes);
    }

    public void searchRecipeList(String pKeyword) {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(recipes.stream().filter(recipe -> {
            Objects.requireNonNull(recipe.output.getItem().getRegistryName());
            return recipe.output.getItem().getRegistryName().getPath().contains(pKeyword);
        }).collect(Collectors.toList()));
    }
}
