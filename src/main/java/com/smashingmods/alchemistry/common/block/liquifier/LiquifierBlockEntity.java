package com.smashingmods.alchemistry.common.block.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.common.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.common.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.common.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.common.storage.ProcessingSlotHandler;
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

public class LiquifierBlockEntity extends AbstractFluidBlockEntity {

    private final int maxProgress = Config.Common.liquifierTicksPerOperation.get();
    private LiquifierRecipe currentRecipe;

    public LiquifierBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getRecipesByType(RecipeRegistry.LIQUIFIER_TYPE, level).stream()
                    .filter(recipe -> recipe.getInput().matches(getSlotHandler().getStackInSlot(0)))
                    .findFirst()
                    .ifPresent(recipe -> {
                        if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                            setProgress(0);
                            currentRecipe = recipe;
                        }
                    });
        }
    }

    @Override
    public boolean canProcessRecipe() {
        ItemStack input = getSlotHandler().getStackInSlot(0);
        if (currentRecipe != null) {
            return getEnergyHandler().getEnergyStored() >= Config.Common.liquifierEnergyPerTick.get()
                    && (getFluidStorage().getFluidStack().isFluidEqual(currentRecipe.getOutput()) || getFluidStorage().isEmpty())
                    && getFluidStorage().getFluidAmount() <= (getFluidStorage().getFluidAmount() + currentRecipe.getOutput().getAmount())
                    && (currentRecipe.getInput().matches(input) && input.getCount() >= currentRecipe.getInput().getCount());
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
            getSlotHandler().decrementSlot(0, currentRecipe.getInput().getCount());
            getFluidStorage().fill(currentRecipe.getOutput().copy(), IFluidHandler.FluidAction.EXECUTE);
        }
        getEnergyHandler().extractEnergy(Config.Common.liquifierEnergyPerTick.get(), false);
        setChanged();
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        currentRecipe = (LiquifierRecipe) pRecipe;
    }

    @Override
    public Recipe<Inventory> getRecipe() {
        return currentRecipe;
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.liquifierEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                super.onEnergyChanged();
                setChanged();
            }
        };
    }

    @Override
    public FluidStorageHandler initializeFluidStorage() {
        return new FluidStorageHandler(Config.Common.liquifierFluidCapacity.get(), FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeSlotHandler() {
        return new ProcessingSlotHandler(1);
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
        return new LiquifierMenu(pContainerId, pInventory, this);
    }
}
