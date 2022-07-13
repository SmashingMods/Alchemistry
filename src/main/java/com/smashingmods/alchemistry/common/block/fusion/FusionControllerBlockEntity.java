package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.*;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FusionControllerBlockEntity extends AbstractReactorBlockEntity {

    protected final ContainerData data;
    private final int maxProgress = Config.Common.fusionTicksPerOperation.get();
    private FusionRecipe currentRecipe;

    public FusionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setReactorType(ReactorType.FUSION);
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

    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (!getInputHandler().getStackInSlot(0).isEmpty()) {
                List<FusionRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.FUSION_TYPE, level).stream().toList();
                currentRecipe = recipes.stream().filter(recipe -> {
                    ItemStack input1 = getInputHandler().getStackInSlot(0);
                    ItemStack input2 = getInputHandler().getStackInSlot(1);
                    return ItemStack.isSameItemSameTags(recipe.getInput1(), input1) && ItemStack.isSameItemSameTags(recipe.getInput2(), input2)
                            || ItemStack.isSameItemSameTags(recipe.getInput2(), input1) && ItemStack.isSameItemSameTags(recipe.getInput1(), input2);
                }).findFirst().orElse(null);
            } else {
                currentRecipe = null;
            }
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input1 = getInputHandler().getStackInSlot(0);
            ItemStack input2 = getInputHandler().getStackInSlot(1);
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.fusionEnergyPerTick.get()
                    && ((ItemStack.isSameItemSameTags(input1, currentRecipe.getInput1()) && input1.getCount() >= currentRecipe.getInput1().getCount())
                        && (ItemStack.isSameItemSameTags(input2, currentRecipe.getInput2()) && input2.getCount() >= currentRecipe.getInput2().getCount()))
                    || ((ItemStack.isSameItemSameTags(input1, currentRecipe.getInput2()) && input1.getCount() >= currentRecipe.getInput2().getCount())
                        && (ItemStack.isSameItemSameTags(input2, currentRecipe.getInput1()) && input2.getCount() >= currentRecipe.getInput1().getCount()))
                    && ((ItemStack.isSameItemSameTags(output, currentRecipe.getOutput()) || output.isEmpty()) && (currentRecipe.getOutput().getCount() + output.getCount()) <= currentRecipe.getOutput().getMaxStackSize());
        }
        return false;
    }

    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getInputHandler().decrementSlot(0, currentRecipe.getInput1().getCount());
            getInputHandler().decrementSlot(1, currentRecipe.getInput2().getCount());
            getOutputHandler().setOrIncrement(0, currentRecipe.getOutput().copy());
        }
        getEnergyHandler().extractEnergy(Config.Common.fusionEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.fusionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeInputHandler() {
        return new CustomItemStackHandler(2);
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
        return new FusionControllerMenu(pContainerId, pInventory, this, this.data);
    }
}
