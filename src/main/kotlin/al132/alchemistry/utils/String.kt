package al132.alchemistry.utils

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import net.minecraft.item.ItemStack

/**
 * Created by al132 on 4/27/2017.
 */


fun String.toElementStack(quantity: Int = 1): ItemStack =
        ElementRegistry[this]?.toItemStack(quantity)?: ItemStack.EMPTY

fun String.toCompoundStack(quantity: Int = 1): ItemStack =
        CompoundRegistry[this]?.toItemStack(quantity)?: ItemStack.EMPTY