package com.smashingmods.alchemistry.common.block.fission;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.ModItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FissionControllerBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity {

    protected final ContainerData data;

    private int progress = 0;
    private final int maxProgress = Config.Common.fissionTicksPerOperation.get();

    private final ModItemStackHandler inputHandler = initializeInputHandler();
    private final ModItemStackHandler outputHandler = initializeOutputHandler();

    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(getInputHandler());
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(getOutputHandler());

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    private FissionRecipe currentRecipe;

    public FissionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FISSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

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

    private void updateRecipe(Level pLevel) {
        if (!inputHandler.getStackInSlot(0).isEmpty() && (currentRecipe == null || !ItemStack.matches(currentRecipe.getOutput().get(0), outputHandler.getStackInSlot(0)) && !ItemStack.matches(currentRecipe.getOutput().get(1), outputHandler.getStackInSlot(1)))) {
            List<FissionRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.FISSION_TYPE, pLevel).stream().toList();
            currentRecipe = recipes.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), inputHandler.getStackInSlot(0))).findFirst().orElse(null);
        } else {
            currentRecipe = null;
        }
    }

    private boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = inputHandler.getStackInSlot(0);
            ItemStack output1 = outputHandler.getStackInSlot(0);
            ItemStack output2 = outputHandler.getStackInSlot(1);
            return energyHandler.getEnergyStored() >= Config.Common.fissionEnergyPerTick.get()
                    && (currentRecipe.getOutput().get(0).getCount() + output1.getCount()) <= currentRecipe.getOutput().get(0).getMaxStackSize()
                    && (currentRecipe.getOutput().get(1).getCount() + output2.getCount()) <= currentRecipe.getOutput().get(1).getMaxStackSize()
                    && (ItemStack.isSameItemSameTags(input, currentRecipe.getInput()) && input.getCount() >= currentRecipe.getInput().getCount());
        }
        return false;
    }

    private void processRecipe() {
        if (progress < maxProgress) {
            progress++;
        } else {
            progress = 0;
            inputHandler.decrementSlot(0, currentRecipe.getInput().getCount());
            outputHandler.setOrIncrement(0, currentRecipe.getOutput().get(0));
            outputHandler.setOrIncrement(1, currentRecipe.getOutput().get(1));
        }
        energyHandler.extractEnergy(Config.Common.fissionEnergyPerTick.get(), false);
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
        return new ModItemStackHandler(this, 1);
    }

    @Override
    public ModItemStackHandler initializeOutputHandler() {
        return new ModItemStackHandler(this, 2) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
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
            @Override
            @Nonnull
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
            @Override
            @Nonnull
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

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction pDirection) {
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

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FissionControllerMenu(pContainerId, pInventory, this, this.data);
    }
}
