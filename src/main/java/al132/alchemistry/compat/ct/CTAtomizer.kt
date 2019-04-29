package al132.alchemistry.compat.ct

import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.AtomizerRecipe
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

@ZenClass("mods.alchemistry.Atomizer")
@ModOnly("alchemistry")
@ZenRegister
object CTAtomizer {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: IItemStack, input: ILiquidStack) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe() = "Added Atomizer recipe for [$input] -> [$output]"

            override fun apply() {
                val inputStack = input.internal as FluidStack
                val outputStack = output.internal as ItemStack
                ModRecipes.atomizerRecipes.add(AtomizerRecipe(false,inputStack, outputStack))
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: ILiquidStack) {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Added Atomizer recipe for [$input]"

            override fun apply() {
                val inputStack = input.internal as FluidStack
                ModRecipes.atomizerRecipes.removeIf { it.input.isFluidEqual(inputStack) }
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeAllRecipes() {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed ALL Atomizer recipes"

            override fun apply() = ModRecipes.atomizerRecipes.clear()
        })
    }
}