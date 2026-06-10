package com.smashingmods.alchemistry.common.block.fission;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorType;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.LinkedList;

public class FissionControllerBlockEntity extends AbstractReactorBlockEntity {

    private FissionRecipe currentRecipe;
    private ResourceLocation recipeId;

    public FissionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FISSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        setReactorType(ReactorType.FISSION);
        setEnergyPerTick(Config.Common.fissionEnergyPerTick.get());
        setMaxProgress(Config.Common.fissionTicksPerOperation.get());
    }

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide()) {
            RecipeRegistry.getFissionRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
        super.onLoad();
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !getInputHandler().isEmpty() && !isRecipeLocked()) {
            RecipeRegistry.getFissionRecipe(recipe -> ItemStack.isSameItemSameComponents(recipe.getInput(), getInputHandler().getStackInSlot(0)), level)
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
        if (currentRecipe != null) {
            FissionRecipe tempRecipe = currentRecipe.copy();
            ItemStack input = getInputHandler().getStackInSlot(0);
            ItemStack output1 = getOutputHandler().getStackInSlot(0);
            ItemStack output2 = getOutputHandler().getStackInSlot(1);
            return getEnergyHandler().getEnergyStored() >= Config.Common.fissionEnergyPerTick.get()
                    && (ItemStack.isSameItemSameComponents(input, tempRecipe.getInput()) && input.getCount() >= tempRecipe.getInput().getCount())
                    && ((ItemStack.isSameItemSameComponents(output1, tempRecipe.getOutput1()) || output1.isEmpty()) && (tempRecipe.getOutput1().getCount() + output1.getCount()) <= tempRecipe.getOutput1().getMaxStackSize())
                    && ((ItemStack.isSameItemSameComponents(output2, tempRecipe.getOutput2()) || output2.isEmpty()) && (tempRecipe.getOutput2().getCount() + output2.getCount()) <= tempRecipe.getOutput2().getMaxStackSize());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (getProgress() < getMaxProgress()) {
            incrementProgress();
        } else {
            FissionRecipe tempRecipe = currentRecipe.copy();
            setProgress(0);
            getInputHandler().decrementSlot(0, tempRecipe.getInput().getCount());
            getOutputHandler().setOrIncrement(0, tempRecipe.getOutput1());
            getOutputHandler().setOrIncrement(1, tempRecipe.getOutput2());
        }
        getEnergyHandler().extractEnergy(getEnergyPerTick(), false);
        setChanged();
    }

    @Override
    public <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe) {
        if (pRecipe instanceof FissionRecipe fissionRecipe) {
            currentRecipe = fissionRecipe;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public FissionRecipe getRecipe() {
        return currentRecipe;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<FissionRecipe> getAllRecipes() {
        if (level != null) {
            return new LinkedList<>(RecipeRegistry.getFissionRecipes(level));
        }
        return new LinkedList<>();
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.fissionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeInputHandler() {
        return new ProcessingSlotHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setCanProcess(canProcessRecipe());
                setChanged();
            }

            @Override
            public boolean isItemValid(int pSlot, @Nonnull ItemStack pItemStack) {
                if (currentRecipe != null && isRecipeLocked()) {
                    return ItemStack.isSameItemSameComponents(currentRecipe.getInput(), pItemStack);
                }
                return pItemStack.getItem() instanceof ElementItem;
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeOutputHandler() {
        return new ProcessingSlotHandler(2) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        if (currentRecipe != null) {
            pTag.putString("recipeId", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.recipeId = ResourceLocation.tryParse(pTag.getString("recipeId"));
        if (level != null && level.isClientSide()) {
            // Display-only mirror of the server's recipe; the SetRecipePacket echo this used to send was
            // redundant (onLoad restores the server's recipe from its own save) and would now fabricate a
            // player "selection" on a machine that has no recipe selector.
            RecipeRegistry.getFissionRecipe(recipe -> recipe.getId().equals(recipeId), level).ifPresent(this::setRecipe);
        }
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FissionControllerMenu(pContainerId, pInventory, this);
    }
}
