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
        Alchemistry.LATE_ADDITIONS.add(Add(input, output))
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack) {
        Alchemistry.LATE_REMOVALS.add(Remove(input))
    }

    class Add(internal var input: IItemStack, internal var output: ILiquidStack) : IAction {

        override fun apply() {
            val inputStack = input.internal as ItemStack
            val outputStack = output.internal as FluidStack
            ModRecipes.liquifierRecipes.add(LiquifierRecipe(inputStack, outputStack))
        }

        override fun describe() = "Added Liquifier recipe for [$input] -> [$output]"
    }

    class Remove(var input: IItemStack) : IAction {

        override fun apply() {
            val inputStack = input.internal as ItemStack
            ModRecipes.liquifierRecipes.removeIf { it.input.isItemEqual(inputStack) }
        }

        override fun describe() = "Removed Liquifier recipe for [$input]"

    }
}