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
