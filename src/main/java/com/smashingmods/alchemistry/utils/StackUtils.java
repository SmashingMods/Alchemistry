package com.smashingmods.alchemistry.utils;

import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class StackUtils {

    public static ItemStack atomicNumToStack(int atomicNumber) {
        return new ItemStack(ItemRegistry.getElementByAtomicNumber(atomicNumber).get());
    }

    public static boolean canStacksMerge(ItemStack origin, ItemStack target, boolean stacksCanbeEmpty) {
        if (stacksCanbeEmpty && (target.isEmpty() || origin.isEmpty())) return true;
        else {
            return origin.getItem() == target.getItem()
                    && origin.getCount() + target.getCount() <= origin.getMaxStackSize()
                    && origin.isStackable() && target.isStackable()
                    && origin.getTag() == target.getTag();
        }
    }

    public static ItemStack toStack(String pString) {
        return toStack(pString, 1);
    }

    public static ItemStack toStack(String pString, int pCount) {

        ResourceLocation resourceLocation = new ResourceLocation(pString);

        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(pString);
        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(pString.replace(" ", "_"));

        Item outputItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(resourceLocation);

        if (optionalElement.isPresent()) {
            return new ItemStack(optionalElement.get(), pCount);
        } else if (optionalCompound.isPresent()) {
            return new ItemStack(optionalCompound.get(), pCount);
        } else if (outputItem != null) {
            return new ItemStack(outputItem, pCount);
        } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
            return new ItemStack(outputBlock, pCount);
        } else {
            return ItemStack.EMPTY;
        }
    }
}
