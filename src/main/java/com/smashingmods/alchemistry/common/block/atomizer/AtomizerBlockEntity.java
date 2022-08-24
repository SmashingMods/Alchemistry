package com.smashingmods.alchemistry.common.block.atomizer;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractFluidBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class AtomizerBlockEntity extends AbstractFluidBlockEntity {

    private final int maxProgress = Config.Common.atomizerTicksPerOperation.get();
    private AtomizerRecipe currentRecipe;

    public AtomizerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getRecipesByType(RecipeRegistry.ATOMIZER_TYPE, level).stream()
                    .filter(recipe -> recipe.getInput().getFluid().equals(getFluidStorage().getFluidStack().getFluid()))
                    .findFirst()
                    .ifPresent(recipe -> {
                        if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                            setProgress(0);
                            currentRecipe = recipe;
                        }
                    });
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            return getEnergyHandler().getEnergyStored() >= Config.Common.atomizerEnergyPerTick.get()
                    && getFluidStorage().getFluidAmount() >= currentRecipe.getInput().getAmount()
                    && ((ItemStack.isSameItemSameTags(getItemHandler().getStackInSlot(0), currentRecipe.getOutput())) || getItemHandler().getStackInSlot(0).isEmpty())
                    && (getItemHandler().getStackInSlot(0).getCount() + currentRecipe.getOutput().getCount()) <= currentRecipe.getOutput().getMaxStackSize();
        } else {
            return false;
        }
    }

    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getItemHandler().setOrIncrement(0, currentRecipe.getOutput().copy());
            getFluidStorage().drain(currentRecipe.getInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        getEnergyHandler().extractEnergy(Config.Common.atomizerEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        currentRecipe = (AtomizerRecipe) pRecipe;
    }

    @Override
    public Recipe<Inventory> getRecipe() {
        return currentRecipe;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.atomizerEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                super.onEnergyChanged();
                setChanged();
            }
        };
    }

    @Override
    public CustomFluidStorage initializeFluidStorage() {
        return new CustomFluidStorage(Config.Common.atomizerFluidCapacity.get(), FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeItemHandler() {
        return new CustomItemStackHandler(1) {
            @Override
            public boolean isItemValid(int pSlot, ItemStack pItemStack) {
                return false;
            }
        };
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("maxProgress", maxProgress);
        super.saveAdditional(pTag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new AtomizerMenu(pContainerId, pInventory, this);
    }
}
