package al132.alchemistry.utils

import net.minecraft.item.ItemStack

/**
 * Created by al132 on 6/25/2017.
 */

fun ItemStack.areStacksEqualIgnoreQuantity(other: ItemStack): Boolean {
    return this.item == other.item
            && this.metadata == other.metadata
            && ItemStack.areItemStackTagsEqual(this, other)
}