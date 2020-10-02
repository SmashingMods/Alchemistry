package al132.alchemistry.utils;

import al132.chemlib.chemistry.CompoundRegistry;
import al132.chemlib.chemistry.ElementRegistry;
import al132.chemlib.items.CompoundItem;
import al132.chemlib.items.ElementItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

public class StringUtils {

    public static ItemStack toStack(String str) {
        return toStack(str, 1);
    }

    public static ItemStack toStack(String str, int quantity) {
        ResourceLocation resourceLocation = new ResourceLocation(str);
        ItemStack outputStack = ItemStack.EMPTY;
        Item outputItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(resourceLocation);
        ElementItem outputElement = ElementRegistry.getByName(str).orElse(null);//[this]
        CompoundItem outputCompound = CompoundRegistry.getByName(str.replace(" ", "_")).orElse(null);//)[this.replace(" ", "_")]
        if (outputElement != null) {
            outputStack = new ItemStack(outputElement, quantity);
        } else if (outputCompound != null) {
            outputStack = new ItemStack(outputCompound, quantity);
        } else if (outputItem != null) {
            outputStack = new ItemStack(outputItem, quantity);//.toStack(quantity = quantity, meta = actualMeta)
        } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
            outputStack = new ItemStack(outputBlock, quantity);//.toStack(quantity = quantity, meta = actualMeta)
        }
        return outputStack;
    }
}
