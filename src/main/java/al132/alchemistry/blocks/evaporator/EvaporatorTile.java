package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.Config;
import al132.alchemistry.Registration;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alib.tiles.CustomFluidTank;
import al132.alib.tiles.CustomStackHandler;
import al132.alib.tiles.FluidTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;

public class EvaporatorTile extends AlchemistryBaseTile implements FluidTile {

    public int progressTicks = 0;
    private int calculatedProcessingTime = 0;
    private EvaporatorRecipe currentRecipe = null;

    protected FluidTank fluidTank;
    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> fluidTank);

    public EvaporatorTile(BlockPos pos, BlockState state) {
        super(Registration.EVAPORATOR_BE.get(), pos, state);
        fluidTank = new CustomFluidTank(10000, FluidStack.EMPTY) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
            }
        };
    }

    public void tickServer() {
        if (level.isClientSide) return;
        if (fluidTank.getFluidAmount() > 0) {
            this.currentRecipe = EvaporatorRegistry.getRecipes(level).stream()
                    .filter(recipe -> fluidTank.getFluid().containsFluid(recipe.input)).findFirst().orElse(null);
        }
        if (canProcess()) {
            process();
        }
        notifyGUIEvery(5);
    }


    public boolean canProcess() {
        if (currentRecipe != null) {
            ItemStack recipeOutput = currentRecipe.output;
            return fluidTank.getFluidAmount() >= currentRecipe.input.getAmount()
                    && getOutput().getStackInSlot(0).getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
        } else return false;
    }

    public void process() {
        if (progressTicks % 5 == 0) calculatedProcessingTime = calculateProcessingTime();

        if (progressTicks < calculatedProcessingTime) progressTicks++;
        else {
            progressTicks = 0;
            fluidTank.drain(currentRecipe.input.getAmount(), IFluidHandler.FluidAction.EXECUTE);
            getOutput().setOrIncrement(0, currentRecipe.output.copy());

        }
        this.setChanged();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.fluidTank.readFromNBT(compound.getCompound("fluidTank"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("progressTicks", this.progressTicks);
        compound.put("fluidTank", fluidTank.writeToNBT(new CompoundTag()));
    }

    public int calculateProcessingTime() { //TODO more elaborate calculation?
        int temp = Config.EVAPORATOR_TICKS_PER_OPERATION.get();//ConfigHandler.evaporatorProcessingTicks!!
        //if (!BiomeDictionary.hasType(world.func_226691_t_(this.pos), BiomeDictionary.Type.DRY)) {
        //    temp += (Config.EVAPORATOR_TICKS_PER_OPERATION.get() * .5);
        //}
        return temp;
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
        return fluidHolder;
    }

    @Override
    public Component getName() {
        return null;
    }
}