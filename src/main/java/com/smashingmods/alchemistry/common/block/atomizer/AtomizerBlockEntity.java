package com.smashingmods.alchemistry.common.block.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class AtomizerBlockEntity extends AbstractFluidBlockEntity {

    private AtomizerRecipe currentRecipe;
    private ResourceLocation recipeId;

    public AtomizerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setEnergyPerTick(Config.Common.atomizerEnergyPerTick.get());
        setMaxProgress(Config.Common.atomizerTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getAtomizerRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getFluidStorage().isEmpty()) {
            RecipeRegistry.getAtomizerRecipe(recipe -> recipe.getInput().getFluid().equals(getFluidStorage().getFluidStack().getFluid()), level)
                .ifPresent(recipe -> {
                    if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                        setProgress(0);
                        setRecipe(recipe.copy());
                    }
                });
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            return getEnergyHandler().getEnergyStored() >= getEnergyPerTick()
                    && getFluidStorage().getFluidAmount() >= currentRecipe.getInput().getAmount()
                    && ((ItemStack.isSameItemSameTags(getSlotHandler().getStackInSlot(0), currentRecipe.getOutput())) || getSlotHandler().getStackInSlot(0).isEmpty())
                    && (getSlotHandler().getStackInSlot(0).getCount() + currentRecipe.getOutput().getCount()) <= currentRecipe.getOutput().getMaxStackSize();
        }
        return false;
    }

    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            AtomizerRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getSlotHandler().setOrIncrement(0, tempRecipe.getOutput().copy());
            getFluidStorage().drain(tempRecipe.getInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof AtomizerRecipe atomizerRecipe) {
            currentRecipe = atomizerRecipe;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public AtomizerRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<AtomizerRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getAtomizerRecipes(level));
        }
        return new LinkedList<>();
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.atomizerEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                super.onEnergyChanged();
                setChanged();
            }
        };
    }

    @Override
    public FluidStorageHandler initializeFluidStorage() {
        return new FluidStorageHandler(Config.Common.atomizerFluidCapacity.get(), FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                if (!isEmpty()) {
                    updateRecipe();
                }
                setCanProcess(canProcessRecipe());
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeSlotHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            public boolean isItemValid(int pSlot, ItemStack pItemStack) {
                return false;
            }
        };
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
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        if (level != null && level.isClientSide()) {
            RecipeRegistry.getAtomizerRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(recipe -> {
                if (!recipe.equals(currentRecipe)) {
                    setRecipe(recipe);
                    Alchemistry.PACKET_HANDLER.sendToServer(new SetRecipePacket(getBlockPos(), recipe.getId(), recipe.getGroup()));
                }
            });
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new AtomizerMenu(pContainerId, pInventory, this);
    }
}
