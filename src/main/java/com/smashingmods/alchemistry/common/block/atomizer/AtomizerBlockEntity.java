package com.smashingmods.alchemistry.common.block.atomizer;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.FluidBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.common.recipe.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.AtomizerRegistry;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class AtomizerBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity, FluidBlockEntity {

    protected final ContainerData data;

    private int progress = 0;
    private int maxProgress = Config.ATOMIZER_TICKS_PER_OPERATION.get();

    private final CustomStackHandler inputHandler = initializeInputHandler();
    private final CustomStackHandler outputHandler = initializeOutputHandler();
    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(getInputHandler());
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(getOutputHandler());
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    private final CustomFluidStorage fluidHandler = initializeFluidStorage();
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidHandler);

    AtomizerRecipe currentRecipe;

    public AtomizerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.ATOMIZER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored();
                    case 3 -> energyHandler.getMaxEnergyStored();
                    case 4 -> fluidHandler.getFluidAmount();
                    case 5 -> fluidHandler.getCapacity();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 1 -> maxProgress = pValue;
                    case 2 -> energyHandler.setEnergy(energyHandler.getEnergyStored());
                    case 3 -> fluidHandler.setAmount(pValue);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    public void updateRecipe() {
        if (!fluidHandler.isEmpty() &&
                (currentRecipe == null || !ItemStack.matches(currentRecipe.output, getOutputHandler().getStackInSlot(0)))
        ) {
            currentRecipe = AtomizerRegistry.getRecipes(level).stream()
                    .filter(recipe -> recipe.input.getFluid() == fluidHandler.getFluidStack().getFluid()).findFirst().orElse(null);
        }
    }

    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack recipeOutput = currentRecipe.output;
            ItemStack outputSlot0 = getOutputHandler().getStackInSlot(0);
            return energyHandler.getEnergyStored() >= Config.ATOMIZER_ENERGY_PER_TICK.get() &&
                    fluidHandler.getFluidAmount() >= currentRecipe.input.getAmount() &&
                    (ItemStack.matches(outputSlot0, recipeOutput) || outputSlot0.isEmpty()) &&
                    outputSlot0.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
        } else {
            return false;
        }
    }

    public void processRecipe() {
        if (progress < Config.ATOMIZER_TICKS_PER_OPERATION.get()) {
            progress++;
        } else {
            progress = 0;
            getOutputHandler().setOrIncrement(0, currentRecipe.output.copy());
            fluidHandler.drain(currentRecipe.input.getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        this.energyHandler.extractEnergy(Config.ATOMIZER_ENERGY_PER_TICK.get(), false);
        this.setChanged();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @Nonnull Inventory pInventory, @Nonnull Player pPlayer) {
        return new AtomizerMenu(pContainerId, pInventory, this, this.data);
    }

    public void tick(Level pLevel) {
        if (!pLevel.isClientSide() && fluidHandler.getFluidAmount() > 0) {
            updateRecipe();
            if (canProcessRecipe()) {
                processRecipe();
            }
        }
    }

    @Override
    public CustomStackHandler initializeInputHandler() {
        return new CustomStackHandler(this);
    }

    @Override
    public CustomStackHandler initializeOutputHandler() {
        return new CustomStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int pSlot, @Nonnull ItemStack pItemStack) {
                return false;
            }
        };
    }

    public CustomStackHandler getInputHandler() {
        return inputHandler;
    }

    public CustomStackHandler getOutputHandler() {
        return outputHandler;
    }

    public AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pInput) {
        return new AutomationStackHandler(pInput) {
            @Override
            @Nonnull
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pOutput) {
        return new AutomationStackHandler(pOutput) {
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
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.ATOMIZER_ENERGY_CAPACITY.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomFluidStorage initializeFluidStorage() {
        return new CustomFluidStorage(10000, FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                updateRecipe();
                setChanged();
            }
        };
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction pDirection) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyHandler.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, pDirection);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("progress", progress);
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.put("energy", energyHandler.serializeNBT());
        pTag.put("fluid", fluidHandler.writeToNBT(new CompoundTag()));
        super.saveAdditional(pTag);
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        energyHandler.deserializeNBT(pTag.get("energy"));
        fluidHandler.readFromNBT(pTag.getCompound("fluid"));
    }
}
