package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public abstract class AbstractReactorBlock extends AbstractProcessingBlock {

    public AbstractReactorBlock(BiFunction<BlockPos, BlockState, BlockEntity> pBlockEntity) {
        super(pBlockEntity);

        if (pBlockEntity instanceof AbstractReactorBlockEntity reactorBlockEntity) {
            reactorBlockEntity.resetIO();
        }
    }
}
