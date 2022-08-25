package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorType;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.common.blockentity.power.PowerState;
import com.smashingmods.alchemylib.common.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.common.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FusionControllerBlockEntity extends AbstractReactorBlockEntity {

    private final int maxProgress = Config.Common.fusionTicksPerOperation.get();
    private FusionRecipe currentRecipe;

    public FusionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setReactorType(ReactorType.FUSION);
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
                if (getEnergyHandler().getEnergyStored() > Config.Common.fusionEnergyPerTick.get()) {
                    setPowerState(PowerState.STANDBY);
                } else {
                    setPowerState(PowerState.OFF);
                }
            }
        }
        super.tick();
    }

    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (!getInputHandler().getStackInSlot(0).isEmpty()) {
                RecipeRegistry.getRecipesByType(RecipeRegistry.FUSION_TYPE, level).stream()
                        .filter(recipe -> {
                            ItemStack input1 = getInputHandler().getStackInSlot(0);
                            ItemStack input2 = getInputHandler().getStackInSlot(1);
                            return ItemStack.isSameItemSameTags(recipe.getInput1(), input1) && ItemStack.isSameItemSameTags(recipe.getInput2(), input2)
                                    || ItemStack.isSameItemSameTags(recipe.getInput2(), input1) && ItemStack.isSameItemSameTags(recipe.getInput1(), input2);
                        })
                        .findFirst()
                        .ifPresent(recipe -> {
                            if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                                setProgress(0);
                                currentRecipe = recipe;
                            }
                        });
            }
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input1 = getInputHandler().getStackInSlot(0);
            ItemStack input2 = getInputHandler().getStackInSlot(1);
            ItemStack output = getOutputHandler().getStackInSlot(0);
            return getEnergyHandler().getEnergyStored() >= Config.Common.fusionEnergyPerTick.get()
                    && (((ItemStack.isSameItemSameTags(input1, currentRecipe.getInput1()) && input1.getCount() >= currentRecipe.getInput1().getCount())
                        && (ItemStack.isSameItemSameTags(input2, currentRecipe.getInput2()) && input2.getCount() >= currentRecipe.getInput2().getCount()))
                    || ((ItemStack.isSameItemSameTags(input1, currentRecipe.getInput2()) && input1.getCount() >= currentRecipe.getInput2().getCount())
                        && (ItemStack.isSameItemSameTags(input2, currentRecipe.getInput1()) && input2.getCount() >= currentRecipe.getInput1().getCount())))
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
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        currentRecipe = (FusionRecipe) pRecipe;
    }

    @Override
    public Recipe<Inventory> getRecipe() {
        return currentRecipe;
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.fusionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeInputHandler() {
        return new ProcessingSlotHandler(2);
    }

    @Override
    public ProcessingSlotHandler initializeOutputHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("maxProgress", maxProgress);
        super.saveAdditional(pTag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FusionControllerMenu(pContainerId, pInventory, this);
    }
}
