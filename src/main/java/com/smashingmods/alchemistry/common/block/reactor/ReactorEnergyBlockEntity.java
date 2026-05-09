package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Plain-{@link BlockEntity} satellite that exposes the parent reactor's energy storage to neighboring
 * blocks via the {@link net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage#BLOCK} capability.
 *
 * <p>Capability registration lives in {@link BlockEntityRegistry#registerCapabilities}; this class only
 * tracks its controller and invalidates the level capability cache when the controller changes.
 */
public class ReactorEnergyBlockEntity extends BlockEntity {

    @Nullable
    private AbstractReactorBlockEntity controller;

    public ReactorEnergyBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.REACTOR_ENERGY_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Nullable
    public AbstractReactorBlockEntity getController() {
        return controller;
    }

    public void setController(@Nullable AbstractReactorBlockEntity pController) {
        if (this.controller == pController) {
            return;
        }
        this.controller = pController;
        if (level != null && !level.isClientSide()) {
            level.invalidateCapabilities(getBlockPos());
        }
    }
}
