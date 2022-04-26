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

    public static List<ItemStack> toStackList(IItemHandler pHandler) {
        List<ItemStack> itemStackList = new ArrayList<>();
        for (int i = 0; i < pHandler.getSlots(); i++) {
            itemStackList.add(pHandler.getStackInSlot(i));
        }
        return itemStackList;
    }

    public static int countSlot(IItemHandler handler, int index) {
        return handler.getStackInSlot(index).getCount();
    }
}