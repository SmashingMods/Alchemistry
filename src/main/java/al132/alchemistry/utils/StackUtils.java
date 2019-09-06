package al132.alchemistry.utils;

import net.minecraft.item.ItemStack;

public class StackUtils {

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
}
