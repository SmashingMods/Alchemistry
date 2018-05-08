package al132.alchemistry.utils

import net.minecraft.item.ItemStack

/**
 * Created by al132 on 6/25/2017.
 */

object Utils {
    fun listContainsItemStack(stack: ItemStack, list: List<ItemStack>): Boolean {
        return list.any { listStack -> ItemStack.areItemsEqual(stack, listStack) && listStack.count >= stack.count }
    }
}