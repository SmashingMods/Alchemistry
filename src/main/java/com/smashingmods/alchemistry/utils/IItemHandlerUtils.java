package com.smashingmods.alchemistry.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;


public class IItemHandlerUtils {
    public static boolean tryInsertInto(IItemHandler self, IItemHandler other) {
        for (int i = 0; i < other.getSlots(); i++) {
            for (int j = 0; j < self.getSlots(); j++) {
                if (!self.getStackInSlot(j).isEmpty()) {
                    int stackSize = IItemHandlerUtils.countSlot(self, j);
                    if (other.insertItem(i, self.extractItem(j, stackSize, true), true).isEmpty()) {
                        other.insertItem(i, self.extractItem(j, stackSize, false), false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<ItemStack> toStackList(IItemHandler handler) {
        List<ItemStack> temp = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) temp.add(stack);
            else temp.add(ItemStack.EMPTY);
        }
        return temp;
    }

    public static int countSlot(IItemHandler handler, int index) {
        return handler.getStackInSlot(index).getCount();
    }
}