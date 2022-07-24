package com.smashingmods.alchemistry.common.block.liquifier;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.*;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class LiquifierBlockEntity extends AbstractFluidBlockEntity {

    protected final ContainerData data;
    private final int maxProgress = Config.Common.liquifierTicksPerOperation.get();
    private LiquifierRecipe currentRecipe;

    public LiquifierBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> getProgress();
                    case 1 -> maxProgress;
                    case 2 -> getEnergyHandler().getEnergyStored();
                    case 3 -> getEnergyHandler().getMaxEnergyStored();
                    case 4 -> getFluidStorage().getFluidAmount();
                    case 5 -> getFluidStorage().getCapacity();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> setProgress(pValue);
                    case 2 -> getEnergyHandler().setEnergy(pValue);
                    case 4 -> getFluidStorage().setAmount(pValue);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (currentRecipe == null || ItemStack.isSameItemSameTags(currentRecipe.getInput(), getItemHandler().getStackInSlot(0))) {
                currentRecipe = RecipeRegistry.getRecipesByType(RecipeRegistry.LIQUIFIER_TYPE, level).stream()
                        .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), getItemHandler().getStackInSlot(0)))
                        .findFirst()
                        .orElse(null);
            }
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            return getEnergyHandler().getEnergyStored() >= Config.Common.liquifierEnergyPerTick.get()
                    && (getFluidStorage().getFluidStack().isFluidEqual(currentRecipe.getOutput()) || getFluidStorage().isEmpty())
                    && getFluidStorage().getFluidAmount() <= (getFluidStorage().getFluidAmount() + currentRecipe.getOutput().getAmount())
                    && (ItemStack.isSameItemSameTags(currentRecipe.getInput().copy(), getItemHandler().getStackInSlot(0)))
                    && getItemHandler().getStackInSlot(0).getCount() >= currentRecipe.getInput().getCount();
        } else {
            return false;
        }
    }

    @Override
    public void processRecipe() {
        if (getProgress() < maxProgress) {
            incrementProgress();
        } else {
            setProgress(0);
            getItemHandler().decrementSlot(0, currentRecipe.getInput().getCount());
            getFluidStorage().fill(currentRecipe.getOutput().copy(), IFluidHandler.FluidAction.EXECUTE);
        }
        getEnergyHandler().extractEnergy(Config.Common.liquifierEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(T pRecipe) {
        currentRecipe = (LiquifierRecipe) pRecipe;
    }

    @Override
    public Recipe<Inventory> getRecipe() {
        return currentRecipe;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.liquifierEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                super.onEnergyChanged();
                setChanged();
            }
        };
    }

    @Override
    public CustomFluidStorage initializeFluidStorage() {
        return new CustomFluidStorage(Config.Common.liquifierFluidCapacity.get(), FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeItemHandler() {
        return new CustomItemStackHandler(1);
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new LiquifierMenu(pContainerId, pInventory, this, data);
    }
}
