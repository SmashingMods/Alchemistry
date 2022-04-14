package com.smashingmods.alchemistry.api.blockentity.handler;

import com.smashingmods.alchemistry.blocks.AlchemistryBlockEntity;
import com.smashingmods.alchemistry.utils.IItemHandlerUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Objects;
import java.util.stream.IntStream;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class CustomStackHandler extends ItemStackHandler {

    public AlchemistryBlockEntity baseBlockEntity;

    public CustomStackHandler(AlchemistryBlockEntity pBaseBlockEntity, int pSize) {
        super(pSize);
        this.baseBlockEntity = pBaseBlockEntity;
    }

    @Override
    public void onContentsChanged(int pSlot) {
        super.onContentsChanged(pSlot);
        this.baseBlockEntity.setChanged();
    }

    @SuppressWarnings("unused")
    public void clear() {
        IntStream.range(0, this.getSlots()).forEach(slot -> setStackInSlot(slot, ItemStack.EMPTY));
    }

    public void incrementSlot(int pSlot, int pAmount) {
        ItemStack temp = this.getStackInSlot(pSlot);

        if (temp.getCount() + pAmount <= temp.getMaxStackSize()) {
            temp.setCount(temp.getCount() + pAmount);
        }

        this.setStackInSlot(pSlot, temp);
    }

    @SuppressWarnings("unused")
    public void incrementSlot(int pSlot) {
        incrementSlot(pSlot, 1);
    }

    @SuppressWarnings("unused")
    public void setOrIncrement(int slot, ItemStack stackToSet) {
        if (!stackToSet.isEmpty()) {
            if (this.getStackInSlot(slot).isEmpty()) this.setStackInSlot(slot, stackToSet);
            else this.incrementSlot(slot, stackToSet.getCount());
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
    public void decrementSlot(int pSlot) {
        decrementSlot(pSlot, 1);
    }

    @SuppressWarnings("unused")
    public boolean eject(Direction direction) {

        BlockEntity targetBlockEntity = Objects.requireNonNull(baseBlockEntity.getLevel()).getBlockEntity(baseBlockEntity.getBlockPos().offset(direction.getNormal()));
        LazyOptional<IItemHandler> originHandler = this.baseBlockEntity.getCapability(ITEM_HANDLER_CAPABILITY, direction);
        LazyOptional<IItemHandler> targetHandler = Objects.requireNonNull(targetBlockEntity).getCapability(ITEM_HANDLER_CAPABILITY, direction.getOpposite());

        if (originHandler.isPresent() && targetHandler.isPresent()) {
            return IItemHandlerUtils.tryInsertInto(originHandler.orElseThrow(NullPointerException::new), targetHandler.orElseThrow(NullPointerException::new));
        } else {
            return false;
        }
    }
}
