package com.smashingmods.alchemistry.api.blockentity.handler;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

public class CustomItemStackHandler extends ItemStackHandler {

    public CustomItemStackHandler(int pSize) {
        super(pSize);
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

    public void emptyToInventory(Inventory pInventory) {
        for (int i = 0; i < this.stacks.size(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                ItemHandlerHelper.insertItemStacked(new PlayerInvWrapper(pInventory), getStackInSlot(i), false);
                setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    public boolean isEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }
}
