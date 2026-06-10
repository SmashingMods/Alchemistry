package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public class AbstractReactorBlock extends AbstractProcessingBlock {

    public AbstractReactorBlock(BiFunction<BlockPos, BlockState, BlockEntity> pBlockEntity) {
        this(pBlockEntity, AbstractProcessingBlock.machineProperties());
    }

    public AbstractReactorBlock(BiFunction<BlockPos, BlockState, BlockEntity> pBlockEntity, BlockBehaviour.Properties pProperties) {
        super(pBlockEntity, pProperties);

        if (pBlockEntity instanceof AbstractReactorBlockEntity reactorBlockEntity) {
            reactorBlockEntity.resetIO();
        }
    }

    // 1.21.5 removed BlockBehaviour#onRemove; the controller's removal teardown (resetIO + cores off) now runs from
    // AbstractReactorBlockEntity#preRemoveSideEffects, which LevelChunk invokes on genuine removal only.
}
