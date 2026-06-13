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
        if (this.controller == pController) {
            return;
        }
        this.controller = pController;
    }
}
