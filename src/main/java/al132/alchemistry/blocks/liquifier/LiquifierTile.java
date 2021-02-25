package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.Config;
import al132.alchemistry.Ref;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alib.tiles.CustomEnergyStorage;
import al132.alib.tiles.CustomStackHandler;
import al132.alib.tiles.EnergyTile;
import al132.alib.tiles.FluidTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LiquifierTile extends AlchemistryBaseTile implements EnergyTile, FluidTile {

    public FluidTank outputTank;
    private LiquifierRecipe currentRecipe = null;
    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> outputTank);

    public int progressTicks = 0;

    public LiquifierTile() {
        super(Ref.liquifierTile);
        outputTank = new FluidTank(10000);
    }

    private void updateRecipe() {
        ItemStack inputStack = this.getInput().getStackInSlot(0);
        if (!inputStack.isEmpty() && (currentRecipe == null || currentRecipe.input.toStacks().stream().anyMatch(x -> ItemStack.areItemsEqual(x, inputStack)))) {
            //!ItemStack.areItemStacksEqual(currentRecipe.input, inputStack))) {

            this.currentRecipe = LiquifierRegistry.getRecipes(world).stream()
                    .filter(it -> it.input.toStacks().stream().anyMatch(stack -> ItemStack.areItemsEqual(stack, inputStack)))// ItemStack.areItemsEqual(it.input, inputStack))
                    .findFirst().orElse(null);
        }
        if (inputStack.isEmpty()) currentRecipe = null;
    }


    @Override
    public void tick() {
        if (!world.isRemote) {
            //this.energy.receiveEnergy(50, false);
            if (!this.getInput().getStackInSlot(0).isEmpty()) {
                updateRecipe();
                if (canProcess()) {
                    process();
                    this.markDirtyGUI();
                }
            }
        }
    }

    public boolean canProcess() {
        if (currentRecipe != null) {
            FluidStack recipeOutput = currentRecipe.output;
            return outputTank.getCapacity() >= outputTank.getFluidAmount() + recipeOutput.getAmount()
                    && this.energy.getEnergyStored() >= Config.LIQUIFIER_ENERGY_PER_TICK.get()
                    && getInput().getStackInSlot(0).getCount() >= currentRecipe.input.count
                    && ((outputTank.getFluid().getFluid() == recipeOutput.getFluid()) || outputTank.getFluid().isEmpty());
        } else return false;
    }

    public void process() {
        if (progressTicks < Config.LIQUIFIER_TICKS_PER_OPERATION.get()) {
            progressTicks++;
        } else {
            progressTicks = 0;
            outputTank.fill(currentRecipe.output.copy(), FluidAction.EXECUTE);//; .setOrIncrement(0, currentRecipe!!.output)
            getInput().getStackInSlot(0).shrink(currentRecipe.input.count);
        }
        this.energy.extractEnergy(Config.LIQUIFIER_ENERGY_PER_TICK.get(), false);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.outputTank.readFromNBT(compound.getCompound("outputTank"));
        updateRecipe();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("progressTicks", this.progressTicks);
        compound.put("outputTank", this.outputTank.writeToNBT(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

                if (LiquifierRegistry.getRecipes(world).stream().anyMatch(it -> it.input.toStacks().stream()
                        .anyMatch(ingStack -> ItemStack.areItemsEqual(ingStack, stack)))) {
                    //ItemStack.areItemsEqual(it.input, stack))) {
                    return super.isItemValid(slot, stack);
                } else return false;
            }
        };
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 0);
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity player) {
        return new LiquifierContainer(i, world, pos, playerInv, player);
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.LIQUIFIER_ENERGY_CAPACITY.get()) {
            @Override
            public void onEnergyChanged() {
                markDirtyGUI();
            }
        };
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public LazyOptional<IFluidHandler> getFluidHandler() {
        return fluidHolder;
    }

}
