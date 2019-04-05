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
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by al132 on 4/27/2017.
 */


fun String.toElementStack(quantity: Int = 1): ItemStack =
        ElementRegistry[this]?.toItemStack(quantity) ?: ItemStack.EMPTY

fun String.toCompoundStack(quantity: Int = 1): ItemStack =
        CompoundRegistry[this.replace(" ", "_")]?.toItemStack(quantity) ?: ItemStack.EMPTY

fun String.toStack(quantity: Int = 1, meta: Int = 0): ItemStack {
    val actualMeta = this.split(":").last().toIntOrNull() ?: meta
    val resourceLocation =
            if (this.count { it == ':' } == 2) ResourceLocation(this.dropLastWhile { it != ':' }.dropLast(1))
            else ResourceLocation(this)
    var outputStack: ItemStack = ItemStack.EMPTY
    val outputItem: Item? = Item.REGISTRY.getObject(resourceLocation)
    val outputBlock: Block? = Block.REGISTRY.getObject(resourceLocation)
    val outputElement: ChemicalElement? = ElementRegistry[this]
    val outputCompound: ChemicalCompound? = CompoundRegistry[this.replace(" ", "_")]
    if (outputElement != null) {
        outputStack = outputElement.toItemStack(quantity = quantity)
    } else if (outputCompound != null) {
        outputStack = outputCompound.toItemStack(quantity = quantity)
    } else if (outputItem != null) {
        outputStack = outputItem.toStack(quantity = quantity, meta = actualMeta)
    } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
        outputStack = outputBlock.toStack(quantity = quantity, meta = actualMeta)
    }
    return outputStack
}

fun String.toStacks(quantity: Int = 1, meta: Int = 0): List<ItemStack> {
    val output = arrayListOf<ItemStack>()
    val singleStack = this.toStack(quantity, meta)
    if (singleStack.isEmpty) {
        output.addAll(OreDictionary.getOres(this))
    } else {
        output.add(singleStack)
    }
    return output
}