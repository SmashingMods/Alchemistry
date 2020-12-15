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
import net.minecraftforge.registries.ForgeRegistries;

public class StackUtils {

    public static ItemStack atomicNumToStack(int atomicNumber) {
        return new ItemStack(ElementRegistry.elements.get(atomicNumber));
    }

    public static boolean areStacksEqualIgnoreQuantity(ItemStack a, ItemStack b) {
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        } else if (!a.isEmpty() && !b.isEmpty()) {
            return ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b);
        } else return false;
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

    public static ItemStack toStack(String str) {
        return toStack(str, 1);
    }

    public static ItemStack toStack(Item item) {
        return new ItemStack(item);
    }

    public static ItemStack toStack(Item item, int count) {
        return new ItemStack(item, count);
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
