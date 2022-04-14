package com.smashingmods.alchemistry.block.dissolver;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.blockentity.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.block.AlchemistryBlockEntity;
import com.smashingmods.alchemistry.utils.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.smashingmods.alchemistry.utils.StackUtils.canStacksMerge;

public class DissolverBlockEntity extends AlchemistryBlockEntity implements EnergyBlockEntity {

    private boolean outputSuccessful = true;
    private ItemStack outputThisTick = ItemStack.EMPTY;
    private DissolverRecipe currentRecipe = null;
    private NonNullList<ItemStack> outputBuffer = NonNullList.create();

    public DissolverBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.DISSOLVER_BE.get(), pos, state);
    }

    public void tickServer() {
        if (level.isClientSide) return;
        if (currentRecipe == null) updateRecipe();
        if (!getInputHandler().getStackInSlot(0).isEmpty() || !outputBuffer.isEmpty()) {
            if (canProcess()) {
                process();
            }
        }
        this.updateGUIEvery(5);
    }

    public boolean canProcess() {
        return energy.getEnergyStored() >= Config.DISSOLVER_ENERGY_PER_TICK.get()
                && (currentRecipe != null || !outputBuffer.isEmpty());
    }

    //tries to output a stack with getCount of one from the current recipe output buffer each tick
    public void process() {
        //if no output buffer, set the buffer to recipe outputs
        if (outputBuffer.isEmpty()) {
            outputBuffer = currentRecipe.outputs.calculateOutput();
            getInputHandler().decrementSlot(0, currentRecipe.inputIngredient.ingredient.getItems()[0].getCount());
        }

        //If output didn't happen or didn't fail last tick, queue up next output single stack
        if (outputSuccessful) {
            if (outputBuffer.size() > 0) outputThisTick = outputBuffer.get(0).split(Config.DISSOLVER_SPEED.get());
            else outputThisTick = ItemStack.EMPTY;

            if (outputBuffer.size() > 0 && outputBuffer.get(0).isEmpty()) outputBuffer.remove(0);
            outputSuccessful = false;
        }

        //Try to stack output with existing stacks in output, if possible
        for (int i = 0; i < getOutputHandler().getSlots(); i++) {
            if (canStacksMerge(outputThisTick, getOutputHandler().getStackInSlot(i), false)) {
                getOutputHandler().setOrIncrement(i, outputThisTick);
                outputSuccessful = true;
                break;
            }
        }
        //Otherwise try the empty stacks
        if (!outputSuccessful) {
            for (int i = 0; i < getOutputHandler().getSlots(); i++) {
                if (canStacksMerge(outputThisTick, getOutputHandler().getStackInSlot(i), true)) {
                    getOutputHandler().setOrIncrement(i, outputThisTick);
                    outputSuccessful = true;
                    break;
                }
            }
        }

        //consume energy and single stack if successful, won't be designated as such until there's a "hit" above
        if (outputSuccessful) {
            this.energy.extractEnergy(Config.DISSOLVER_ENERGY_PER_TICK.get(), false);
            this.setChanged();
            outputThisTick = ItemStack.EMPTY;
        }
    }

    @Override
    public CustomStackHandler getInputHandler() {
        if (inputHandler == null) {
            inputHandler = new CustomStackHandler(this, 1) {
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    if (!this.getStackInSlot(slot).isEmpty()) return super.isItemValid(slot, stack);
                    else if (DissolverRegistry.match(level, stack, false) != null)
                        return super.isItemValid(slot, stack);
                    else return false;
                }

                @Override
                public void onContentsChanged(int slot) {
                    super.onContentsChanged(slot);
                    updateRecipe();
                }
            };
        }
        return inputHandler;
    }

    @Override
    public CustomStackHandler getOutputHandler() {
        if (outputHandler == null) {
            outputHandler = new CustomStackHandler(this, 10) {
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return false;
                }
            };
        }
        return outputHandler;
    }


    public void updateRecipe() {
        this.currentRecipe = DissolverRegistry.match(level, getInputHandler().getStackInSlot(0), true);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        this.outputSuccessful = compound.getBoolean("OutputSuccessful");
        StackUtils.loadAllItems(compound.getCompound("OutputBuffer"), outputBuffer);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("OutputSuccessful", this.outputSuccessful);
        compound.put("OutputBuffer", StackUtils.saveAllItems(new CompoundTag(), outputBuffer));
        //return compound;
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.DISSOLVER_ENERGY_CAPACITY.get());
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }
}