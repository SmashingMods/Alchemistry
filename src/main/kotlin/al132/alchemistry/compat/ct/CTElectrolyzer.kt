package al132.alchemistry.compat.ct

import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.ElectrolyzerRecipe
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

@ZenClass("mods.alchemistry.Electrolyzer")
@ModOnly("alchemistry")
@ZenRegister
object CTElectrolyzer {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: ILiquidStack, electrolyte: IItemStack, electrolyteConsumptionChance: Int,
                  output1: IItemStack, output2: IItemStack, output3: IItemStack?, output3Chance: Int?,
                  output4: IItemStack?, output4Chance: Int?) {
        Alchemistry.LATE_ADDITIONS.add(Add(input,electrolyte,electrolyteConsumptionChance,output1,output2,output3,output3Chance,output4,output4Chance))
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: ILiquidStack, electrolyte: IItemStack) {
        Alchemistry.LATE_REMOVALS.add(Remove(input,electrolyte))
    }


    class Add(val input: ILiquidStack, val electrolyte: IItemStack, val electrolyteConsumptionChance: Int,
              val output1: IItemStack, val output2: IItemStack, val output3: IItemStack?, val output3Chance: Int?,
              val output4: IItemStack?, val output4Chance: Int?) : IAction {

        override fun apply() {
            val inputStack: FluidStack? = input.internal as? FluidStack
            val electrolytestack: ItemStack? = electrolyte.internal as? ItemStack
            val output1Stack: ItemStack? = output1.internal as? ItemStack
            val output2Stack: ItemStack? = output2.internal as? ItemStack
            val output3Stack: ItemStack = (output3?.internal as? ItemStack) ?: ItemStack.EMPTY
            val output4Stack: ItemStack = (output4?.internal as? ItemStack) ?: ItemStack.EMPTY
            if (inputStack != null && electrolytestack != null && output1Stack != null && output2Stack != null) {
                val recipe = ElectrolyzerRecipe(inputStack, electrolytestack, "", electrolyteConsumptionChance,
                        output1Stack, output2Stack, output3Stack, output3Chance ?: 0, output4Stack, output4Chance ?: 0)
                ModRecipes.electrolyzerRecipes.add(recipe)
            } else Alchemistry.logger.info("Unable to add crafttweaker recipe")
        }

        override fun describe(): String? = null
    }

    class Remove(val input: ILiquidStack, val electrolyte: IItemStack) : IAction {

        override fun apply() {
            val inputStack: FluidStack? = input.internal as? FluidStack
            val electrolyteStack: ItemStack? = electrolyte.internal as? ItemStack
            if (inputStack != null && electrolyteStack != null) {
                ModRecipes.electrolyzerRecipes.removeIf { it.input.isFluidEqual(inputStack) && it.matchesElectrolyte(electrolyteStack) }
            }
        }

        override fun describe(): String? = null
    }
}