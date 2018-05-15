package al132.alchemistry.utils

import al132.alchemistry.chemistry.ChemicalCompound
import al132.alchemistry.chemistry.ChemicalElement
import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alib.utils.extensions.toStack
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 6/25/2017.
 */

object Utils {
    fun listContainsItemStack(stack: ItemStack, list: List<ItemStack>): Boolean {
        return list.any { listStack -> ItemStack.areItemsEqual(stack, listStack) && listStack.count >= stack.count }
    }

    fun stackFromResourceLocation(path: String, quantity: Int = 1, meta: Int = 0): ItemStack {
        val resourceLocation = ResourceLocation(path)
        var outputStack: ItemStack = ItemStack.EMPTY
        val outputItem: Item? = Item.REGISTRY.getObject(resourceLocation)
        val outputBlock: Block? = Block.REGISTRY.getObject(resourceLocation)
        val outputElement: ChemicalElement? = ElementRegistry[path]
        val outputCompound: ChemicalCompound? = CompoundRegistry[path]
        if (outputItem != null) {
            outputStack = outputItem.toStack(quantity = quantity, meta = meta)
        } else if (outputBlock != null && outputBlock != Blocks.AIR) {
            outputStack = outputBlock.toStack(quantity = quantity, meta = meta)
        } else if (outputElement != null) {
            outputStack = outputElement.toItemStack(quantity = quantity)
        } else if (outputCompound != null) {
            outputStack = outputCompound.toItemStack(quantity = quantity)
        }
        return outputStack
    }
}