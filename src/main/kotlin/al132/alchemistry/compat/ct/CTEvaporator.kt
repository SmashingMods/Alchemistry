package al132.alchemistry.compat.ct


import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.EvaporatorRecipe
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

@ZenClass("mods.alchemistry.Evaporator")
@ModOnly("alchemistry")
@ZenRegister
object CTEvaporator {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: IItemStack, input: ILiquidStack) {
        Alchemistry.LATE_ADDITIONS.add(Add(input, output))
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: ILiquidStack) {
        Alchemistry.LATE_REMOVALS.add(Remove(input))
    }

    class Add(internal var input: ILiquidStack, internal var output: IItemStack) : IAction {

        override fun apply() {
            val inputStack = input.internal as FluidStack
            val outputStack = output.internal as ItemStack
            ModRecipes.evaporatorRecipes.add(EvaporatorRecipe(inputStack, outputStack))
        }

        override fun describe() = "Added Evaporator recipe for [$input] -> [$output]"
    }

    class Remove(var input: ILiquidStack) : IAction {

        override fun apply() {
            val inputStack = input.internal as FluidStack
            ModRecipes.evaporatorRecipes.removeIf { it.input.isFluidEqual(inputStack) }
        }

        override fun describe() = "Removed Evaporator recipe for [$input]"

    }
}