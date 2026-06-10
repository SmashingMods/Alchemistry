package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import com.smashingmods.alchemylib.api.blockentity.power.PowerState;
import com.smashingmods.alchemylib.api.blockentity.power.PowerStateProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import javax.annotation.Nullable;

public class ReactorEnergyBlock extends AbstractProcessingBlock {
    public ReactorEnergyBlock(BlockBehaviour.Properties pProperties) {
        super(ReactorEnergyBlockEntity::new, pProperties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PowerStateProperty.POWER_STATE, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(PowerStateProperty.POWER_STATE, PowerState.DISABLED);
    }

    // 1.21.5 removed BlockBehaviour#onRemove; clearing the controller's energy flag on removal now runs from
    // ReactorEnergyBlockEntity#preRemoveSideEffects, which LevelChunk invokes on genuine removal only.
}

