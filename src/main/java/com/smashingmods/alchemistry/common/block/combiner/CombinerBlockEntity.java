package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CombinerBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity {

    protected final ContainerData data;

    private int progress = 0;
    private int maxProgress = Config.COMBINER_TICKS_PER_OPERATION.get();

    private final CustomStackHandler inputHandler = initializeInputHandler();
    private final CustomStackHandler outputHandler = initializeOutputHandler();
    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(inputHandler);
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(outputHandler);
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    private List<CombinerRecipe> recipes = new ArrayList<>();
    private CombinerRecipe currentRecipe;
    private boolean recipeLocked = false;
    private boolean paused = false;

    public CombinerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.COMBINER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
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
                    case 1 -> maxProgress = pValue;
                    case 2 -> energyHandler.setEnergy(pValue);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new CombinerMenu(pContainerId, pInventory, this, this.data);
    }

    public void tick(Level pLevel) {
        if (!pLevel.isClientSide()) {
            if (canProcessRecipe()) {
                processRecipe();
            } else {
                progress = 0;
            }
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack output = getAutomationInventory().getStackInSlot(5);
            return energyHandler.getEnergyStored() >= Config.COMBINER_ENERGY_PER_TICK.get()
                    && (currentRecipe.output.getCount() + output.getCount()) <= currentRecipe.output.getMaxStackSize()
                    && (ItemStack.isSame(output, currentRecipe.output) || output.isEmpty())
                    && currentRecipe.matchesHandlerStacks(getInputHandler());
        } else {
            return false;
        }
    }

    public void processRecipe() {
        if (progress < maxProgress) {
            progress++;
        } else {
            progress = 0;
            energyHandler.extractEnergy(Config.COMBINER_ENERGY_PER_TICK.get(), false);
            outputHandler.setOrIncrement(0, currentRecipe.output.copy());
            for (int index = 0; index < currentRecipe.inputs.size(); index++) {
                ItemStack itemStack = currentRecipe.inputs.get(index);
                if (itemStack != null && !itemStack.isEmpty()) {
                    getInputHandler().decrementSlot(index, itemStack.getCount());
                }
            }
        }
        setChanged();
    }

    public List<CombinerRecipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<CombinerRecipe> pRecipes) {
        this.recipes = pRecipes;
    }

    public CombinerRecipe getCurrentRecipe() {
        return this.currentRecipe;
    }

    public void setCurrentRecipe(CombinerRecipe pRecipe) {
        this.currentRecipe = pRecipe;
    }

    public void toggleRecipeLocked() {
        Alchemistry.LOGGER.info("TOGGLED LOCK");
        this.recipeLocked = !this.recipeLocked;
    }

    public boolean getRecipeLocked() {
        return this.recipeLocked;
    }

    public void togglePaused() {
        this.paused = !this.paused;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.COMBINER_ENERGY_CAPACITY.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomStackHandler initializeInputHandler() {
        return new CustomStackHandler(this, 5);
    }

    @Override
    public CustomStackHandler initializeOutputHandler() {
        return new CustomStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    public CustomStackHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public CustomStackHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pInput) {
        return new AutomationStackHandler(pInput) {
            @NotNull
            @Override
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pOutput) {
        return new AutomationStackHandler(pOutput) {
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
        super.invalidateCaps();
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
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        energyHandler.deserializeNBT(pTag.get("energy"));
    }
}
