package com.smashingmods.alchemistry.common.block.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemistry.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemistry.api.storage.FluidStorageHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

    private LiquifierRecipe currentRecipe;

    public LiquifierBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.LIQUIFIER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.liquifierEnergyPerTick.get());
        setMaxProgress(Config.Common.liquifierTicksPerOperation.get());
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getSlotHandler().isEmpty() && !isRecipeLocked()) {
            RecipeRegistry.getLiquifierRecipe(recipe -> recipe.getInput().matches(getSlotHandler().getStackInSlot(0)), level)
                .ifPresent(recipe -> {
                    if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                        setProgress(0);
                        setRecipe(recipe.copy());
                    }
                });
        }
    }

    @Override
    public boolean canProcessRecipe() {
        ItemStack input = getSlotHandler().getStackInSlot(0);
        if (currentRecipe != null) {
            LiquifierRecipe tempRecipe = currentRecipe.copy();
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && (getFluidStorage().getFluidStack().isFluidEqual(tempRecipe.getOutput()) || getFluidStorage().isEmpty())
                    && getFluidStorage().getFluidAmount() <= (getFluidStorage().getFluidAmount() + tempRecipe.getOutput().getAmount())
                    && (tempRecipe.getInput().matches(input) && input.getCount() >= tempRecipe.getInput().getCount());
        } else {
            return false;
        }
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            LiquifierRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getSlotHandler().decrementSlot(0, tempRecipe.getInput().getCount());
            getFluidStorage().fill(tempRecipe.getOutput(), IFluidHandler.FluidAction.EXECUTE);
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
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
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeSlotHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!isEmpty() && !isRecipeLocked()) {
                    updateRecipe();
                }
                setCanProcess(canProcessRecipe());
                setChanged();
            }
        };
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (level != null) {
            RecipeRegistry.getLiquifierRecipe(recipe -> recipe.getId().equals(ResourceLocation.tryParse(pTag.getString("recipeId"))), level)
                    .ifPresent(recipe -> setRecipe(recipe.copy()));
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new LiquifierMenu(pContainerId, pInventory, this);
    }
}
