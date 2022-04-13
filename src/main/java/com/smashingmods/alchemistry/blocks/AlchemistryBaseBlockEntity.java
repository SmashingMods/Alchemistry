package com.smashingmods.alchemistry.blocks;

import com.smashingmods.alchemistry.api.blockentity.BaseInventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.GuiBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.handler.AutomationStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

abstract public class AlchemistryBaseBlockEntity extends BaseInventoryBlockEntity implements GuiBlockEntity {

    private int dirtyTicks = 0;

    public AlchemistryBaseBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @SuppressWarnings("unused")
    public void markDirtyGUIEvery(int ticks) {
        this.dirtyTicks++;
        if (this.dirtyTicks >= ticks) {
            this.setChanged();
            this.dirtyTicks = 0;
        }
    }

    @Override
    public AutomationStackHandler initAutomationInputHandler(IItemHandlerModifiable input) {
        return new AutomationStackHandler(input) {
            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public AutomationStackHandler initAutomationOutputHandler(IItemHandlerModifiable output) {
        return new AutomationStackHandler(output) {
            @Override
            @Nonnull
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!getStackInSlot(slot).isEmpty()) return super.extractItem(slot, amount, simulate);
                else return ItemStack.EMPTY;
            }
        };
    }

    @Override
    public int getHeight() {
        return 222;
    }

    @Override
    public int getWidth() {
        return 174;
    }
}
