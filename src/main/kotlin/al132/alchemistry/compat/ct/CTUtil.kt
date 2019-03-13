package al132.alchemistry.compat.ct

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.mc1120.item.MCItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Util")
@ModOnly("alchemistry")
@ZenRegister
object CTUtil {

    @ZenMethod
    @JvmStatic
    fun get(name: String): IItemStack? {
        val parsedName = name.toLowerCase().replace(" ","_")
        val compound: ItemStack = CompoundRegistry[parsedName]?.toItemStack(1)?:ItemStack.EMPTY
        val element: ItemStack = ElementRegistry[parsedName]?.toItemStack(1)?:ItemStack.EMPTY
        if(!compound.isEmpty) return MCItemStack.createNonCopy(compound)
        else if(!element.isEmpty) return MCItemStack.createNonCopy(element)
        else {

            return null
        }
    }
}