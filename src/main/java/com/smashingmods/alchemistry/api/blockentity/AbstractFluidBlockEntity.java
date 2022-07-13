package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class AbstractFluidBlockEntity extends AbstractProcessingBlockEntity implements FluidBlockEntity {

    private final CustomFluidStorage fluidStorage = initializeFluidStorage();
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidStorage);
    private final CustomItemStackHandler itemHandler = initializeItemHandler();
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public AbstractFluidBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public CustomFluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public CustomItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction pDirection) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, pDirection);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("item", itemHandler.serializeNBT());
        pTag.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("item"));
        fluidStorage.readFromNBT(pTag.getCompound("fluid"));
    }

    @Override
    public void dropContents() {
        if (level != null && !level.isClientSide()) {
            SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                container.setItem(i, itemHandler.getStackInSlot(i));
            }
            Containers.dropContents(level, worldPosition, container);
        }
    }
}
