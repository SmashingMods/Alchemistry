package al132.alchemistry.compat.ct

import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.LiquifierRecipe
import al132.alchemistry.recipes.ModRecipes
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Liquifier")
@ModOnly("alchemistry")
@ZenRegister
object CTLiquifier {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: ILiquidStack, input: IItemStack) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe() = "Added Liquifier recipe for [$input] -> [$output]"

            override fun apply() {
                val inputStack = input.internal as ItemStack
                val outputStack = output.internal as FluidStack
                ModRecipes.liquifierRecipes.add(LiquifierRecipe(inputStack, outputStack))
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack) {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed Liquifier recipe for [$input]"

            override fun apply() {
                val inputStack = input.internal as ItemStack
                ModRecipes.liquifierRecipes.removeIf { it.input.isItemEqual(inputStack) }
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeAllRecipes() {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed ALL Liquifier recipes"

            override fun apply() = ModRecipes.liquifierRecipes.clear()
        })
    }
}