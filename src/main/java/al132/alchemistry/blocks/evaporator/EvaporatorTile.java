package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.Config;
import al132.alchemistry.Ref;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alchemistry.recipes.EvaporatorRecipe;
import al132.alchemistry.recipes.ModRecipes;
import al132.alib.tiles.CustomStackHandler;
import al132.alib.tiles.FluidTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EvaporatorTile extends AlchemistryBaseTile implements FluidTile {

    public int progressTicks = 0;
    private int calculatedProcessingTime = 0;
    private EvaporatorRecipe currentRecipe = null;

    protected FluidTank fluidTank;
    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> fluidTank);

    public EvaporatorTile() {
        super(Ref.evaporatorTile);
        fluidTank = new FluidTank(10000, (stack) -> true) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                markDirtyClient();
            }
        };
    }

    @Override
    public void tick() {
        if (world.isRemote) return;
        if (fluidTank.getFluidAmount() > 0) {
            this.currentRecipe = ModRecipes.evaporatorRecipes.stream()
                    .filter(recipe -> fluidTank.getFluid().containsFluid(recipe.input)).findFirst().orElse(null);
        }
        if (canProcess()) process();
        markDirtyClient();//(5);
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
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.fluidTank.readFromNBT(compound.getCompound("fluidTank"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("progressTicks", this.progressTicks);
        compound.put("fluidTank", fluidTank.writeToNBT(new CompoundNBT()));
        return super.write(compound);
    }

    public int calculateProcessingTime() { //TODO more elaborate calculation?
        int temp = Config.EVAPORATOR_TICKS_PER_OPERATION.get();//ConfigHandler.evaporatorProcessingTicks!!
        if (!BiomeDictionary.hasType(world.getBiome(this.pos), BiomeDictionary.Type.DRY)) {
            temp += (Config.EVAPORATOR_TICKS_PER_OPERATION.get() * .5);
        }
        return temp;
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 0);
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 1){
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity player) {
        return new EvaporatorContainer(i, world, pos, playerInv, player);
    }

    @Override
    public LazyOptional<IFluidHandler> getFluidHandler() {
        return fluidHolder;
    }

}