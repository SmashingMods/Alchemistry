package com.smashingmods.alchemistry.block.combiner;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.api.blockentity.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import com.smashingmods.alchemistry.block.AlchemistryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class CombinerBlockEntity extends AlchemistryBlockEntity implements EnergyBlockEntity {

    public static BlockEntityType<CombinerBlockEntity> type;

    public CombinerRecipe currentRecipe = null;
    public boolean recipeIsLocked = false;
    int progressTicks = 0;
    public boolean paused = false;
    public CustomStackHandler clientRecipeTarget;
    public CompoundTag recipeTargetNBT = null;

    public CombinerBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.COMBINER_BE.get(), pos, state);
        clientRecipeTarget = new CustomStackHandler(this, 1) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }

            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    public void updateRecipe() {
        currentRecipe = CombinerRegistry.matchInputs(level, this.getInputHandler());
        if(currentRecipe != null) clientRecipeTarget.setStackInSlot(0, currentRecipe.output.copy());
        setChanged();
    }

    public void updateRecipe(ItemStack output) {
        this.currentRecipe = CombinerRegistry.matchOutput(level, output);
        ItemStack temp = ItemStack.EMPTY;
        if (currentRecipe != null) temp = currentRecipe.output.copy();
        clientRecipeTarget.setStackInSlot(0, temp);
        setChanged();
    }

    public void tickServer() {
        if (recipeTargetNBT != null) {
            this.updateRecipe(ItemStack.of(recipeTargetNBT));
            recipeTargetNBT = null;
        }
        ItemStack displayStack = ItemStack.EMPTY;
        if (currentRecipe != null && currentRecipe.output != null)
            displayStack = currentRecipe.output.copy();
        if (recipeIsLocked) clientRecipeTarget.setStackInSlot(0, displayStack);
        if (!this.paused && canProcess()) {
            process();
        }
        this.updateGUIEvery(5);
        //Messages.sendToTracking(new BlockEntityPacket(this.getBlockPos(), this.getUpdateTag()), this.level, this.getBlockPos());
    }

    public boolean canProcess() {
        return currentRecipe != null
                //&& (currentRecipe.gamestage == "" || hasCurrentRecipeStage()) TODO
                && energy.getEnergyStored() >= Config.COMBINER_ENERGY_PER_TICK.get()//ConfigHandler.combinerEnergyPerTick!! //has enough energy
                && (currentRecipe.output.getCount() + getOutputHandler().getStackInSlot(0).getCount() <= currentRecipe.output.getMaxStackSize()) //output quantities can stack
                && (ItemStack.isSame(getOutputHandler().getStackInSlot(0), currentRecipe.output) || getOutputHandler().getStackInSlot(0).isEmpty()) //output item types can stack
                && currentRecipe.matchesHandlerStacks(this.getInputHandler())
                && (!recipeIsLocked || ItemStack.isSame(CombinerRegistry.matchInputs(level, getInputHandler()).output, currentRecipe.output));
    }

    public void process() {
        this.energy.extractEnergy(Config.COMBINER_ENERGY_PER_TICK.get(), false);
        this.setChanged();

        if (progressTicks < Config.COMBINER_TICKS_PER_OPERATION.get()) progressTicks++;
        else {
            progressTicks = 0;
            if (currentRecipe != null) {
                getOutputHandler().setOrIncrement(0, currentRecipe.output.copy());
            }
            if (currentRecipe != null && currentRecipe.inputs.size() == 9) {
                CombinerRecipe thisRecipe = currentRecipe;
                for (int index = 0; index < 9; index++) {
                    //if (currentRecipe != null) {
                    ItemStack stack = thisRecipe.inputs.get(index);
                    if (stack != null && !stack.isEmpty()) {
                        getInputHandler().decrementSlot(index, stack.getCount());
                    }
                    if (getInputHandler().getStackInSlot(index).getItem() == Registry.SLOT_FILLER_ITEM.get()) {
                        getInputHandler().decrementSlot(index, 1);
                    }
                    //}
                }
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        this.recipeIsLocked = compound.getBoolean("recipeIsLocked");
        this.progressTicks = compound.getInt("progressTicks");
        this.paused = compound.getBoolean("paused");
        this.recipeTargetNBT = compound.getCompound("recipeTarget");
        clientRecipeTarget.setStackInSlot(0, ItemStack.of(recipeTargetNBT));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("recipeIsLocked", recipeIsLocked);
        compound.putInt("progressTicks", progressTicks);
        compound.putBoolean("paused", paused);
        compound.put("recipeTarget", clientRecipeTarget.getStackInSlot(0).serializeNBT());
    }

    @Override
    public CustomStackHandler getInputHandler() {
        if (inputHandler == null) {
            inputHandler = new CustomStackHandler(this, 9) {

                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    if (!recipeIsLocked) return super.isItemValid(slot, stack);
                    else if (recipeIsLocked
                            && currentRecipe != null
                            && !currentRecipe.inputs.isEmpty()
                            && (ItemStack.isSame(stack, currentRecipe.inputs.get(slot)))) {
                        return super.isItemValid(slot, stack);
                    } else return false;
                }

                @Override
                public void onContentsChanged(int slot) {
                    super.onContentsChanged(slot);
                    if (!recipeIsLocked) updateRecipe();
                }
            };
        }
        return inputHandler;
    }

    @Override
    public CustomStackHandler getOutputHandler() {
        if (outputHandler == null) {
            outputHandler = new CustomStackHandler(this, 1) {
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return false;
                }
            };
        }
        return outputHandler;
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.COMBINER_ENERGY_CAPACITY.get());
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }
}