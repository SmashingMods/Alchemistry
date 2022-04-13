package com.smashingmods.alchemistry.api.blockentity.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class AutomationStackHandler implements IItemHandlerModifiable {

    private final IItemHandlerModifiable HANDLER;

    public AutomationStackHandler(IItemHandlerModifiable pHandler) {
        this.HANDLER = pHandler;
    }

    public int getSlots() {
        return HANDLER.getSlots();
    }

    @Nonnull
    public ItemStack getStackInSlot(int pSlot) {
        return HANDLER.getStackInSlot(pSlot);
    }

    public void setStackInSlot(int pSlot, @Nonnull ItemStack pItemStack) {
        HANDLER.setStackInSlot(pSlot, pItemStack);
    }

    public int getSlotLimit(int pSlot) {
        return HANDLER.getSlotLimit(pSlot);
    }

    @Override
    public boolean isItemValid(int pSlot, @Nonnull ItemStack pItemStack) {
        return true;
    }

    @Nonnull
    public ItemStack insertItem(int pSlot, @Nonnull ItemStack pItemStack, boolean pSimulate) {
        return HANDLER.insertItem(pSlot, pItemStack, pSimulate);
    }

    @Nonnull
    public ItemStack extractItem(int pSlot, int pAmount, boolean pSimulate) {
        return HANDLER.extractItem(pSlot, pAmount, pSimulate);
    }
}