package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryMenu;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.CombinerRecipePacket;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.MenuRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CombinerMenu extends AbstractAlchemistryMenu {

    private final Level level;
    private final CombinerBlockEntity blockEntity;
    private final List<CombinerRecipe> displayedRecipes = new ArrayList<>();

    public CombinerMenu(int pContainerId, Inventory pInventory, FriendlyByteBuf pBuffer) {
        this(pContainerId, pInventory, Objects.requireNonNull(pInventory.player.level.getBlockEntity(pBuffer.readBlockPos())), new SimpleContainerData(5));
    }

    protected CombinerMenu(int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, ContainerData pContainerData) {
        super(MenuRegistry.COMBINER_MENU.get(), pContainerId, pInventory, pBlockEntity, pContainerData, 4);

        this.level = pInventory.player.getLevel();
        this.blockEntity = (CombinerBlockEntity) pBlockEntity;
        CustomItemStackHandler inputHandler = blockEntity.getInputHandler();
        CustomItemStackHandler outputHandler = blockEntity.getOutputHandler();

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
        if(pPlayer.level.isClientSide()) {
            if (!getBlockEntity().isRecipeLocked()) {
                if (this.isValidRecipeIndex(pId)) {
                    int recipeIndex = blockEntity.getRecipes().indexOf(displayedRecipes.get(pId));
                    CombinerRecipe recipe = blockEntity.getRecipes().get(recipeIndex);
                    this.setSelectedRecipeIndex(pId);
                    this.blockEntity.setRecipe(recipe);
                    AlchemistryPacketHandler.INSTANCE.sendToServer(new CombinerRecipePacket(getBlockEntity().getBlockPos(), recipeIndex));
                }
            }
        }
        return true;
    }

    protected int getSelectedRecipeIndex() {
        return this.getContainerData().get(4);
    }

    protected void setSelectedRecipeIndex(int pIndex) {
        this.getContainerData().set(4, pIndex);
    }

    private boolean isValidRecipeIndex(int pSlot) {
        return pSlot >= 0 && pSlot < this.displayedRecipes.size();
    }

    private void setupRecipeList() {
        this.blockEntity.getRecipes().clear();
        List<CombinerRecipe> recipes = level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == RecipeRegistry.COMBINER_TYPE)
                .map(recipe -> (CombinerRecipe) recipe).sorted()
                .collect(Collectors.toList());

        if (displayedRecipes.isEmpty()) {
            this.setSelectedRecipeIndex(-1);
            this.blockEntity.setRecipes(recipes);
            this.resetDisplayedRecipes();
        }
    }

    public void resetDisplayedRecipes() {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(this.blockEntity.getRecipes());
    }

    public List<CombinerRecipe> getDisplayedRecipes() {
        return displayedRecipes;
    }

    public void searchRecipeList(String pKeyword) {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(this.blockEntity.getRecipes().stream().filter(recipe -> {
            Objects.requireNonNull(recipe.getOutput().getItem().getRegistryName());
            return recipe.getOutput().getItem().getRegistryName().getPath().contains(pKeyword.toLowerCase().replace(" ", "_"));
        }).toList());
    }
}
