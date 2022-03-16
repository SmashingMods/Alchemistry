package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.Config;
import al132.alchemistry.Registration;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alib.tiles.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

public class LiquifierTile extends AlchemistryBaseTile implements EnergyTile, FluidTile {

    public FluidTank outputTank;
    private LiquifierRecipe currentRecipe = null;
    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> outputTank);

    public int progressTicks = 0;

    public LiquifierTile(BlockPos pos, BlockState state) {
        super(Registration.LIQUIFIER_BE.get(), pos, state);
        outputTank = new CustomFluidTank(10000, FluidStack.EMPTY);
    }

    private void updateRecipe() {
        ItemStack inputStack = this.getInput().getStackInSlot(0);
        if (!inputStack.isEmpty() && (currentRecipe == null || currentRecipe.input.toStacks().stream().anyMatch(x -> ItemStack.isSame(x, inputStack)))) {
            //!ItemStack.isSame(currentRecipe.input, inputStack))) {

            this.currentRecipe = LiquifierRegistry.getRecipes(level).stream()
                    .filter(it -> it.input.toStacks().stream().anyMatch(stack -> ItemStack.isSame(stack, inputStack)))// ItemStack.isSame(it.input, inputStack))
                    .findFirst().orElse(null);
        }
        if (inputStack.isEmpty()) currentRecipe = null;
    }


    public void tickServer() {
        if (level.isClientSide) return;
        if (!this.getInput().getStackInSlot(0).isEmpty()) {
            updateRecipe();
            if (canProcess()) {
                process();
            }
        }
        this.notifyGUIEvery(5);
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
        setChanged();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.outputTank.readFromNBT(compound.getCompound("outputTank"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("progressTicks", this.progressTicks);
        compound.put("outputTank", this.outputTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {

                if (LiquifierRegistry.getRecipes(level).stream().anyMatch(it -> it.input.toStacks().stream()
                        .anyMatch(ingStack -> ItemStack.isSame(ingStack, stack)))) {
                    //ItemStack.isSame(it.input, stack))) {
                    return super.isItemValid(slot, stack);
                } else return false;
            }
        };
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 0);
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.LIQUIFIER_ENERGY_CAPACITY.get());
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public LazyOptional<IFluidHandler> getFluidHandler() {
        return fluidHolder;
    }

    @Override
    public Component getName() {
        return null;
    }
}
