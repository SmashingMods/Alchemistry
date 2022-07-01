package com.smashingmods.alchemistry.api.blockentity.handler;

import com.smashingmods.alchemistry.api.blockentity.AbstractAlchemistryBlockEntity;
import com.smashingmods.alchemistry.utils.IItemHandlerUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class ModItemStackHandler extends ItemStackHandler {

    public AbstractAlchemistryBlockEntity blockEntity;

    public ModItemStackHandler(AbstractAlchemistryBlockEntity pBlockEntity, int pSize) {
        super(pSize);
        this.blockEntity = pBlockEntity;
    }

    public void incrementSlot(int pSlot, int pAmount) {
        ItemStack temp = this.getStackInSlot(pSlot);

        if (temp.getCount() + pAmount <= temp.getMaxStackSize()) {
            temp.setCount(temp.getCount() + pAmount);
        }

        this.setStackInSlot(pSlot, temp);
    }

    public void setOrIncrement(int slot, ItemStack stackToSet) {
        if (!stackToSet.isEmpty()) {
            if (getStackInSlot(slot).isEmpty()) {
                setStackInSlot(slot, stackToSet);
            } else {
                incrementSlot(slot, stackToSet.getCount());
            }
        }
    }

    public void decrementSlot(int pSlot, int pAmount) {
        ItemStack temp = this.getStackInSlot(pSlot);

        if (temp.isEmpty()) return;
        if (temp.getCount() - pAmount < 0) return;

        temp.shrink(pAmount);
        if (temp.getCount() <= 0) {
            this.setStackInSlot(pSlot, ItemStack.EMPTY);
        } else {
            this.setStackInSlot(pSlot, temp);
        }
    }

    @SuppressWarnings("unused")
    public boolean eject(Direction direction) {

        BlockEntity targetBlockEntity = Objects.requireNonNull(blockEntity.getLevel()).getBlockEntity(blockEntity.getBlockPos().offset(direction.getNormal()));
        LazyOptional<IItemHandler> originHandler = this.blockEntity.getCapability(ITEM_HANDLER_CAPABILITY, direction);
        LazyOptional<IItemHandler> targetHandler = Objects.requireNonNull(targetBlockEntity).getCapability(ITEM_HANDLER_CAPABILITY, direction.getOpposite());

        if (originHandler.isPresent() && targetHandler.isPresent()) {
            return IItemHandlerUtils.tryInsertInto(originHandler.orElseThrow(NullPointerException::new), targetHandler.orElseThrow(NullPointerException::new));
        } else {
            return false;
        }
    }

    public List<ItemStack> getSlotsAsList() {
        List<ItemStack> toReturn = new ArrayList<>();
        for (int index = 0; index < getSlots(); index++) {
            toReturn.add(getStackInSlot(index));
        }
        return toReturn;
    }
}
