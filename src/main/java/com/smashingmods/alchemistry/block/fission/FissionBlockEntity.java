package com.smashingmods.alchemistry.block.fission;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.block.AlchemistryBlockEntity;
import com.smashingmods.alchemistry.block.PowerStatus;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import com.smashingmods.chemlib.items.ElementItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

import static com.smashingmods.alchemistry.block.PowerStatus.*;

public class FissionBlockEntity extends AlchemistryBlockEntity implements EnergyBlockEntity {

    protected IEnergyStorage energyStorage;
    protected LazyOptional<IEnergyStorage> energyHolder = LazyOptional.of(() -> energyStorage);

    int progressTicks = 0;
    ItemStack recipeOutput1 = ItemStack.EMPTY;
    ItemStack recipeOutput2 = ItemStack.EMPTY;
    boolean isValidMultiblock = false;
    int checkMultiblockTicks = 0;
    boolean firstTick = true;

    public FissionBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.FISSION_CONTOLLER_BE.get(), pos, state);
        this.energyStorage = new EnergyStorage(Config.FISSION_ENERGY_CAPACITY.get());
    }

    public ItemStack getInputStack() {
        return this.getInputHandler().getStackInSlot(0);
    }

    private void refreshRecipe() {
        if (!this.getInputStack().isEmpty() && (this.getInputStack().getItem() instanceof ElementItem)) {
            int inputAtomNumber = ((ElementItem) this.getInputStack().getItem()).atomicNumber;
            int outputAtomNumber = inputAtomNumber / 2;
            if (inputAtomNumber != 0) {
                if (inputAtomNumber % 2 == 0) {
                    if (ElementRegistry.elements.containsKey(outputAtomNumber) && ElementRegistry.elements.get(outputAtomNumber) != null) {
                        recipeOutput1 = new ItemStack(ElementRegistry.elements.get(outputAtomNumber), 2);//(quantity = 2, meta = meta / 2);
                        recipeOutput2 = ItemStack.EMPTY;
                    }
                } else {
                    if (ElementRegistry.elements.containsKey(outputAtomNumber) && ElementRegistry.elements.containsKey((outputAtomNumber) + 1)) {
                        recipeOutput1 = new ItemStack(ElementRegistry.elements.get((outputAtomNumber) + 1));//.elements.toStack(meta = (meta / 2) + 1).
                        recipeOutput2 = new ItemStack(ElementRegistry.elements.get(outputAtomNumber));
                    }
                }
            }
        } else {
            recipeOutput1 = ItemStack.EMPTY;
            recipeOutput2 = ItemStack.EMPTY;
        }
    }

    public void tickServer() {
        if (level.isClientSide) return;

        if (firstTick) {
            refreshRecipe();
            firstTick = false;
        }

        boolean isActive = !this.getInputStack().isEmpty();
        checkMultiblockTicks++;
        if (checkMultiblockTicks >= 20) {
            updateMultiblock();
            checkMultiblockTicks = 0;
        }
        BlockState state = this.level.getBlockState(this.getBlockPos());
        if (state.getBlock() != Registry.FISSION_CONTROLLER_BLOCK.get()) return;
        PowerStatus currentStatus = state.getValue(FissionControllerBlock.STATUS);
        if (this.isValidMultiblock) {
            if (isActive) {
                if (currentStatus != ON) this.level.setBlockAndUpdate(this.getBlockPos(), state.setValue(FissionControllerBlock.STATUS, ON));
            } else if (currentStatus != STANDBY)
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(FissionControllerBlock.STATUS, STANDBY));
        } else if (currentStatus != OFF) level.setBlockAndUpdate(this.getBlockPos(), state.setValue(FissionControllerBlock.STATUS, OFF));

        if (canProcess()) {
            process();
        }
        this.updateGUIEvery(5);
    }

    public boolean canProcess() {
        ItemStack output0 = getOutputHandler().getStackInSlot(0);
        ItemStack output1 = getOutputHandler().getStackInSlot(1);
        return this.isValidMultiblock
                && !recipeOutput1.isEmpty()
                && (ItemStack.isSame(output0, recipeOutput1) || output0.isEmpty())
                && (ItemStack.isSame(output1, recipeOutput2) || output1.isEmpty())
                && output0.getCount() + recipeOutput1.getCount() <= recipeOutput1.getMaxStackSize()
                && output1.getCount() + recipeOutput2.getCount() <= recipeOutput2.getMaxStackSize()
                && energyStorage.getEnergyStored() >= Config.FISSION_ENERGY_PER_TICK.get();
    }

    public void process() {
        if (progressTicks < Config.FISSION_TICKS_PER_OPERATION.get()) {
            progressTicks++;
        } else {
            progressTicks = 0;
            getOutputHandler().setOrIncrement(0, recipeOutput1.copy());
            if (!recipeOutput2.isEmpty()) getOutputHandler().setOrIncrement(1, recipeOutput2.copy());
            getInputHandler().decrementSlot(0, 1); //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyStorage.extractEnergy(Config.FISSION_ENERGY_PER_TICK.get(), false);//ConfigHandler.fissionEnergyPerTick!!, false)
        setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.isValidMultiblock = compound.getBoolean("isValidMultiblock");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("progressTicks", progressTicks);
        compound.putBoolean("isValidMultiblock", isValidMultiblock);
    }

    public void updateMultiblock() {
        this.isValidMultiblock = validateMultiblock();
    }

    private boolean containsCasing(BlockPos pos) {
        return this.level.getBlockState(pos).getBlock() == Registry.FISSION_CASING_BLOCK.get();
    }

    private boolean containsCore(BlockPos pos) {
        return this.level.getBlockState(pos).getBlock() == Registry.FISSION_CORE_BLOCK.get();
    }

    private boolean containsFissionPart(BlockPos pos) {
        Block block = this.level.getBlockState(pos).getBlock();
        return block == Registry.FISSION_CASING_BLOCK.get()
                || block == Registry.FISSION_CORE_BLOCK.get()
                || block == Registry.FISSION_CONTROLLER_BLOCK.get();
    }

    public boolean validateMultiblock() {
        Direction multiblockDirection = level.getBlockState(this.getBlockPos()).getValue(FissionControllerBlock.FACING).getOpposite();
        if (multiblockDirection == null) return false;
        BiFunction<BlockPos, Integer, BlockPos> offsetUp = (BlockPos pos, Integer amt) -> pos.relative(Direction.UP, amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetLeft = (BlockPos pos, Integer amt) -> new BlockPos(pos.relative(multiblockDirection.getClockWise(), amt));//.rotate(Rotation.CLOCKWISE_90));
        BiFunction<BlockPos, Integer, BlockPos> offsetRight = (BlockPos pos, Integer amt) -> new BlockPos(pos.relative(multiblockDirection.getCounterClockWise(), /*-1 **/ amt));//.rotate(Rotation.CLOCKWISE_90));
        BiFunction<BlockPos, Integer, BlockPos> offsetBack = (BlockPos pos, Integer amt) -> pos.relative(multiblockDirection, amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetDown = (BlockPos pos, Integer amt) -> pos.relative(Direction.DOWN, amt);

        BlockPos coreBottom = offsetBack.apply(this.getBlockPos(), 3);
        coreBottom = offsetUp.apply(coreBottom, 1);
        BlockPos coreTop = offsetUp.apply(coreBottom, 2);
        boolean coreMatches = BlockPos.betweenClosedStream(coreBottom, coreTop).allMatch(this::containsCore);


        //A cube of all blocks surrounding the fission multiblock, checking to ensure no other fission multiblocks are overlapping/sharing
        BlockPos outsideCorner1 = offsetLeft.apply(this.getBlockPos(), 3);
        outsideCorner1 = offsetDown.apply(outsideCorner1, 1);
        final BlockPos outsideCorner1Final = outsideCorner1; //java doesn't like non-final fields in the lambda below..
        BlockPos outsideCorner2 = offsetRight.apply(outsideCorner1, 6);
        outsideCorner2 = offsetUp.apply(outsideCorner2, 6);
        outsideCorner2 = offsetBack.apply(outsideCorner2, 6);

        long borderingParts = BlockPos.betweenClosedStream(outsideCorner1, outsideCorner2).filter(it -> {
            int sharedAxes = 0;
            if (it.getX() == outsideCorner1Final.getX() || it.getX() == outsideCorner1Final.getX()) sharedAxes++;
            if (it.getY() == outsideCorner1Final.getY() || it.getY() == outsideCorner1Final.getY()) sharedAxes++;
            if (it.getZ() == outsideCorner1Final.getZ() || it.getZ() == outsideCorner1Final.getZ()) sharedAxes++;
            return sharedAxes >= 1;
        }).filter(it -> !it.equals(this.getBlockPos())).filter(this::containsFissionPart).count();
        //.count(this::containsFissionPart);


        BlockPos casingCorner1 = offsetLeft.apply(this.getBlockPos(), 2);
        casingCorner1 = offsetBack.apply(casingCorner1, 1);
        final BlockPos casingCorner1Final = casingCorner1;
        BlockPos casingCorner2 = offsetRight.apply(casingCorner1, 4);
        casingCorner2 = offsetBack.apply(casingCorner2, 4);
        casingCorner2 = offsetUp.apply(casingCorner2, 4);
        final BlockPos casingCorner2Final = casingCorner2;

        boolean casingMatches = BlockPos.betweenClosedStream(casingCorner1, casingCorner2).filter(it -> {
            int sharedAxes = 0;
            if (it.getX() == casingCorner1Final.getX() || it.getX() == casingCorner2Final.getX()) sharedAxes++;
            if (it.getY() == casingCorner1Final.getY() || it.getY() == casingCorner2Final.getY()) sharedAxes++;
            if (it.getZ() == casingCorner1Final.getZ() || it.getZ() == casingCorner2Final.getZ()) sharedAxes++;

            return sharedAxes >= 1;
        }).allMatch(this::containsCasing);

        return casingMatches && coreMatches && (borderingParts == 0);
    }

    @Override
    public CustomStackHandler getInputHandler() {
        if (inputHandler == null) {
            inputHandler = new CustomStackHandler(this, 1) {
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return stack.getItem() instanceof ElementItem;
                }

                @Override
                public void onContentsChanged(int slot) {
                    ((FissionBlockEntity) this.blockEntity).refreshRecipe();
                    super.onContentsChanged(slot);
                }
            };
        }
        return inputHandler;
    }

    @Override
    public CustomStackHandler getOutputHandler() {
        if (outputHandler == null) {
            outputHandler = new CustomStackHandler(this, 2) {
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return false;
                }
            };
        }
        return outputHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.isValidMultiblock) return super.getCapability(cap, side);
        else return LazyOptional.empty();
    }

    @Override
    public LazyOptional<IEnergyStorage> getEnergy() {
        return this.energyHolder;
    }
}