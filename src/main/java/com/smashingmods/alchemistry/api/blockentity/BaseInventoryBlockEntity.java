package com.smashingmods.alchemistry.api.blockentity;

import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import com.smashingmods.alchemistry.api.blockentity.handler.CustomStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public abstract class BaseInventoryBlockEntity extends BaseBlockEntity implements InventoryBlockEntity {

    private final CustomStackHandler inputHandler;
    private final CustomStackHandler outputHandler;
    private CombinedInvWrapper combinedInv;
    protected LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> combinedInv);

    public BaseInventoryBlockEntity(BlockEntityType<?> pBlockEntityType, BlockPos pPos, BlockState pBlockState) {
        super(pBlockEntityType, pPos, pBlockState);
        this.inputHandler = this.initInputHandler();
        this.outputHandler = this.initOutputHandler();
        AutomationStackHandler automationInputHandler = this.initAutomationInputHandler(inputHandler);
        AutomationStackHandler automationOutputHandler = this.initAutomationOutputHandler(outputHandler);
        combinedInv = new CombinedInvWrapper(automationInputHandler, automationOutputHandler);
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
    }

    @Override
    public LazyOptional<IItemHandler> getExternalInventory() {
        return inventoryHolder;
    }

    @Override
    public CombinedInvWrapper getAutomationInventory() {
        return combinedInv;
    }

    @Override
    public CustomStackHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public CustomStackHandler getOutputHandler() {
        return outputHandler;
    }
}