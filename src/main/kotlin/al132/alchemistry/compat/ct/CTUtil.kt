package al132.alchemistry.compat.ct

import al132.alchemistry.chemistry.CompoundPair
import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import crafttweaker.CraftTweakerAPI
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.mc1120.item.MCItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod
import java.awt.Color

@ZenClass("mods.alchemistry.Util")
@ModOnly("alchemistry")
@ZenRegister
object CTUtil {

    @ZenMethod
    @JvmStatic
    fun get(name: String): IItemStack? {
        val parsedName = name.trim().toLowerCase().replace(" ", "_")
        val compound: ItemStack = CompoundRegistry[parsedName]?.toItemStack(1) ?: ItemStack.EMPTY
        val element: ItemStack = ElementRegistry[parsedName]?.toItemStack(1) ?: ItemStack.EMPTY
        if (!compound.isEmpty) return MCItemStack.createNonCopy(compound)
        else if (!element.isEmpty) return MCItemStack.createNonCopy(element)
        else return null
    }

    @ZenMethod
    @JvmStatic
    fun createElement(atomicNumber: Int, name: String, abbreviation: String, red: Int, green: Int, blue: Int) {
        val parsedName = name.trim().toLowerCase().replace(" ", "_")
        CraftTweakerAPI.apply(object : IAction {
            override fun describe() = "Added new chemical element [$atomicNumber,$parsedName,$abbreviation]"
            override fun apply() {
                if (ElementRegistry[atomicNumber] == null) {
                    ElementRegistry.add(atomicNumber, parsedName, abbreviation,
                            Color(red.coerceIn(0, 255), green.coerceIn(0, 255), blue.coerceIn(0, 255)))
                }
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun createCompound(meta: Int, name: String, red: Int, green: Int, blue: Int, components: Array<Array<Any?>>) {
        val parsedName = name.trim().toLowerCase().replace(" ", "_")
        CraftTweakerAPI.apply(object : IAction {
            override fun describe() = "Added new chemical compound [$parsedName]"
            override fun apply() {
                if (CompoundRegistry[parsedName] == null) {
                    val parsedComponents = components.map { x: Array<Any?> -> CompoundPair((x.first() as String).toLowerCase().replace(" ", "_"), x[1] as Int) }
                    CompoundRegistry.addExternal(meta, parsedName,
                            Color(red.coerceIn(0, 255), green.coerceIn(0, 255), blue.coerceIn(0, 255)), parsedComponents)
                }
            }
        })
    }
}