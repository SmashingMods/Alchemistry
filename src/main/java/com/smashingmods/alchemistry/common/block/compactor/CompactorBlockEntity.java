package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.*;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CompactorBlockEntity extends AbstractInventoryBlockEntity {

    protected final ContainerData data;
    private final int maxProgress = Config.Common.compactorTicksPerOperation.get();
    private CompactorRecipe currentRecipe;

    public CompactorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch(pIndex) {
                    case 0 -> getProgress();
                    case 1 -> maxProgress;
                    case 2 -> getEnergyHandler().getEnergyStored();
                    case 3 -> getEnergyHandler().getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> setProgress(pValue);
                    case 2 -> getEnergyHandler().setEnergy(pValue);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (!getInputHandler().getStackInSlot(0).isEmpty() && (currentRecipe == null || !ItemStack.isSameItemSameTags(currentRecipe.getOutput(), getOutputHandler().getStackInSlot(0)))) {
                if (!getInputHandler().getStackInSlot(1).isEmpty()) {
                    List<CompactorRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream().toList();
                    List<CompactorRecipe> filtered = recipes.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), getInputHandler().getStackInSlot(0))).collect(Collectors.toList());
                    if (filtered.size() > 1) {
                        currentRecipe = filtered.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), getInputHandler().getStackInSlot(1))).findFirst().orElse(null);
                    } else {
                        currentRecipe = !filtered.isEmpty() ? filtered.get(0) : null;
                    }
                }
            }
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = getInputHandler().getStackInSlot(0);
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.compactorEnergyPerTick.get()
                    && (ItemStack.isSameItemSameTags(input, currentRecipe.getInput()) && input.getCount() >= currentRecipe.getInput().getCount())
                    && (currentRecipe.getOutput().getCount() + output.getCount()) <= currentRecipe.getOutput().getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(output, currentRecipe.getOutput()) || output.isEmpty());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getOutputHandler().setOrIncrement(0, currentRecipe.getOutput().copy());
            getInputHandler().decrementSlot(0, currentRecipe.getInput().getCount());
        }
        getEnergyHandler().extractEnergy(Config.Common.compactorEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(T pRecipe) {
        currentRecipe = (CompactorRecipe) pRecipe;
    }

    @Override
    public Recipe<Inventory> getRecipe() {
        return currentRecipe;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.compactorEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeInputHandler() {
        return new CustomItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                if (level != null && !level.isClientSide()) {
                    if (slot == 1 && !getInputHandler().getStackInSlot(slot).isEmpty()) {
                        RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream().filter(recipe -> ItemStack.isSameItemSameTags(initializeInputHandler().getStackInSlot(slot), recipe.getOutput())).findFirst().ifPresent(recipe -> {
                            setRecipe(recipe);
                        });
                    }
                }
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeOutputHandler() {
        return new CustomItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CompactorMenu(pContainerId, pInventory, this, this.data);
    }
}
