package com.smashingmods.alchemistry.common.block.dissolver;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.ProcessingBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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

public class DissolverBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity, ProcessingBlockEntity {

    protected final ContainerData data;

    private int progress = 0;
    private final int maxProgress = Config.Common.dissolverTicksPerOperation.get();

    private final CustomItemStackHandler inputHandler = initializeInputHandler();
    private final CustomItemStackHandler outputHandler = initializeOutputHandler();

    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(inputHandler);
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(outputHandler);

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    private DissolverRecipe currentRecipe;
    private final NonNullList<ItemStack> internalBuffer = NonNullList.createWithCapacity(64);

    public DissolverBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.DISSOLVER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

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
            updateRecipe();
            if (canProcessRecipe()) {
                processRecipe();
            } else {
                progress = 0;
            }
            processBuffer();
        }
    }

    @Override
    public void updateRecipe() {
        if (level != null && !level.isClientSide()) {
            if (!inputHandler.getStackInSlot(0).isEmpty() && (currentRecipe == null || !ItemStack.isSameItemSameTags(currentRecipe.getInput().copy(), getInputHandler().getStackInSlot(0).copy()))) {
                currentRecipe = RecipeRegistry.getRecipesByType(RecipeRegistry.DISSOLVER_TYPE, level).stream()
                        .filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput().copy(), inputHandler.getStackInSlot(0).copy()))
                        .findFirst()
                        .orElse(null);
            }
        }
    }

    @Override
    public boolean canProcessRecipe() {
        if (currentRecipe != null) {
            ItemStack input = inputHandler.getStackInSlot(0).copy();
            return energyHandler.getEnergyStored() >= Config.Common.dissolverEnergyPerTick.get()
                    && ItemStack.isSameItemSameTags(input, currentRecipe.getInput())
                    && (input.getCount() >= currentRecipe.getInput().getCount())
                    && internalBuffer.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void processRecipe() {
        if (progress < maxProgress) {
            progress++;
        } else {
            progress = 0;
            inputHandler.decrementSlot(0, currentRecipe.getInput().copy().getCount());
            internalBuffer.addAll(currentRecipe.getOutput().calculateOutput());
        }
        energyHandler.extractEnergy(Config.Common.dissolverEnergyPerTick.get(), false);
        setChanged();
    }

    private void processBuffer() {
        for (int i = 0; i < internalBuffer.size(); i++) {
            ItemStack bufferStack = internalBuffer.get(i).copy();
            for (int j = 0; j < outputHandler.getStacks().size(); j++) {
                ItemStack slotStack = outputHandler.getStackInSlot(j).copy();
                if (slotStack.isEmpty() || (ItemStack.isSameItemSameTags(bufferStack, slotStack) && bufferStack.getCount() + slotStack.getCount() <= slotStack.getMaxStackSize())) {
                    outputHandler.setOrIncrement(j, bufferStack);
                    internalBuffer.remove(i);
                    break;
                }
            }
        }
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.dissolverEnergyCapacity.get()) {
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
        return new CustomItemStackHandler(10) {
            @Override
            public boolean isItemValid(int pSlot, ItemStack pItemStack) {
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
                if (getStackInSlot(pSlot).isEmpty()) {
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
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction pDirection) {
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

        ListTag bufferTag = new ListTag();
        internalBuffer.stream()
                .filter(itemStack -> !itemStack.isEmpty())
                .forEach(itemStack -> bufferTag.add(itemStack.save(new CompoundTag())));
        pTag.put("buffer", bufferTag);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        energyHandler.deserializeNBT(pTag.get("energy"));

        ListTag bufferTag = pTag.getList("buffer", 10);
        bufferTag.stream()
                .filter(tag -> tag instanceof CompoundTag)
                .map(CompoundTag.class::cast)
                .map(ItemStack::of)
                .forEach(internalBuffer::add);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new DissolverMenu(pContainerId, pInventory, this, this.data);
    }
}
