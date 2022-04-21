//package com.smashingmods.alchemistry.common.block.oldblocks.evaporator;
//
//import com.smashingmods.alchemistry.Config;
//import com.smashingmods.alchemistry.common.block.AlchemistryBlockEntity;
//import com.smashingmods.alchemistry.api.blockentity.handler.CustomFluidStorage;
//import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
//import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
//import com.smashingmods.alchemistry.common.block.oldblocks.blockentity.FluidBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import net.minecraftforge.fluids.capability.templates.FluidTank;
//import org.jetbrains.annotations.NotNull;
//
//import javax.annotation.Nonnull;
//
//public class EvaporatorBlockEntity extends AlchemistryBlockEntity implements FluidBlockEntity {
//
//    public int progressTicks = 0;
//    private int calculatedProcessingTime = 0;
//    private EvaporatorRecipe currentRecipe = null;
//
//    protected FluidTank fluidTank;
//    protected LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> fluidTank);
//
//    public EvaporatorBlockEntity(BlockPos pos, BlockState state) {
//        super(BlockEntityRegistry.EVAPORATOR_BE.get(), pos, state);
//        fluidTank = new CustomFluidStorage(10000, FluidStack.EMPTY) {
//            @Override
//            protected void onContentsChanged() {
//                super.onContentsChanged();
//            }
//        };
//    }
//
//    public void tickServer() {
//        if (level.isClientSide) return;
//        if (fluidTank.getFluidAmount() > 0) {
//            this.currentRecipe = EvaporatorRegistry.getRecipes(level).stream()
//                    .filter(recipe -> fluidTank.getFluid().containsFluid(recipe.input)).findFirst().orElse(null);
//        }
//        if (canProcess()) {
//            process();
//        }
//        updateGUIEvery(5);
//    }
//
//
//    public boolean canProcess() {
//        if (currentRecipe != null) {
//            ItemStack recipeOutput = currentRecipe.output;
//            return fluidTank.getFluidAmount() >= currentRecipe.input.getAmount()
//                    && getOutputHandler().getStackInSlot(0).getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
//        } else return false;
//    }
//
//    public void process() {
//        if (progressTicks % 5 == 0) calculatedProcessingTime = calculateProcessingTime();
//
//        if (progressTicks < calculatedProcessingTime) progressTicks++;
//        else {
//            progressTicks = 0;
//            fluidTank.drain(currentRecipe.input.getAmount(), IFluidHandler.FluidAction.EXECUTE);
//            getOutputHandler().setOrIncrement(0, currentRecipe.output.copy());
//
//        }
//        this.setChanged();
//    }
//
//    @Override
//    public void load(@NotNull CompoundTag compound) {
//        super.load(compound);
//        this.progressTicks = compound.getInt("progressTicks");
//        this.fluidTank.readFromNBT(compound.getCompound("fluidTank"));
//    }
//
//    @Override
//    public void saveAdditional(@NotNull CompoundTag compound) {
//        super.saveAdditional(compound);
//        compound.putInt("progressTicks", this.progressTicks);
//        compound.put("fluidTank", fluidTank.writeToNBT(new CompoundTag()));
//    }
//
//    public int calculateProcessingTime() { //TODO more elaborate calculation?
//        int temp = Config.EVAPORATOR_TICKS_PER_OPERATION.get();//ConfigHandler.evaporatorProcessingTicks!!
//        //if (!BiomeDictionary.hasType(world.func_226691_t_(this.pos), BiomeDictionary.Type.DRY)) {
//        //    temp += (Config.EVAPORATOR_TICKS_PER_OPERATION.get() * .5);
//        //}
//        return temp;
//    }
//
//    @Override
//    public CustomStackHandler getInputHandler() {
//        if (inputHandler == null) {
//            inputHandler = new CustomStackHandler(this, 0);
//        }
//        return inputHandler;
//    }
//
//    @Override
//    public CustomStackHandler getOutputHandler() {
//        if (outputHandler == null) {
//            outputHandler = new CustomStackHandler(this, 1) {
//                @Override
//                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
//                    return false;
//                }
//            };
//        }
//        return outputHandler;
//    }
//
//    @Override
//    public LazyOptional<IFluidHandler> getFluidHandler() {
//        return fluidHolder;
//    }
//}