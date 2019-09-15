package al132.alchemistry.blocks.fusion;

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
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
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
import static al132.alchemistry.blocks.fusion.FusionControllerBlock.STATUS;

public class FusionTile extends AlchemistryBaseTile implements EnergyTile {

    boolean isValidMultiblock = false;
    ItemStack recipeOutput = ItemStack.EMPTY;
    private int checkMultiblockTicks = 0;

    protected int progressTicks = 0;

    public FusionTile() {
        super(Ref.fusionTile);
    }


    @Override
    public void tick() {
        if (world.isRemote) return;
        checkMultiblockTicks++;
        if (checkMultiblockTicks >= 20) {
            updateMultiblock();
            checkMultiblockTicks = 0;
        }
        boolean isActive = !this.getInput().getStackInSlot(0).isEmpty() && !this.getInput().getStackInSlot(1).isEmpty();
        BlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() != Ref.fusionController) return;
        PowerStatus currentStatus = state.get(STATUS);
        if (this.isValidMultiblock) {
            if (isActive) {
                if (currentStatus != ON) this.world.setBlockState(this.pos, state.with(STATUS, ON));
            } else if (currentStatus != STANDBY) world.setBlockState(pos, state.with(STATUS, STANDBY));
        } else if (currentStatus != OFF) world.setBlockState(pos, state.with(STATUS, OFF));
        if (canProcess()) process();
        this.markDirtyClient();
    }

    public boolean canProcess() {
        ItemStack input0 = getInput().getStackInSlot(0);
        ItemStack input1 = getInput().getStackInSlot(1);
        ItemStack output0 = getOutput().getStackInSlot(0);
        return this.isValidMultiblock
                && !input0.isEmpty()
                && !input1.isEmpty()
                && !recipeOutput.isEmpty()
                && (ItemStack.areItemsEqual(output0, recipeOutput) || output0.isEmpty())
                && output0.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize()
                && energy.getEnergyStored() >= Config.FUSION_ENERGY_PER_TICK.get();
    }

    public void process() {
        if (progressTicks < Config.FUSION_TICKS_PER_OPERATION.get()) {
            progressTicks++;
        } else {
            progressTicks = 0;
            getOutput().setOrIncrement(0, recipeOutput.copy());
            getInput().decrementSlot(0, 1); //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
            getInput().decrementSlot(1, 1);//Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energy.extractEnergy(Config.FUSION_ENERGY_PER_TICK.get(), false);
    }

    public void refreshRecipe() {
        Item item0 = getInput().getStackInSlot(0).getItem();
        Item item1 = getInput().getStackInSlot(1).getItem();
        if (item0 instanceof ElementItem && item1 instanceof ElementItem) {
            int meta0 = ElementRegistry.elements.inverse().get(item0);
            int meta1 = ElementRegistry.elements.inverse().get(item1);//this.getInput().getStackInSlot(1).metadata;
            ElementItem outputElement = ElementRegistry.elements.get(meta0 + meta1);
            if (outputElement != null) recipeOutput = new ItemStack(outputElement);//outputElement.toItemStack(1);
            else recipeOutput = ItemStack.EMPTY;
        } else recipeOutput = ItemStack.EMPTY;
    }


    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.progressTicks = compound.getInt("progressTicks");
        this.refreshRecipe();
        this.updateMultiblock();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("progressTicks", progressTicks);
        return super.write(compound);
    }

    public void updateMultiblock() {
        this.isValidMultiblock = validateMultiblock();
    }

    private boolean containsCasing(BlockPos pos) {
        return this.world.getBlockState(pos).getBlock() == Ref.fusionCasing;
    }

    private boolean containsCore(BlockPos pos) {
        return this.world.getBlockState(pos).getBlock() == Ref.fusionCore;
    }

    private boolean containsFusionPart(BlockPos pos) {
        Block block = this.world.getBlockState(pos).getBlock();
        return block == Ref.fusionCasing || block == Ref.fusionCore || block == Ref.fusionController;
    }

    public boolean validateMultiblock() {
        Direction temp = world.getBlockState(this.pos).get(FusionControllerBlock.FACING);//.getOpposite();
        if (temp == null) return false;
        Direction multiblockDirection = temp.getOpposite();
        BiFunction<BlockPos, Integer, BlockPos> offsetUp = (BlockPos pos, Integer amt) -> pos.offset(Direction.UP, amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetLeft = (BlockPos pos, Integer amt) -> pos.offset(multiblockDirection.rotateY(), amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetRight = (BlockPos pos, Integer amt) -> pos.offset(multiblockDirection.rotateY(), -1 * amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetBack = (BlockPos pos, Integer amt) -> pos.offset(multiblockDirection, amt);
        BiFunction<BlockPos, Integer, BlockPos> offsetDown = (BlockPos pos, Integer amt) -> pos.offset(Direction.DOWN, amt);

        net.minecraft.util.math.BlockPos coreBottom = offsetBack.apply(this.pos, 3);
        coreBottom = offsetUp.apply(coreBottom, 1);
        BlockPos coreTop = offsetUp.apply(coreBottom, 2);
        boolean coreMatches = BlockPos.getAllInBox(coreBottom, coreTop).allMatch(this::containsCore);


        //A cube of all blocks surrounding the fusion multiblock, checking to ensure no other fusion multiblocks are overlapping/sharing
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
        }).filter(it -> !it.equals(this.pos)).filter(this::containsFusionPart).count();


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
        return new CustomStackHandler(this, 2) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof ElementItem;
            }

            @Override
            public void onContentsChanged(int slot) {
                ((FusionTile) tile).refreshRecipe();
                super.onContentsChanged(slot);
            }
        };
    }

    @Override
    public CustomStackHandler initOutput() {
        return new CustomStackHandler(this, 1) {
            @Nonnull
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInv, PlayerEntity player) {
        return new FusionContainer(i, world, pos, playerInv, player);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (this.isValidMultiblock) return super.getCapability(cap, side);
        else return LazyOptional.empty();
    }

    @Override
    public IEnergyStorage initEnergy() {
        return new CustomEnergyStorage(Config.FUSION_ENERGY_CAPACITY.get());
    }

    @Override
    public IEnergyStorage getEnergy() {
        return energy;
    }
}