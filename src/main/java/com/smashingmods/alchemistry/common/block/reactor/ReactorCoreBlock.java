package com.smashingmods.alchemistry.common.block.reactor;

import com.smashingmods.alchemylib.api.blockentity.power.PowerState;
import com.smashingmods.alchemylib.api.blockentity.power.PowerStateProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ReactorCoreBlock extends RotatedPillarBlock {

    public ReactorCoreBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(PowerStateProperty.POWER_STATE).equals(PowerState.ON)) {
            return 15;
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PowerStateProperty.POWER_STATE);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return Objects.requireNonNull(super.getStateForPlacement(pContext)).setValue(PowerStateProperty.POWER_STATE, PowerState.OFF);
    }
}
