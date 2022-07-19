package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.*;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.BlockEntityPacket;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CompactorBlockEntity extends AbstractInventoryBlockEntity {

    protected final ContainerData data;
    private final int maxProgress = Config.Common.compactorTicksPerOperation.get();
    private CompactorRecipe currentRecipe;
    private ItemStack target = ItemStack.EMPTY;

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
            if (!getInputHandler().getStackInSlot(0).isEmpty()) {
                if (target.isEmpty()) {
                    List<CompactorRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream()
                            .filter(recipe -> ItemStack.isSameItemSameTags(getInputHandler().getStackInSlot(0), recipe.getInput()))
                            .collect(Collectors.toList());
                    if (recipes.size() == 1) {
                        currentRecipe = recipes.get(0);
                    } else {
                        currentRecipe = null;
                    }
                } else {
                    RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream().filter(recipe -> ItemStack.isSameItemSameTags(target, recipe.getOutput())).findFirst().ifPresent(recipe -> currentRecipe = recipe);
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
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        if (pRecipe == null) {
            currentRecipe = null;
        } else {
            currentRecipe = (CompactorRecipe) pRecipe;
            target = ((CompactorRecipe) pRecipe).getOutput();
            if (level != null && !level.isClientSide()) {
                AlchemistryPacketHandler.sendToNear(new BlockEntityPacket(getBlockPos(), getUpdateTag()), level, getBlockPos(), 64);
            }
        }
    }

    @Override
    public Recipe<Inventory> getRecipe() {
        return currentRecipe;
    }

    public ItemStack getTarget() {
        return target;
    }

    public void setTarget(ItemStack pTarget) {
        this.target = pTarget;
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

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack pItemStack) {
                if (slot == 1) {
                    target = new ItemStack(pItemStack.getItem(), 1);
                }
                return slot != 1;
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

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("target", target.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        target = ItemStack.of(pTag.getCompound("target"));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CompactorMenu(pContainerId, pInventory, this, this.data);
    }
}
