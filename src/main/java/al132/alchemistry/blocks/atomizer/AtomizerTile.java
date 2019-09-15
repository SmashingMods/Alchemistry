package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.Config;
import al132.alchemistry.Ref;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alchemistry.recipes.AtomizerRecipe;
import al132.alchemistry.recipes.ModRecipes;
import al132.alib.tiles.CustomEnergyStorage;
import al132.alib.tiles.CustomStackHandler;
import al132.alib.tiles.EnergyTile;
import al132.alib.tiles.FluidTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AtomizerTile extends AlchemistryBaseTile implements EnergyTile, FluidTile {


    protected FluidTank inputTank;
    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> inputTank);
    private AtomizerRecipe currentRecipe = null;
    private int progressTicks = 0;

    public AtomizerTile() {
        super(Ref.atomizerTile);
        inputTank = new FluidTank(10000) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                updateRecipe();
                markDirtyGUI();
            }
        };
    }

    public void updateRecipe() {
        if (!inputTank.isEmpty()
                && (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe.output, getOutput().getStackInSlot(0)))) {
            currentRecipe = ModRecipes.atomizerRecipes.stream()
                    .filter(it -> it.input.getFluid() == inputTank.getFluid().getFluid()).findFirst().orElse(null);
        }
        if (inputTank.isEmpty()) currentRecipe = null;
    }


    @Override
    public void tick() {
        if (world.isRemote) return;
        //this.energy.receiveEnergy(50, false);
        if (inputTank.getFluidAmount() > 0) {
            updateRecipe();
            if (canProcess()) process();
        }
        markDirtyGUI();//(5);
    }

    public boolean canProcess() {
        if (currentRecipe != null) {
            ItemStack recipeOutput = currentRecipe.output;
            ItemStack output0 = getOutput().getStackInSlot(0);
            return energy.getEnergyStored() >= Config.LIQUIFIER_ENERGY_PER_TICK.get()
                    && inputTank.getFluidAmount() >= currentRecipe.input.getAmount()
                    && (ItemStack.areItemsEqual(output0, recipeOutput) || output0.isEmpty())
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
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.progressTicks = compound.getInt("progressTicks");
        inputTank.readFromNBT(compound.getCompound("inputTank"));
        updateRecipe();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("progressTicks", progressTicks);
        compound.put("inputTank", inputTank.writeToNBT(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 0);
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 1){
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity player) {
        return new AtomizerContainer(i, world, pos, playerInv, player);
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
}