package com.smashingmods.alchemistry.common.block.fission;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.*;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.block.reactor.ReactorEnergyInputBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorItemInputBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorItemOutputBlockEntity;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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

import java.util.List;

public class FissionControllerBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity, ProcessingBlockEntity, MultiblockBlockEntity {

    protected final ContainerData data;
    private ReactorShape reactorShape;

    private int progress = 0;
    private final int maxProgress = Config.Common.fissionTicksPerOperation.get();

    private final CustomItemStackHandler inputHandler = initializeInputHandler();
    private final CustomItemStackHandler outputHandler = initializeOutputHandler();

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

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (reactorShape == null) {
                reactorShape = new ReactorShape(this.getBlockPos(), ReactorType.FISSION, level);
            }

            if (isValidMultiblock()) {
                setMultiblockHandlers();

                if (this.getBlockState().getValue(PowerStateProperty.POWER_STATE) == PowerState.DISABLED) {
                    if (this.energyHandler.getEnergyStored() > 0) {
                        setPowerState(PowerState.STANDBY);
                    } else {
                        setPowerState(PowerState.OFF);
                    }
                }

                updateRecipe();
                if (canProcessRecipe()) {
                    setPowerState(PowerState.ON);
                    processRecipe();
                } else {
                    progress = 0;
                    if (this.energyHandler.getEnergyStored() > 0) {
                        setPowerState(PowerState.STANDBY);
                    } else {
                        setPowerState(PowerState.OFF);
                    }
                }
            } else {
                setPowerState(PowerState.DISABLED);
            }
        }
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (!inputHandler.getStackInSlot(0).isEmpty()) {
                List<FissionRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.FISSION_TYPE, level).stream().toList();
                currentRecipe = recipes.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput(), inputHandler.getStackInSlot(0))).findFirst().orElse(null);
            } else {
                currentRecipe = null;
            }
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = inputHandler.getStackInSlot(0);
            ItemStack output1 = outputHandler.getStackInSlot(0);
            ItemStack output2 = outputHandler.getStackInSlot(1);
            return energyHandler.getEnergyStored() >= Config.Common.fissionEnergyPerTick.get()
                    && (ItemStack.isSameItemSameTags(input, currentRecipe.getInput()) && input.getCount() >= currentRecipe.getInput().getCount())
                    && ((ItemStack.isSameItemSameTags(output1, currentRecipe.getOutput1()) || output1.isEmpty()) && (currentRecipe.getOutput1().getCount() + output1.getCount()) <= currentRecipe.getOutput1().getMaxStackSize())
                    && ((ItemStack.isSameItemSameTags(output2, currentRecipe.getOutput2()) || output2.isEmpty()) && (currentRecipe.getOutput2().getCount() + output2.getCount()) <= currentRecipe.getOutput2().getMaxStackSize());
        }
        return false;
    }

    @Override
    public void processRecipe() {
        if (progress < maxProgress) {
            progress++;
        } else {
            progress = 0;
            inputHandler.decrementSlot(0, currentRecipe.getInput().getCount());
            outputHandler.setOrIncrement(0, currentRecipe.getOutput1().copy());
            outputHandler.setOrIncrement(1, currentRecipe.getOutput2().copy());
        }
        energyHandler.extractEnergy(Config.Common.fissionEnergyPerTick.get(), false);
        setChanged();
    }

    public boolean isValidMultiblock() {
        if (level != null && !level.isClientSide()) {
            return validateMultiblockShape(level, reactorShape.createShapeMap());
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private void setMultiblockHandlers() {
        BlockPos.betweenClosedStream(reactorShape.getFullBoundingBox()).forEach(blockPos -> {
            Block block = level.getBlockState(blockPos).getBlock();
            if (block.equals(BlockRegistry.REACTOR_ENERGY_INPUT.get())) {
                if (level.getBlockEntity(blockPos) instanceof ReactorEnergyInputBlockEntity blockEntity) {
                    blockEntity.setEnergyHandler(this.energyHandler);
                }
            }
            if (block.equals(BlockRegistry.REACTOR_ITEM_INPUT.get())) {
                if (level.getBlockEntity(blockPos) instanceof ReactorItemInputBlockEntity blockEntity) {
                    blockEntity.setInputHandler(this.inputHandler);
                }
            }
            if (block.equals(BlockRegistry.REACTOR_ITEM_OUTPUT.get())) {
                if (level.getBlockEntity(blockPos) instanceof ReactorItemOutputBlockEntity blockEntity) {
                    blockEntity.setOutputHandler(this.outputHandler);
                }
            }
        });
    }

    private void setPowerState(PowerState pPowerState) {
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(PowerStateProperty.POWER_STATE, pPowerState));
        }
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.fissionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public CustomItemStackHandler initializeInputHandler() {
        return new CustomItemStackHandler(1);
    }

    @Override
    public CustomItemStackHandler initializeOutputHandler() {
        return new CustomItemStackHandler(2) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    public CustomItemStackHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public CustomItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
            @Override
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
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
    public <T> LazyOptional<T> getCapability(Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        } else if (pCapability == CapabilityEnergy.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
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
