package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.common.block.reactor.ReactorEnergyInputBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorItemInputBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorItemOutputBlockEntity;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractReactorBlockEntity extends AbstractInventoryBlockEntity implements ReactorBlockEntity {

    private ReactorShape reactorShape;
    private ReactorType reactorType;

    public AbstractReactorBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (reactorShape == null) {
                setReactorShape(new ReactorShape(getBlockPos(), getReactorType(), level));
            }

            if (isValidMultiblock()) {
                setMultiblockHandlers();

                if (getPowerState().equals(PowerState.DISABLED)) {
                    if (getEnergyHandler().getEnergyStored() > 0) {
                        setPowerState(PowerState.STANDBY);
                    } else {
                        setPowerState(PowerState.OFF);
                    }
                }

                if (!getPaused()) {
                    if (!getRecipeLocked()) {
                        updateRecipe();
                    }
                    if (canProcessRecipe()) {
                        setPowerState(PowerState.ON);
                        processRecipe();
                    } else {
                        setProgress(0);
                        if (getEnergyHandler().getEnergyStored() > 0) {
                            setPowerState(PowerState.STANDBY);
                        } else {
                            setPowerState(PowerState.OFF);
                        }
                    }
                }
            } else {
                setPowerState(PowerState.DISABLED);
            }
        }
    }

    @Override
    public ReactorShape getReactorShape() {
        return reactorShape;
    }

    @Override
    public void setReactorShape(ReactorShape reactorShape) {
        this.reactorShape = reactorShape;
    }

    @Override
    public ReactorType getReactorType() {
        return reactorType;
    }

    @Override
    public void setReactorType(ReactorType pReactorType) {
        this.reactorType = pReactorType;
    }

    @SuppressWarnings("ConstantConditions")
    public void setMultiblockHandlers() {
        BlockPos.betweenClosedStream(getReactorShape().getFullBoundingBox()).forEach(blockPos -> {
            Block block = level.getBlockState(blockPos).getBlock();
            if (block.equals(BlockRegistry.REACTOR_ENERGY_INPUT.get())) {
                if (level.getBlockEntity(blockPos) instanceof ReactorEnergyInputBlockEntity blockEntity) {
                    blockEntity.setEnergyHandler(getEnergyHandler());
                }
            }
            if (block.equals(BlockRegistry.REACTOR_ITEM_INPUT.get())) {
                if (level.getBlockEntity(blockPos) instanceof ReactorItemInputBlockEntity blockEntity) {
                    blockEntity.setInputHandler(getInputHandler());
                }
            }
            if (block.equals(BlockRegistry.REACTOR_ITEM_OUTPUT.get())) {
                if (level.getBlockEntity(blockPos) instanceof ReactorItemOutputBlockEntity blockEntity) {
                    blockEntity.setOutputHandler(getOutputHandler());
                }
            }
        });
    }

    public PowerState getPowerState() {
        return getBlockState().getValue(PowerStateProperty.POWER_STATE);
    }

    @Override
    public void setPowerState(PowerState pPowerState) {
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(PowerStateProperty.POWER_STATE, pPowerState));
        }
    }

    @Override
    public boolean isValidMultiblock() {
        if (level != null && !level.isClientSide()) {
            return validateMultiblockShape(level, getReactorShape().createShapeMap());
        }
        return false;
    }
}
