package al132.alchemistry.utils;

import al132.chemlib.chemistry.CompoundRegistry;
import al132.chemlib.chemistry.ElementRegistry;
import al132.chemlib.items.CompoundItem;
import al132.chemlib.items.ElementItem;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
/*
    public static boolean areStacksEqualIgnoreQuantity(ItemStack a, ItemStack b) {
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        } else if (!a.isEmpty() && !b.isEmpty()) {
            return ItemStack.matches(a, b) && ItemStack.tagMatches(a, b);
        } else return false;
    }
*/
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

    public static CompoundTag saveAllItems(CompoundTag p_191282_0_, NonNullList<ItemStack> p_191282_1_) {
        return saveAllItems(p_191282_0_, p_191282_1_, true);
    }

    public static CompoundTag saveAllItems(CompoundTag p_191281_0_, NonNullList<ItemStack> p_191281_1_, boolean p_191281_2_) {
        ListTag listnbt = new ListTag();
        for (int i = 0; i < p_191281_1_.size(); ++i) {
            ItemStack itemstack = p_191281_1_.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.putByte("Slot", (byte) i);
                itemstack.save(compoundnbt);
                listnbt.add(compoundnbt);
            }
        }

        if (!listnbt.isEmpty() || p_191281_2_) {
            p_191281_0_.put("Items", listnbt);
        }

        return p_191281_0_;
    }

    public static void loadAllItems(CompoundTag p_191283_0_, NonNullList<ItemStack> p_191283_1_) {
        ListTag listnbt = p_191283_0_.getList("Items", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            int j = compoundnbt.getByte("Slot") & 255;
            if (j >= 0 && j < p_191283_1_.size()) {
                p_191283_1_.set(j, ItemStack.of(compoundnbt));
            }
        }
    }
}
