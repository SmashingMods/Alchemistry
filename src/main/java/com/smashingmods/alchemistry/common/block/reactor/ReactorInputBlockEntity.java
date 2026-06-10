package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ReactorInputBlockEntity extends BlockEntity {

    @Nullable
    private AbstractReactorBlockEntity controller;

    public ReactorInputBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.REACTOR_INPUT_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
    }

    @Nullable
    public AbstractReactorBlockEntity getController() {
        return controller;
    }

    public void setController(@Nullable AbstractReactorBlockEntity pController) {
        this.controller = pController;
    }

    /**
     * 1.21.5 removed {@code BlockBehaviour#onRemove} and split its work into {@link BlockEntity#preRemoveSideEffects}
     * (run by {@code LevelChunk} while the block entity is still in the level, only when the block actually changes)
     * and {@code BlockBehaviour#affectNeighborsAfterRemoval}. Detaching this face from its controller is block-entity
     * teardown, so it moves here off {@link ReactorInputBlock}: when the input face is broken, clear the controller's
     * input flag so the multiblock re-validates as incomplete.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pPos, BlockState pState) {
        if (level != null && !level.isClientSide() && controller != null) {
            controller.setInputFound(false);
        }
        super.preRemoveSideEffects(pPos, pState);
    }
}
