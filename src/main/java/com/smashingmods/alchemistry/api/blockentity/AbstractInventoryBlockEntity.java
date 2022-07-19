package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class AbstractInventoryBlockEntity extends AbstractProcessingBlockEntity implements InventoryBlockEntity {

    private final CustomItemStackHandler inputHandler = initializeInputHandler();
    private final CustomItemStackHandler outputHandler = initializeOutputHandler();
    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(inputHandler);
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(outputHandler);
    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    public AbstractInventoryBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }

    @Override
    public void invalidateCaps() {
        lazyItemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
    }

    @Override
    public void dropContents() {
        if (level != null && !level.isClientSide()) {
            SimpleContainer container = new SimpleContainer(getAutomationInventory().getSlots());
            for (int i = 0; i < getAutomationInventory().getSlots(); i++) {
                container.setItem(i, getAutomationInventory().getStackInSlot(i));
            }
            Containers.dropContents(level, worldPosition, container);
        }
    }
}
