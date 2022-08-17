package com.smashingmods.alchemistry.common.block.fission;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.PowerState;
import com.smashingmods.alchemistry.api.blockentity.ReactorType;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
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

public class FissionControllerBlockEntity extends AbstractReactorBlockEntity {

    protected final ContainerData data;
    private final int maxProgress = Config.Common.fissionTicksPerOperation.get();
    private FissionRecipe currentRecipe;

    public FissionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FISSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setReactorType(ReactorType.FISSION);
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
    public void tick() {
        if (!isProcessingPaused()) {
            if (!isRecipeLocked()) {
                updateRecipe();
            }
            if (canProcessRecipe()) {
                setPowerState(PowerState.ON);
                processRecipe();
            } else {
                if (getEnergyHandler().getEnergyStored() > Config.Common.fissionEnergyPerTick.get()) {
                    setPowerState(PowerState.STANDBY);
                } else {
                    setPowerState(PowerState.OFF);
                }
            }
        }
        super.tick();
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (!getInputHandler().getStackInSlot(0).isEmpty()) {
                RecipeRegistry.getRecipesByType(RecipeRegistry.FISSION_TYPE, level).stream()
                        .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), getInputHandler().getStackInSlot(0)))
                        .findFirst().ifPresent(recipe -> {
                            if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                                setProgress(0);
                                currentRecipe = recipe;
                            }
                        });
            } else {
                setProgress(0);
                currentRecipe = null;
            }
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = getInputHandler().getStackInSlot(0);
            ItemStack output1 = getOutputHandler().getStackInSlot(0);
            ItemStack output2 = getOutputHandler().getStackInSlot(1);
            return getEnergyHandler().getEnergyStored() >= Config.Common.fissionEnergyPerTick.get()
                    && (ItemStack.isSameItemSameTags(input, currentRecipe.getInput()) && input.getCount() >= currentRecipe.getInput().getCount())
                    && ((ItemStack.isSameItemSameTags(output1, currentRecipe.getOutput1()) || output1.isEmpty()) && (currentRecipe.getOutput1().getCount() + output1.getCount()) <= currentRecipe.getOutput1().getMaxStackSize())
                    && ((ItemStack.isSameItemSameTags(output2, currentRecipe.getOutput2()) || output2.isEmpty()) && (currentRecipe.getOutput2().getCount() + output2.getCount()) <= currentRecipe.getOutput2().getMaxStackSize());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getInputHandler().decrementSlot(0, currentRecipe.getInput().getCount());
            getOutputHandler().setOrIncrement(0, currentRecipe.getOutput1().copy());
            getOutputHandler().setOrIncrement(1, currentRecipe.getOutput2().copy());
        }
        getEnergyHandler().extractEnergy(Config.Common.fissionEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        currentRecipe = (FissionRecipe) pRecipe;
    }

    @Override
    public FissionRecipe getRecipe() {
        return currentRecipe;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.fissionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeInputHandler() {
        return new CustomItemStackHandler(1);
    }

    @Override
    public CustomItemStackHandler initializeOutputHandler() {
        return new CustomItemStackHandler(2) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FissionControllerMenu(pContainerId, pInventory, this, this.data);
    }
}
