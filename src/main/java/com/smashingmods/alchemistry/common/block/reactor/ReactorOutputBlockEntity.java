package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemistry.api.blockentity.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorOutputBlockEntity extends BlockEntity {

    private AbstractReactorBlockEntity controller;
    private LazyOptional<IItemHandler> lazyOutputHandler;

    public ReactorOutputBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.REACTOR_OUTPUT_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public AbstractReactorBlockEntity getController() {
        return controller;
    }

    public void setController(@Nullable AbstractReactorBlockEntity pController) {
        this.controller = pController;
        this.lazyOutputHandler = LazyOptional.of(() -> controller.getOutputHandler());
    }

    public void onControllerRemoved() {
        controller = null;
        lazyOutputHandler = LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (controller != null) {
                return lazyOutputHandler.cast();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(pCapability, pDirection);
    }
}
