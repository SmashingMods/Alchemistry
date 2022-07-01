package com.smashingmods.alchemistry.common.block.fusion;

import com.smashingmods.alchemistry.Config;
import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.EnergyBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.InventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomEnergyStorage;
import com.smashingmods.alchemistry.api.blockentity.handler.ModItemStackHandler;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.registry.BlockEntityRegistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FusionControllerBlockEntity extends AbstractAlchemistryBlockEntity implements InventoryBlockEntity, EnergyBlockEntity {

    protected final ContainerData data;

    private int progress = 0;
    private final int maxProgress = Config.Common.fusionTicksPerOperation.get();

    private final ModItemStackHandler inputHandler = initializeInputHandler();
    private final ModItemStackHandler outputHandler = initializeOutputHandler();

    private final AutomationStackHandler automationInputHandler = getAutomationInputHandler(getInputHandler());
    private final AutomationStackHandler automationOutputHandler = getAutomationOutputHandler(getOutputHandler());

    private final CombinedInvWrapper combinedInvWrapper = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> combinedInvWrapper);

    private final CustomEnergyStorage energyHandler = initializeEnergyStorage();
    private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> energyHandler);

    private FusionRecipe currentRecipe;

    public FusionControllerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityRegistry.FUSION_CONTROLLER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch(pIndex) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energyHandler.getEnergyStored();
                    case 3 -> energyHandler.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> progress = pValue;
                    case 2 -> energyHandler.setEnergy(pValue);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    public void tick(Level pLevel) {
        if (!pLevel.isClientSide()) {
            updateRecipe(pLevel);
            if (canProcessRecipe()) {
                processRecipe();
            } else {
                progress = 0;
            }
        }
    }

    private void updateRecipe(Level pLevel) {
        if (!inputHandler.getStackInSlot(0).isEmpty()) {
            List<FusionRecipe> recipes = RecipeRegistry.getRecipesByType(RecipeRegistry.FUSION_TYPE, pLevel).stream().toList();
            currentRecipe = recipes.stream().filter(recipe -> ItemStack.isSameItemSameTags(recipe.getInput1(), inputHandler.getStackInSlot(0)) && ItemStack.isSameItemSameTags(recipe.getInput2(), inputHandler.getStackInSlot(1))).findFirst().orElse(null);
        } else {
            currentRecipe = null;
        }
    }

    private boolean canProcessRecipe() {
        if (currentRecipe != null && isValidMultiblock()) {
            ItemStack input1 = inputHandler.getStackInSlot(0);
            ItemStack input2 = inputHandler.getStackInSlot(1);
            ItemStack output = outputHandler.getStackInSlot(0);
            return energyHandler.getEnergyStored() >= Config.Common.fissionEnergyPerTick.get()
                    && (ItemStack.isSameItemSameTags(input1, currentRecipe.getInput1()) && input1.getCount() >= currentRecipe.getInput1().getCount())
                    && (ItemStack.isSameItemSameTags(input2, currentRecipe.getInput2()) && input2.getCount() >= currentRecipe.getInput2().getCount())
                    && ((ItemStack.isSameItemSameTags(output, currentRecipe.getOutput()) || output.isEmpty()) && (currentRecipe.getOutput().getCount() + output.getCount()) <= currentRecipe.getOutput().getMaxStackSize());
        }
        return false;
    }

    private void processRecipe() {
        if (progress < maxProgress) {
            progress++;
        } else {
            progress = 0;
            inputHandler.decrementSlot(0, currentRecipe.getInput1().getCount());
            inputHandler.decrementSlot(1, currentRecipe.getInput2().getCount());
            outputHandler.setOrIncrement(0, currentRecipe.getOutput().copy());
        }
        energyHandler.extractEnergy(Config.Common.fusionEnergyPerTick.get(), false);
        setChanged();
    }

    private boolean isValidMultiblock() {
        if (level != null && !level.isClientSide()) {
            BlockPos controller = this.getBlockPos();
            Direction frontFacing = level.getBlockState(controller).getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction oppositeFacing = frontFacing.getOpposite();
            Direction leftFacing = frontFacing.getCounterClockWise();
            Direction rightFacing = frontFacing.getClockWise();

            BlockPos coreBottom = controller.relative(oppositeFacing, 2);
            BlockPos coreTop = coreBottom.relative(Direction.UP, 2);
            BlockPos frontTopCW = controller.relative(Direction.UP, 3).relative(leftFacing, 2);
            BlockPos frontTopCCW = controller.relative(Direction.UP, 3).relative(rightFacing, 2);
            BlockPos frontBottomCW = controller.relative(Direction.DOWN, 1).relative(leftFacing, 2);
            BlockPos frontBottomCCW = controller.relative(Direction.DOWN, 1).relative(rightFacing, 2);
            BlockPos rearTopCW = controller.relative(Direction.UP, 3).relative(oppositeFacing, 4).relative(leftFacing, 2);
            BlockPos rearTopCCW = controller.relative(Direction.UP, 3).relative(oppositeFacing, 4).relative(rightFacing, 2);
            BlockPos rearBottomCW = controller.relative(Direction.DOWN, 1).relative(oppositeFacing, 4).relative(leftFacing, 2);
            BlockPos rearBottomCCW = controller.relative(Direction.DOWN, 1).relative(oppositeFacing, 4).relative(rightFacing, 2);

            Function<BlockPos, Boolean> containsCasing = blockPos -> level.getBlockState(blockPos).getBlock() == BlockRegistry.REACTOR_CASING.get();
            Function<BlockPos, Boolean> containsCore = blockPos -> level.getBlockState(blockPos).getBlock() == BlockRegistry.FUSION_CORE.get();
            Function<BlockPos, Boolean> containsController = blockPos -> level.getBlockState(blockPos).getBlock() == BlockRegistry.FUSION_CONTROLLER.get();
            Function<BlockPos, Boolean> validateFrontPlane = blockPos -> containsCasing.apply(blockPos) || containsController.apply(blockPos);
            Function<BlockPos, Vec3i> blockPosToVec3i = blockPos -> new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            BoundingBox core = BoundingBox.fromCorners(blockPosToVec3i.apply(coreBottom), blockPosToVec3i.apply(coreTop));
            BoundingBox frontPlane = BoundingBox.fromCorners(blockPosToVec3i.apply(frontBottomCW), blockPosToVec3i.apply(frontTopCCW));
            BoundingBox rearPlane = BoundingBox.fromCorners(blockPosToVec3i.apply(rearBottomCW), blockPosToVec3i.apply(rearTopCCW));
            BoundingBox topPlane = BoundingBox.fromCorners(blockPosToVec3i.apply(frontTopCW), blockPosToVec3i.apply(rearTopCCW));
            BoundingBox bottomPlane = BoundingBox.fromCorners(blockPosToVec3i.apply(frontBottomCW), blockPosToVec3i.apply(rearBottomCCW));
            BoundingBox leftPlane = BoundingBox.fromCorners(blockPosToVec3i.apply(frontBottomCCW), blockPosToVec3i.apply(rearTopCCW));
            BoundingBox rightPlane = BoundingBox.fromCorners(blockPosToVec3i.apply(frontBottomCW), blockPosToVec3i.apply(rearTopCW));

            boolean coreMatches = BlockPos.betweenClosedStream(core).allMatch(containsCore::apply);
            boolean frontMatches = BlockPos.betweenClosedStream(frontPlane).allMatch(validateFrontPlane::apply);
            boolean rearMatches = BlockPos.betweenClosedStream(rearPlane).allMatch(containsCasing::apply);
            boolean topMatches = BlockPos.betweenClosedStream(topPlane).allMatch(containsCasing::apply);
            boolean bottomMatches = BlockPos.betweenClosedStream(bottomPlane).allMatch(containsCasing::apply);
            boolean leftMatches = BlockPos.betweenClosedStream(leftPlane).allMatch(containsCasing::apply);
            boolean rightMatches = BlockPos.betweenClosedStream(rightPlane).allMatch(containsCasing::apply);

            return coreMatches && frontMatches && rearMatches && topMatches && bottomMatches && leftMatches && rightMatches;
        }
        return false;
    }

    @Override
    public CustomEnergyStorage initializeEnergyStorage() {
        return new CustomEnergyStorage(Config.Common.fusionEnergyCapacity.get()) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public ModItemStackHandler initializeInputHandler() {
        return new ModItemStackHandler(this, 2);
    }

    @Override
    public ModItemStackHandler initializeOutputHandler() {
        return new ModItemStackHandler(this, 1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    @Override
    public ModItemStackHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public ModItemStackHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public AutomationStackHandler getAutomationInputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
            @Override
            @Nonnull
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler) {
        return new AutomationStackHandler(pHandler) {
            @Override
            @Nonnull
            public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
                if (!getStackInSlot(pSlot).isEmpty()) {
                    return super.extractItem(pSlot, pAmount, pSimulate);
                } else {
                    return ItemStack.EMPTY;
                }
            }
        };
    }

    @Override
    public CombinedInvWrapper getAutomationInventory() {
        return combinedInvWrapper;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction pDirection) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, pDirection);
    }

    @Override
    public void invalidateCaps() {
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("progress", progress);
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.put("energy", energyHandler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("progress");
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        energyHandler.deserializeNBT(pTag.get("energy"));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new FusionControllerMenu(pContainerId, pInventory, this, this.data);
    }
}
