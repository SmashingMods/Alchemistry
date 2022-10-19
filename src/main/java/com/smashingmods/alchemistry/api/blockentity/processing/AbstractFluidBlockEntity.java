package com.smashingmods.alchemistry.api.blockentity.processing;

import com.smashingmods.alchemistry.api.storage.FluidStorageHandler;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class AbstractFluidBlockEntity extends AbstractProcessingBlockEntity implements FluidBlockEntity {

    private final FluidStorageHandler fluidStorage = initializeFluidStorage();
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidStorage);
    private final ProcessingSlotHandler itemHandler = initializeSlotHandler();
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    public AbstractFluidBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (!getFluidStorage().isEmpty()) {
            setCanProcess(canProcessRecipe());
        }
        super.tick();
    }

    @Override
    public FluidStorageHandler getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public ProcessingSlotHandler getSlotHandler() {
        return itemHandler;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction pDirection) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        } else if (cap == ForgeCapabilities.FLUID_HANDLER) {
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
    public boolean isRecipeLocked() {
        return false;
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

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }
}
