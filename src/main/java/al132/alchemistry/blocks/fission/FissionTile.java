package al132.alchemistry.blocks.fission;

import al132.alchemistry.Config;
import al132.alchemistry.Ref;
import al132.alchemistry.blocks.AlchemistryBaseTile;
import al132.alchemistry.blocks.PowerStatus;
import al132.alib.tiles.CustomEnergyStorage;
import al132.alib.tiles.CustomStackHandler;
import al132.alib.tiles.EnergyTile;
import al132.chemlib.chemistry.ElementRegistry;
import al132.chemlib.items.ElementItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

import static al132.alchemistry.blocks.PowerStatus.*;
import static al132.alchemistry.blocks.fission.FissionControllerBlock.STATUS;

public class FissionTile extends AlchemistryBaseTile implements EnergyTile {
    public FissionTile() {
        super(Ref.fissionTile);
    }

    int progressTicks = 0;
    ItemStack recipeOutput1 = ItemStack.EMPTY;
    ItemStack recipeOutput2 = ItemStack.EMPTY;
    boolean isValidMultiblock = false;
    int checkMultiblockTicks = 0;
    boolean firstTick = true;

    public ItemStack getInputStack() {
        return this.getInput().getStackInSlot(0);
    }


    private void refreshRecipe() {
        if (!this.getInputStack().isEmpty() && (this.getInputStack().getItem() instanceof ElementItem)) {
            int atomicNumber = ((ElementItem) this.getInputStack().getItem()).atomicNumber;
            int halfAtomNum = atomicNumber / 2;
            if (atomicNumber != 0) {
                if (atomicNumber % 2 == 0) {
                    if (ElementRegistry.elements.containsKey(halfAtomNum) && ElementRegistry.elements.get(halfAtomNum) != null) {
                        recipeOutput1 = new ItemStack(ElementRegistry.elements.get(halfAtomNum), 2);//(quantity = 2, meta = meta / 2);
                        recipeOutput2 = ItemStack.EMPTY;
                    }
                } else {
                    if (ElementRegistry.elements.containsKey(halfAtomNum) && ElementRegistry.elements.containsKey((halfAtomNum) + 1)) {
                        recipeOutput1 = new ItemStack(ElementRegistry.elements.get((halfAtomNum) + 1));//.elements.toStack(meta = (meta / 2) + 1).
                        recipeOutput2 = new ItemStack(ElementRegistry.elements.get(halfAtomNum));
                    }
                }
            }
        } else {
            recipeOutput1 = ItemStack.EMPTY;
            recipeOutput2 = ItemStack.EMPTY;
        }
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        if (firstTick) {
            refreshRecipe();
            firstTick = false;
        }

        boolean isActive = !this.getInputStack().isEmpty();
        checkMultiblockTicks++;
        if (checkMultiblockTicks >= 20) {
            updateMultiblock();
            checkMultiblockTicks = 0;
        }
        BlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() != Ref.fissionController) return;
        PowerStatus currentStatus = state.get(STATUS);
        if (this.isValidMultiblock) {
            if (isActive) {
                if (currentStatus != ON) this.world.setBlockState(this.pos, state.with(STATUS, ON));
            } else if (currentStatus != STANDBY) world.setBlockState(pos, state.with(STATUS, STANDBY));
        } else if (currentStatus != OFF) world.setBlockState(pos, state.with(STATUS, OFF));

        if (canProcess()) {
            process();
            this.notifyGUIEvery(5);
        }
    }

    public boolean canProcess() {
        ItemStack output0 = getOutput().getStackInSlot(0);
        ItemStack output1 = getOutput().getStackInSlot(1);
        return this.isValidMultiblock
                && !recipeOutput1.isEmpty()
                && (ItemStack.areItemsEqual(output0, recipeOutput1) || output0.isEmpty())
                && (ItemStack.areItemsEqual(output1, recipeOutput2) || output1.isEmpty())
                && output0.getCount() + recipeOutput1.getCount() <= recipeOutput1.getMaxStackSize()
                && output1.getCount() + recipeOutput2.getCount() <= recipeOutput2.getMaxStackSize()
                && energy.getEnergyStored() >= Config.FISSION_ENERGY_PER_TICK.get();
    }

    public void process() {
        if (progressTicks < Config.FISSION_TICKS_PER_OPERATION.get()) {
            progressTicks++;
        } else {
            progressTicks = 0;
            getOutput().setOrIncrement(0, recipeOutput1.copy());
            if (!recipeOutput2.isEmpty()) getOutput().setOrIncrement(1, recipeOutput2.copy());
            getInput().decrementSlot(0, 1); //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energy.extractEnergy(Config.FISSION_ENERGY_PER_TICK.get(), false);//ConfigHandler.fissionEnergyPerTick!!, false)
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.isValidMultiblock = compound.getBoolean("isValidMultiblock");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("progressTicks", progressTicks);
        compound.putBoolean("isValidMultiblock", isValidMultiblock);
        return super.write(compound);
    }

    public void updateMultiblock() {
        this.isValidMultiblock = validateMultiblock();
    }

    private boolean containsCasing(BlockPos pos) {
        return this.world.getBlockState(pos).getBlock() == Ref.fissionCasing;
    }

    private boolean containsCore(BlockPos pos) {
        return this.world.getBlockState(pos).getBlock() == Ref.fissionCore;
    }

    private boolean containsFissionPart(BlockPos pos) {
        Block block = this.world.getBlockState(pos).getBlock();
        return block == Ref.fissionCasing || block == Ref.fissionCore || block == Ref.fissionController;
    }

    public boolean validateMultiblock() {
        Direction multiblockDirection = world.getBlockState(this.pos).get(FissionControllerBlock.FACING).getOpposite();
        if (multiblockDirection == null) return false;
        BiFunction<BlockPos, Integer, BlockPos> offsetUp = (BlockPos pos, Integer amt) -> pos.offset(Direction.UP, amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetLeft = (BlockPos pos, Integer amt) -> pos.offset(multiblockDirection.rotateY(), amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetRight = (BlockPos pos, Integer amt) -> pos.offset(multiblockDirection.rotateY(), -1 * amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetBack = (BlockPos pos, Integer amt) -> pos.offset(multiblockDirection, amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetDown = (BlockPos pos, Integer amt) -> pos.offset(Direction.DOWN, amt);

        net.minecraft.util.math.BlockPos coreBottom = offsetBack.apply(this.pos, 3);
        coreBottom = offsetUp.apply(coreBottom, 1);
        BlockPos coreTop = offsetUp.apply(coreBottom, 2);
        boolean coreMatches = BlockPos.getAllInBox(coreBottom, coreTop).allMatch(this::containsCore);


        //A cube of all blocks surrounding the fission multiblock, checking to ensure no other fission multiblocks are overlapping/sharing
        BlockPos outsideCorner1 = offsetLeft.apply(this.pos, 3);
        outsideCorner1 = offsetDown.apply(outsideCorner1, 1);
        final BlockPos outsideCorner1Final = outsideCorner1; //java doesn't like non-final fields in the lambda below..
        BlockPos outsideCorner2 = offsetRight.apply(outsideCorner1, 6);
        outsideCorner2 = offsetUp.apply(outsideCorner2, 6);
        outsideCorner2 = offsetBack.apply(outsideCorner2, 6);

        long borderingParts = BlockPos.getAllInBox(outsideCorner1, outsideCorner2).filter(it -> {
            int sharedAxes = 0;
            if (it.getX() == outsideCorner1Final.getX() || it.getX() == outsideCorner1Final.getX()) sharedAxes++;
            if (it.getY() == outsideCorner1Final.getY() || it.getY() == outsideCorner1Final.getY()) sharedAxes++;
            if (it.getZ() == outsideCorner1Final.getZ() || it.getZ() == outsideCorner1Final.getZ()) sharedAxes++;
            return sharedAxes >= 1;
        }).filter(it -> !it.equals(this.pos)).filter(this::containsFissionPart).count();
        //.count(this::containsFissionPart);


        BlockPos casingCorner1 = offsetLeft.apply(this.pos, 2);
        casingCorner1 = offsetBack.apply(casingCorner1, 1);
        final BlockPos casingCorner1Final = casingCorner1;
        BlockPos casingCorner2 = offsetRight.apply(casingCorner1, 4);
        casingCorner2 = offsetBack.apply(casingCorner2, 4);
        casingCorner2 = offsetUp.apply(casingCorner2, 4);
        final BlockPos casingCorner2Final = casingCorner2;

        boolean casingMatches = BlockPos.getAllInBox(casingCorner1, casingCorner2).filter(it -> {
            int sharedAxes = 0;
            if (it.getX() == casingCorner1Final.getX() || it.getX() == casingCorner2Final.getX()) sharedAxes++;
            if (it.getY() == casingCorner1Final.getY() || it.getY() == casingCorner2Final.getY()) sharedAxes++;
            if (it.getZ() == casingCorner1Final.getZ() || it.getZ() == casingCorner2Final.getZ()) sharedAxes++;
            return sharedAxes >= 1;
        }).allMatch(this::containsCasing);

        return casingMatches && coreMatches && (borderingParts == 0);
    }

    @Override
    public CustomStackHandler initInput() {
        return new CustomStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof ElementItem;
            }

            @Override
            public void onContentsChanged(int slot) {
                ((FissionTile) this.tile).refreshRecipe();
                super.onContentsChanged(slot);
            }
        };
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 2) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity player) {
        return new FissionContainer(i, world, pos, playerInv, player);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.isValidMultiblock) return super.getCapability(cap, side);
        else return LazyOptional.empty();
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.FISSION_ENERGY_CAPACITY.get()) {
            @Override
            public void onEnergyChanged() {
                notifyGUIEvery(5);
            }
        };
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }
}