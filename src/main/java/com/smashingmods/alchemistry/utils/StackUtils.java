package com.smashingmods.alchemistry.utils;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.chemistry.CompoundRegistry;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import com.smashingmods.chemlib.items.CompoundItem;
import com.smashingmods.chemlib.items.ElementItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class StackUtils {

    public static ItemStack atomicNumToStack(int atomicNumber) {
        return new ItemStack(ElementRegistry.elements.get(atomicNumber));
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

        ElementItem outputElement = ElementRegistry.getByName(pString).orElse(null);
        CompoundItem outputCompound = CompoundRegistry.getByName(pString.replace(" ", "_")).orElse(null);

        Item outputItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(resourceLocation);

        if (outputElement != null) {

            Alchemistry.LOGGER.info(String.format("output element: %s", outputElement));

            return new ItemStack(outputElement, pCount);
        } else if (outputCompound != null) {

            Alchemistry.LOGGER.info(String.format("output compound: %s", outputCompound));

            return new ItemStack(outputCompound, pCount);
        } else if (outputItem != null) {

            Alchemistry.LOGGER.info(String.format("output item: %s", outputItem));

            return new ItemStack(outputItem, pCount);
        } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {

            Alchemistry.LOGGER.info(String.format("output block: %s", outputBlock));

            return new ItemStack(outputBlock, pCount);
        } else {
            return ItemStack.EMPTY;
        }
    }
}
