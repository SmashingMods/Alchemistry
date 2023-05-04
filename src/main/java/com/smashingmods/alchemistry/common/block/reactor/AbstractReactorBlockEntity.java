package com.smashingmods.alchemistry.common.block.reactor;

import java.util.function.Consumer;

import com.mojang.math.Vector3f;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemylib.api.block.AbstractProcessingBlock;
import com.smashingmods.alchemylib.api.blockentity.power.PowerState;
import com.smashingmods.alchemylib.api.blockentity.power.PowerStateProperty;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class AbstractReactorBlockEntity extends AbstractInventoryBlockEntity implements ReactorBlockEntity {

    private ReactorShape reactorShape;
    private ReactorType reactorType;

    private ReactorEnergyBlockEntity reactorEnergyBlockEntity;
    private ReactorInputBlockEntity reactorInputBlockEntity;
    private ReactorOutputBlockEntity reactorOutputBlockEntity;

    private boolean energyFound;
    private boolean inputFound;
    private boolean outputFound;

    /**
     * True if the reactor should actively push it's outputs to a connected
     * container (such as a chest). Otherwise the reactor will only passively
     * provide it's outputs.
     */
    private boolean autoeject = false;

    public AbstractReactorBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(Alchemistry.MODID, pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (reactorShape == null) {
                setReactorShape(new ReactorShape(getBlockPos(), getReactorType(), level));
            }

            setMultiblockHandlers();
            if (isValidMultiblock()) {
                switch (getPowerState()) {
                    case ON -> {
                        if (!isProcessingPaused()) {
                            BlockPos coreCenter = reactorShape.getCoreBoundingBox().getCenter();
                            DustParticleOptions options = new DustParticleOptions(new Vector3f(1f, 1f, 0.5f), 0.15f);
                            ((ServerLevel) level).sendParticles(options,
                                    coreCenter.getX(),
                                    coreCenter.getY(),
                                    coreCenter.getZ(),
                                    50,
                                    1.5f,
                                    1.5f,
                                    1.5f,
                                    0f);
                        }
                    }
                    case OFF, DISABLED -> {
                        if (getEnergyHandler().getEnergyStored() > getEnergyPerTick()) {
                            setPowerState(PowerState.STANDBY);
                        } else {
                            setPowerState(PowerState.OFF);
                        }
                    }
                }
                if (!isProcessingPaused()) {
                    if (!getInputHandler().isEmpty()) {
                        updateRecipe();
                    }
                    if (canProcessRecipe()) {
                        setPowerState(PowerState.ON);
                        processRecipe();
                    } else {
                        if (getEnergyHandler().getEnergyStored() > getEnergyPerTick()) {
                            setPowerState(PowerState.STANDBY);
                        } else {
                            setPowerState(PowerState.OFF);
                        }
                    }
                } else {
                    if (getPowerState().equals(PowerState.ON)) {
                        setPowerState(PowerState.STANDBY);
                    }
                }
                if (isAutoEject()) {
                    tryEjectOutputs();
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

            if (reactorEnergyBlockEntity == null || !energyFound) {
                BlockPos.betweenClosedStream(reactorBox)
                        .filter(blockPos -> level.getBlockEntity(blockPos) instanceof ReactorEnergyBlockEntity)
                        .findFirst()
                        .ifPresent(blockPos -> {
                            setEnergyFound(true);
                            reactorEnergyBlockEntity = (ReactorEnergyBlockEntity) level.getBlockEntity(blockPos);
                            reactorEnergyBlockEntity.setController(this);
                            level.updateNeighborsAt(blockPos, BlockRegistry.REACTOR_ENERGY.get());
                        });
            } else {
                reactorEnergyBlockEntity.setController(this);
            }

            if (reactorInputBlockEntity == null || !inputFound) {
                BlockPos.betweenClosedStream(reactorBox)
                        .filter(blockPos -> level.getBlockEntity(blockPos) instanceof ReactorInputBlockEntity)
                        .findFirst()
                        .ifPresent(blockPos -> {
                            inputFound = true;
                            reactorInputBlockEntity = (ReactorInputBlockEntity) level.getBlockEntity(blockPos);
                            reactorInputBlockEntity.setController(this);
                            level.updateNeighborsAt(blockPos, BlockRegistry.REACTOR_INPUT.get());
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
                            outputFound = true;
                            reactorOutputBlockEntity = (ReactorOutputBlockEntity) level.getBlockEntity(blockPos);
                            reactorOutputBlockEntity.setController(this);
                            level.updateNeighborsAt(blockPos, BlockRegistry.REACTOR_OUTPUT.get());
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

    public void setAutoeject(boolean autoeject) {
        this.autoeject = autoeject;
    }

    @Override
    public boolean isValidMultiblock() {
        if (level != null && !level.isClientSide()) {

            Consumer<BlockPos> handleCorePowerState = blockPos -> {
                if (level != null && !level.isClientSide()) {
                    BlockState blockState = level.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof ReactorCoreBlock) {
                        PowerState coreState = blockState.getValue(PowerStateProperty.POWER_STATE);
                        switch (getPowerState()) {
                            case DISABLED, OFF -> {
                                if (coreState.equals(PowerState.ON)) {
                                    BlockState newState = blockState.setValue(PowerStateProperty.POWER_STATE, PowerState.OFF);
                                    level.setBlock(blockPos, newState, 7);
                                }
                            }
                            case STANDBY, ON -> {
                                if (coreState.equals(PowerState.OFF)) {
                                    BlockState newState = blockState.setValue(PowerStateProperty.POWER_STATE, PowerState.ON);
                                    level.setBlock(blockPos, newState, 7);
                                }
                            }
                        }
                    }
                }
            };

            BlockPos.betweenClosedStream(reactorShape.getCoreBoundingBox()).forEach(handleCorePowerState);
            return validateMultiblockShape(level, getReactorShape().createShapeMap()) && energyFound && inputFound && outputFound;
        }
        return false;
    }

    public void resetIO() {
        if (level != null && !level.isClientSide()) {
            setMultiblockHandlers();
            if (reactorEnergyBlockEntity != null) {
                BlockState energyState = reactorEnergyBlockEntity.getBlockState();
                BlockPos energyPos = reactorEnergyBlockEntity.getBlockPos();
                level.setBlockAndUpdate(energyPos, Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(energyPos, energyState);
            }

            if (reactorInputBlockEntity != null) {
                BlockState inputState = reactorInputBlockEntity.getBlockState();
                BlockPos inputPos = reactorInputBlockEntity.getBlockPos();
                level.setBlockAndUpdate(inputPos, Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(inputPos, inputState);
            }

            if (reactorOutputBlockEntity != null) {
                BlockState outputState = reactorOutputBlockEntity.getBlockState();
                BlockPos outputPos = reactorOutputBlockEntity.getBlockPos();
                level.setBlockAndUpdate(outputPos, Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(outputPos, outputState);
            }
        }
    }

    public void onRemove() {
        if (level != null && !level.isClientSide()) {
            resetIO();
            BlockPos.betweenClosedStream(reactorShape.getCoreBoundingBox()).forEach(blockPos -> {
                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.getBlock() instanceof ReactorCoreBlock) {
                    BlockState offState = blockState.setValue(PowerStateProperty.POWER_STATE, PowerState.OFF);
                    level.setBlockAndUpdate(blockPos, offState);
                }
            });
        }
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
        pTag.putBoolean("autoeject", autoeject);
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

        autoeject = pTag.getBoolean("autoeject");
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

    public boolean isAutoEject() {
        return autoeject;
    }

    public void tryEjectOutputs() {
        if (reactorOutputBlockEntity == null) {
            return;
        }

        Direction outputDirection = reactorOutputBlockEntity.getBlockState().getValue(AbstractProcessingBlock.FACING);
        BlockEntity target = level.getBlockEntity(reactorOutputBlockEntity.getBlockPos().relative(outputDirection));

        if (target == null) {
            return; // Output pointing to air or a solid block (that doesn't happen to be a container or something)
        }

        IItemHandler targetHandler = target.getCapability(ForgeCapabilities.ITEM_HANDLER, outputDirection.getOpposite()).orElse(null);

        if (targetHandler != null) {
            ProcessingSlotHandler outputHandler = getOutputHandler();
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack outputStack = outputHandler.getStackInSlot(i);
                if (outputStack.isEmpty()) {
                    continue; // No need to transfer an empty itemstack
                }
                ItemStack remaining = ItemHandlerHelper.insertItem(targetHandler, outputStack, false);
                if (remaining.getCount() == outputStack.getCount()) {
                    // Item not transfered - most likely no other items will be transfered either,
                    // so we can break completely.
                    break;
                }
                outputHandler.setStackInSlot(i, remaining);
            }
        }
    }
}
