package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public class AbstractReactorBlock extends AbstractProcessingBlock {

    public AbstractReactorBlock(BiFunction<BlockPos, BlockState, BlockEntity> pBlockEntity) {
        super(pBlockEntity);

        if (pBlockEntity instanceof AbstractReactorBlockEntity reactorBlockEntity) {
            reactorBlockEntity.resetIO();
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            if (pLevel.getBlockEntity(pPos) instanceof AbstractReactorBlockEntity reactorBlockEntity) {
                reactorBlockEntity.onRemove();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
