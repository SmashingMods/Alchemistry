package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.ModItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class CompactorBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity {

    protected final ContainerData data;

    private int progress = 0;
    private final int maxProgress = Config.Common.compactorTicksPerOperation.get();

    private final ModItemStackHandler inputHandler = initializeInputHandler();
    private final ModItemStackHandler outputHandler = initializeOutputHandler();

    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(getInputHandler());
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(getOutputHandler());

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    private CompactorRecipe currentRecipe;

    public CompactorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.COMPACTOR_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch(pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored();
                    case 3 -> energyHandler.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 2 -> energyHandler.setEnergy(pValue);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public void tick(Level pLevel) {
        if (!pLevel.isClientSide()) {
            updateRecipe(pLevel);
            if (canProcessRecipe()) {
                processRecipe();
            } else {
                progress = 0;
            }
        }
    }

    public void updateRecipe(Level pLevel) {
        if (!inputHandler.getStackInSlot(0).isEmpty() && (currentRecipe == null || !ItemStack.matches(currentRecipe.getOutput(), getOutputHandler().getStackInSlot(0)))) {

            List<CompactorRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.COMPACTOR_TYPE, pLevel).stream().toList();
            List<CompactorRecipe> filtered = recipes.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), inputHandler.getStackInSlot(0))).collect(Collectors.toList());

            if (filtered.size() > 1) {
                currentRecipe = filtered.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), inputHandler.getStackInSlot(1))).findFirst().orElse(null);
            } else {
                currentRecipe = !filtered.isEmpty() ? filtered.get(0) : null;
            }
        }
    }

    private boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = inputHandler.getStackInSlot(0);
            ItemStack output = outputHandler.getStackInSlot(0);
            return energyHandler.getEnergyStored() >= Config.Common.compactorEnergyPerTick.get()
                    && (ItemStack.isSameItemSameTags(input, currentRecipe.getInput()) && input.getCount() >= currentRecipe.getInput().getCount())
                    && (currentRecipe.getOutput().getCount() + output.getCount()) <= currentRecipe.getOutput().getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(output, currentRecipe.getOutput()) || output.isEmpty());
        }
        return false;
    }

    private void processRecipe() {
        if (progress < maxProgress) {
            progress++;
        } else {
            progress = 0;
            outputHandler.setOrIncrement(0, currentRecipe.getOutput().copy());
            inputHandler.decrementSlot(0, currentRecipe.getInput().getCount());
        }
        energyHandler.extractEnergy(Config.Common.compactorEnergyPerTick.get(), false);
        setChanged();
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
    public ModItemStackHandler initializeInputHandler() {
        return new ModItemStackHandler(this, 2);
    }

    @Override
    public ModItemStackHandler initializeOutputHandler() {
        return new ModItemStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    public ModItemStackHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public ModItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
            @NotNull
            @Override
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
            @NotNull
            @Override
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                if (!getStackInSlot(pSlot).isEmpty()) {
                    return super.extractItem(pSlot, pAmount, pSimulate);
                } else {
                    return ItemStack.EMPTY;
                }
            }
        };
    }

    @Override
    public CombinedInvWrapper getAutomationInventory() {
        return combinedInvWrapper;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction pDirection) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, pDirection);
    }

    @Override
    public void invalidateCaps() {
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("progress", progress);
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.put("energy", energyHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        energyHandler.deserializeNBT(pTag.get("energy"));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CompactorMenu(pContainerId, pInventory, this, this.data);
    }
}
