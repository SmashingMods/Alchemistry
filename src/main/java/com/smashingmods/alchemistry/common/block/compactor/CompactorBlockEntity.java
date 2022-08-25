package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.common.network.BlockEntityPacket;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.common.blockentity.processing.AbstractInventoryBlockEntity;
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

import javax.annotation.Nonnull;
import java.util.List;

public class CompactorBlockEntity extends AbstractInventoryBlockEntity {

    private final int maxProgress = Config.Common.compactorTicksPerOperation.get();
    private CompactorRecipe currentRecipe;
    private ItemStack target = ItemStack.EMPTY;

    public CompactorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide() && !isRecipeLocked()) {
            if (!getInputHandler().getStackInSlot(0).isEmpty()) {
                if (target.isEmpty()) {
                    List<CompactorRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream()
                            .filter(recipe -> recipe.getInput().matches(getInputHandler().getStackInSlot(0)))
                            .toList();
                    if (recipes.size() == 1) {
                        if (currentRecipe == null || !currentRecipe.equals(recipes.get(0))) {
                            setProgress(0);
                            setRecipe(recipes.get(0));
                            setTarget(new ItemStack(recipes.get(0).getOutput().getItem()));
                        }
                    } else {
                        setProgress(0);
                        setRecipe(null);
                    }
                } else {
                    RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream()
                            .filter(recipe -> ItemStack.isSameItemSameTags(target, recipe.getOutput()))
                            .findFirst()
                            .ifPresent(recipe -> {
                                if (currentRecipe == null || !currentRecipe.equals(recipe)) {
                                    setProgress(0);
                                    setRecipe(recipe);
                                    setTarget(new ItemStack(recipe.getOutput().getItem()));
                                }
                            });
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
                    && (currentRecipe.getInput().matches(input) && input.getCount() >= currentRecipe.getInput().getCount())
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
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public <T extends Recipe<Inventory>> void setRecipe(@Nullable T pRecipe) {
        if (pRecipe == null) {
            currentRecipe = null;
        } else {
            currentRecipe = (CompactorRecipe) pRecipe;
            target = ((CompactorRecipe) pRecipe).getOutput();
            if (level != null && !level.isClientSide()) {
                PacketHandler.sendToNear(new BlockEntityPacket(getBlockPos(), getUpdateTag()), level, getBlockPos(), 64);
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
        if (level != null && !level.isClientSide() && !isRecipeLocked()) {
            this.target = pTarget;
        }
    }

    @Override
    public EnergyStorageHandler initializeEnergyStorage() {
        return new EnergyStorageHandler(Config.Common.compactorEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ProcessingSlotHandler initializeInputHandler() {
        return new ProcessingSlotHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                if (level != null && !level.isClientSide()) {
                    if (slot == 1 && !getInputHandler().getStackInSlot(slot).isEmpty()) {
                        RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream().filter(recipe -> ItemStack.isSameItemSameTags(initializeInputHandler().getStackInSlot(slot), recipe.getOutput())).findFirst().ifPresent(recipe -> setRecipe(recipe));
                    }
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack pItemStack) {
                if (slot == 1) {
                    if (level != null && !level.isClientSide()) {
                        RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, level).stream()
                                .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput().copy(), pItemStack.copy()))
                                .findFirst()
                                .ifPresent(recipe -> {
                                    setRecipe(recipe);
                                    setTarget(new ItemStack(pItemStack.getItem()));
                                });
                    }
                }
                return slot != 1;
            }
        };
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
        return new CompactorMenu(pContainerId, pInventory, this);
    }
}
