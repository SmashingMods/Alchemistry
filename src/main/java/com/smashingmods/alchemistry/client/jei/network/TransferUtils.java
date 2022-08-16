package com.smashingmods.alchemistry.client.jei.network;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransferUtils {

    public static int getMaxOperations(ItemStack pRecipeInput, boolean pMaxTransfer) {
        return getMaxOperations(pRecipeInput, ItemStack.EMPTY, pMaxTransfer, true);
    }

    public static int getMaxOperations(List<ItemStack> pRecipeInputList, boolean pMaxTransfer) {
        return getMaxOperations(pRecipeInputList, new ArrayList<>(), pMaxTransfer, true);
    }

    public static int getMaxOperations(ItemStack pRecipeInput, ItemStack pInventoryInput, boolean pMaxTransfer, boolean pCreative) {
        int maxCount;
        if (pMaxTransfer) {
            if (pCreative) {
                maxCount = pRecipeInput.getMaxStackSize();
            } else {
                maxCount = pInventoryInput.getCount();
            }
        } else {
            maxCount = pRecipeInput.getCount();
        }
        return maxCount / pRecipeInput.getCount();
    }

    public static int getMaxOperations(List<ItemStack> pRecipeInputList, @Nonnull List<ItemStack> pInventoryItems, boolean pMaxTransfer, boolean pCreative) {

        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < pRecipeInputList.size(); i++) {
            int maxCount;
            if (pMaxTransfer) {
                if (pCreative) {
                    maxCount = pRecipeInputList.get(i).getMaxStackSize();
                } else {
                    maxCount = pInventoryItems.get(i).getCount();
                }
            } else {
                maxCount = pRecipeInputList.get(i).getCount();
            }
            ints.add(maxCount / pRecipeInputList.get(i).getCount());
        }
        return Collections.min(ints);
    }
}
