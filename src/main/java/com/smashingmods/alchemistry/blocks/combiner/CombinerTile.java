package com.smashingmods.alchemistry.blocks.combiner;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemistry.blocks.AlchemistryBaseTile;
import com.smashingmods.alchemylib.tiles.CustomEnergyStorage;
import com.smashingmods.alchemylib.tiles.CustomStackHandler;
import com.smashingmods.alchemylib.tiles.EnergyTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class CombinerTile extends AlchemistryBaseTile implements EnergyTile {

    public static BlockEntityType<CombinerTile> type;

    public CombinerRecipe currentRecipe = null;
    public boolean recipeIsLocked = false;
    int progressTicks = 0;
    public boolean paused = false;
    public CustomStackHandler clientRecipeTarget;
    public CompoundTag recipeTargetNBT = null;


    public CombinerTile(BlockPos pos, BlockState state) {
        super(Registration.COMBINER_BE.get(), pos, state);
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
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    public void updateRecipe() {
        currentRecipe = CombinerRegistry.matchInputs(level, this.getInput());
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
                && (currentRecipe.output.getCount() + getOutput().getStackInSlot(0).getCount() <= currentRecipe.output.getMaxStackSize()) //output quantities can stack
                && (ItemStack.isSame(getOutput().getStackInSlot(0), currentRecipe.output) || getOutput().getStackInSlot(0).isEmpty()) //output item types can stack
                && currentRecipe.matchesHandlerStacks(this.getInput())
                && (!recipeIsLocked || ItemStack.isSame(CombinerRegistry.matchInputs(level, getInput()).output, currentRecipe.output));
    }

    public void process() {
        this.energy.extractEnergy(Config.COMBINER_ENERGY_PER_TICK.get(), false);
        this.setChanged();

        if (progressTicks < Config.COMBINER_TICKS_PER_OPERATION.get()) progressTicks++;
        else {
            progressTicks = 0;
            if (currentRecipe != null) {
                getOutput().setOrIncrement(0, currentRecipe.output.copy());
            }
            if (currentRecipe != null && currentRecipe.inputs.size() == 9) {
                CombinerRecipe thisRecipe = currentRecipe;
                for (int index = 0; index < 9; index++) {
                    //if (currentRecipe != null) {
                    ItemStack stack = thisRecipe.inputs.get(index);
                    if (stack != null && !stack.isEmpty()) {
                        getInput().decrementSlot(index, stack.getCount());
                    }
                    if (getInput().getStackInSlot(index).getItem() == Registration.SLOT_FILLER_ITEM.get()) {
                        getInput().decrementSlot(index, 1);
                    }
                    //}
                }
            }
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.recipeIsLocked = compound.getBoolean("recipeIsLocked");
        this.progressTicks = compound.getInt("progressTicks");
        this.paused = compound.getBoolean("paused");
        this.recipeTargetNBT = compound.getCompound("recipeTarget");
        clientRecipeTarget.setStackInSlot(0, ItemStack.of(recipeTargetNBT));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("recipeIsLocked", recipeIsLocked);
        compound.putInt("progressTicks", progressTicks);
        compound.putBoolean("paused", paused);
        compound.put("recipeTarget", clientRecipeTarget.getStackInSlot(0).serializeNBT());
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 9) {

            @Nonnull
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

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.COMBINER_ENERGY_CAPACITY.get());
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public Component getName() {
        return null;
    }
}