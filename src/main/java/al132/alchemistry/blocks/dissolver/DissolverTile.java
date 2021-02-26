package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.Config;
import al132.alchemistry.Ref;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alib.tiles.CustomEnergyStorage;
import al132.alib.tiles.CustomStackHandler;
import al132.alib.tiles.EnergyTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static al132.alchemistry.utils.StackUtils.canStacksMerge;

public class DissolverTile extends AlchemistryBaseTile implements EnergyTile {

    private boolean outputSuccessful = true;
    private ItemStack outputThisTick = ItemStack.EMPTY;
    private DissolverRecipe currentRecipe = null;
    private NonNullList<ItemStack> outputBuffer = NonNullList.create();

    public DissolverTile() {
        super(Ref.dissolverTile);
    }

    @Override
    public void tick() {
        if (world.isRemote) return;
        if (currentRecipe == null) updateRecipe();
        if (!getInput().getStackInSlot(0).isEmpty() || !outputBuffer.isEmpty()) {
            if (canProcess()) {
                process();
            }
        }
        this.notifyGUIEvery(5);
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
            getInput().decrementSlot(0, currentRecipe.inputIngredient.ingredient.getMatchingStacks()[0].getCount());
        }

        //If output didn't happen or didn't fail last tick, queue up next output single stack
        if (outputSuccessful) {
            if (outputBuffer.size() > 0) outputThisTick = outputBuffer.get(0).split(Config.DISSOLVER_SPEED.get());
            else outputThisTick = ItemStack.EMPTY;

            if (outputBuffer.size() > 0 && outputBuffer.get(0).isEmpty()) outputBuffer.remove(0);
            outputSuccessful = false;
        }

        //Try to stack output with existing stacks in output, if possible
        for (int i = 0; i < getOutput().getSlots(); i++) {
            if (canStacksMerge(outputThisTick, getOutput().getStackInSlot(i), false)) {
                getOutput().setOrIncrement(i, outputThisTick);
                outputSuccessful = true;
                break;
            }
        }
        //Otherwise try the empty stacks
        if (!outputSuccessful) {
            for (int i = 0; i < getOutput().getSlots(); i++) {
                if (canStacksMerge(outputThisTick, getOutput().getStackInSlot(i), true)) {
                    getOutput().setOrIncrement(i, outputThisTick);
                    outputSuccessful = true;
                    break;
                }
            }
        }

        //consume energy and single stack if successful, won't be designated as such until there's a "hit" above
        if (outputSuccessful) {
            this.energy.extractEnergy(Config.DISSOLVER_ENERGY_PER_TICK.get(), false);
            outputThisTick = ItemStack.EMPTY;
        }
    }


    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity player) {
        return new DissolverContainer(i, world, pos, playerInv, player);
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 1) {
            @Nonnull
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (!this.getStackInSlot(slot).isEmpty()) return super.isItemValid(slot, stack);
                else if (DissolverRegistry.match(world, stack, false) != null) return super.isItemValid(slot, stack);
                else return false;
            }

            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                updateRecipe();
            }
        };
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 10) {
            @Nonnull
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }


    public void updateRecipe() {
        this.currentRecipe = DissolverRegistry.match(world, getInput().getStackInSlot(0), true);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.outputSuccessful = compound.getBoolean("OutputSuccessful");
        ItemStackHelper.loadAllItems(compound.getCompound("OutputBuffer"), outputBuffer);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putBoolean("OutputSuccessful", this.outputSuccessful);
        compound.put("OutputBuffer", ItemStackHelper.saveAllItems(new CompoundNBT(), outputBuffer));
        return compound;
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