package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemistry.api.blockentity.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorEnergyBlockEntity extends BlockEntity {

    @Nullable
    private AbstractReactorBlockEntity controller;
    private LazyOptional<IEnergyStorage> lazyEnergyHandler;

    public ReactorEnergyBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.REACTOR_ENERGY_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Nullable
    public AbstractReactorBlockEntity getController() {
        return controller;
    }

    public void setController(@Nullable AbstractReactorBlockEntity pController) {
        this.controller = pController;
        //noinspection ConstantConditions
        this.lazyEnergyHandler = LazyOptional.of(() -> controller.getEnergyHandler());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityEnergy.ENERGY) {
            if (controller != null) {
                return lazyEnergyHandler.cast();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(pCapability, pDirection);
    }
}
