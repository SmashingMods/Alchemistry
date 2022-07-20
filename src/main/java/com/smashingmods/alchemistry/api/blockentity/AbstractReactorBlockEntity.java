package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.common.block.reactor.ReactorEnergyBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorInputBlockEntity;
import com.smashingmods.alchemistry.common.block.reactor.ReactorOutputBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
public abstract class AbstractReactorBlockEntity extends AbstractInventoryBlockEntity implements ReactorBlockEntity {

    private ReactorShape reactorShape;
    private ReactorType reactorType;

    private ReactorEnergyBlockEntity reactorEnergyBlockEntity;
    private ReactorInputBlockEntity reactorInputBlockEntity;
    private ReactorOutputBlockEntity reactorOutputBlockEntity;

    private boolean energyFound;
    private boolean inputFound;
    private boolean outputFound;

    public AbstractReactorBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (reactorShape == null) {
                setReactorShape(new ReactorShape(getBlockPos(), getReactorType(), level));
            }

            setMultiblockHandlers();
            if (isValidMultiblock()) {
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

    public void setMultiblockHandlers() {
        if (level != null && !level.isClientSide()) {
            BoundingBox reactorBox = getReactorShape().getFullBoundingBox();

            if (!energyFound) {
                BlockPos.betweenClosedStream(reactorBox)
                        .filter(blockPos -> level.getBlockEntity(blockPos) instanceof ReactorEnergyBlockEntity)
                        .findFirst()
                        .ifPresent(blockPos -> {
                            BlockState energyState = level.getBlockState(blockPos);
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 7);
                            level.setBlock(blockPos, energyState, 7);
                            energyFound = true;
                            reactorEnergyBlockEntity = (ReactorEnergyBlockEntity) level.getBlockEntity(blockPos);
                        });
            } else {
                reactorEnergyBlockEntity.setController(this);
            }

            if (reactorInputBlockEntity == null || !inputFound) {
                inputFound = false;
                BlockPos.betweenClosedStream(reactorBox)
                        .filter(blockPos -> level.getBlockEntity(blockPos) instanceof ReactorInputBlockEntity)
                        .findFirst()
                        .ifPresent(blockPos -> {
                            BlockState inputState = level.getBlockState(blockPos);
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 7);
                            level.setBlock(blockPos, inputState, 7);
                            inputFound = true;
                            reactorInputBlockEntity = (ReactorInputBlockEntity) level.getBlockEntity(blockPos);
                        });
            } else {
                reactorInputBlockEntity.setController(this);
            }

            if (reactorOutputBlockEntity == null || !outputFound) {
                outputFound = false;
                BlockPos.betweenClosedStream(reactorBox)
                        .filter(blockPos -> level.getBlockEntity(blockPos) instanceof ReactorOutputBlockEntity)
                        .findFirst()
                        .ifPresent(blockPos -> {
                            BlockState outputState = level.getBlockState(blockPos);
                            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 7);
                            level.setBlock(blockPos, outputState, 7);
                            outputFound = true;
                            reactorOutputBlockEntity = (ReactorOutputBlockEntity) level.getBlockEntity(blockPos);
                        });
            } else {
                reactorOutputBlockEntity.setController(this);
            }
        }
    }

    @Override
    public PowerState getPowerState() {
        return getBlockState().getValue(PowerStateProperty.POWER_STATE);
    }

    @Override
    public void setPowerState(PowerState pPowerState) {
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(PowerStateProperty.POWER_STATE, pPowerState));
        }
    }

    public void setEnergyFound(boolean pEnergyFound) {
        this.energyFound = pEnergyFound;
    }

    public void setInputFound(boolean pInputFound) {
        this.inputFound = pInputFound;
    }

    public void setOutputFound(boolean pOutputFound) {
        this.outputFound = pOutputFound;
    }

    public ReactorEnergyBlockEntity getReactorEnergyBlockEntity() {
        return reactorEnergyBlockEntity;
    }

    public ReactorInputBlockEntity getReactorInputBlockEntity() {
        return reactorInputBlockEntity;
    }

    public ReactorOutputBlockEntity getReactorOutputBlockEntity() {
        return reactorOutputBlockEntity;
    }

    @Override
    public boolean isValidMultiblock() {
        if (level != null && !level.isClientSide()) {
            return validateMultiblockShape(level, getReactorShape().createShapeMap()) && energyFound && inputFound && outputFound;
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        if (reactorEnergyBlockEntity != null) {
            pTag.put("reactorEnergyPos", blockPosToTag(reactorEnergyBlockEntity.getBlockPos()));
        }
        if (reactorInputBlockEntity != null) {
            pTag.put("reactorInputPos", blockPosToTag(reactorInputBlockEntity.getBlockPos()));
        }
        if (reactorOutputBlockEntity != null) {
            pTag.put("reactorOutputPos", blockPosToTag(reactorOutputBlockEntity.getBlockPos()));
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        if (level != null && !level.isClientSide()) {
            if (level.getBlockEntity(blockPosFromTag(pTag.getCompound("reactorEnergyPos"))) instanceof ReactorEnergyBlockEntity blockEntity) {
                reactorEnergyBlockEntity = blockEntity;
                energyFound = true;
            } else {
                energyFound = false;
            }
            if (level.getBlockEntity(blockPosFromTag(pTag.getCompound("reactorInputPos"))) instanceof ReactorInputBlockEntity blockEntity) {
                reactorInputBlockEntity = blockEntity;
                inputFound = true;
            } else {
                inputFound = false;
            }
            if (level.getBlockEntity(blockPosFromTag(pTag.getCompound("reactorOutputPos"))) instanceof ReactorOutputBlockEntity blockEntity) {
                reactorOutputBlockEntity = blockEntity;
                outputFound = true;
            } else {
                outputFound = false;
            }
        }
    }

    private CompoundTag blockPosToTag(BlockPos pBlockPos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pBlockPos.getX());
        tag.putInt("y", pBlockPos.getY());
        tag.putInt("z", pBlockPos.getZ());
        return tag;
    }

    private BlockPos blockPosFromTag(CompoundTag pTag) {
        return new BlockPos(pTag.getInt("x"), pTag.getInt("y"), pTag.getInt("z"));
    }
}
