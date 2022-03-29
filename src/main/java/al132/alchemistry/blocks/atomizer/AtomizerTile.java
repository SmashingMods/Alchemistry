package al132.alchemistry.blocks.atomizer;

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

import javax.annotation.Nonnull;

public class AtomizerTile extends AlchemistryBaseTile implements EnergyTile, FluidTile {


    protected CustomFluidTank inputTank;
    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> inputTank);
    private AtomizerRecipe currentRecipe = null;
    protected int progressTicks = 0;

    public AtomizerTile(BlockPos pos, BlockState state) {
        super(Registration.ATOMIZER_BE.get(), pos, state);
        inputTank = new CustomFluidTank(10000, FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                updateRecipe();
                setChanged();
            }
        };
    }

    public void updateRecipe() {
        if (!inputTank.isEmpty()
                && (currentRecipe == null || !ItemStack.matches(currentRecipe.output, getOutput().getStackInSlot(0)))) {
            currentRecipe = AtomizerRegistry.getRecipes(level).stream()
                    .filter(it -> it.input.getFluid() == inputTank.getFluid().getFluid()).findFirst().orElse(null);
        }
        if (inputTank.isEmpty()) currentRecipe = null;
        setChanged();
    }

    public void tickServer() {
        if (level.isClientSide) return;
        if (inputTank.getFluidAmount() > 0) {
            updateRecipe();
            if (canProcess()) {
                process();
            }
        }
        updateGUIEvery(5);
    }

    public boolean canProcess() {
        if (currentRecipe != null) {
            ItemStack recipeOutput = currentRecipe.output;
            ItemStack output0 = getOutput().getStackInSlot(0);
            return energy.getEnergyStored() >= Config.LIQUIFIER_ENERGY_PER_TICK.get()
                    && inputTank.getFluidAmount() >= currentRecipe.input.getAmount()
                    && (ItemStack.matches(output0, recipeOutput) || output0.isEmpty())
                    && output0.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
        } else return false;
    }

    public void process() {
        if (progressTicks < Config.LIQUIFIER_TICKS_PER_OPERATION.get()) {
            progressTicks++;
        } else {
            progressTicks = 0;
            getOutput().setOrIncrement(0, currentRecipe.output.copy());
            inputTank.drain(currentRecipe.input.getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }
        this.energy.extractEnergy(Config.LIQUIFIER_ENERGY_PER_TICK.get(), false);
        this.setChanged();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.progressTicks = compound.getInt("progressTicks");
        inputTank.readFromNBT(compound.getCompound("inputTank"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("progressTicks", progressTicks);
        compound.put("inputTank", inputTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 0);
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
    public LazyOptional<IFluidHandler> getFluidHandler() {
        return this.fluidHolder;
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.ATOMIZER_ENERGY_CAPACITY.get());
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