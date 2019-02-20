package al132.alchemistry.compat.ct

import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.CombinerRecipe
import al132.alchemistry.recipes.ModRecipes
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Combiner")
@ModOnly("alchemistry")
@ZenRegister
object CTChemicalCombiner {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: IItemStack?, input: Array<IItemStack?>) {
        Alchemistry.LATE_ADDITIONS.add(Add(input, output))
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: Array<IItemStack?>) {
        Alchemistry.LATE_REMOVALS.add(Remove(input))
    }

    class Add(val input: Array<IItemStack?>, val output: IItemStack?) : IAction {

        override fun apply() {
            val inputStacks = input.toList()
            val outputStack: ItemStack? = output?.internal as? ItemStack
            if(outputStack != null) {
                val recipe = CombinerRecipe(outputStack, inputStacks.map { stack: IItemStack? ->
                    (stack?.internal as? ItemStack) ?: ItemStack.EMPTY
                    // if(stack == null || stack.internal == null) ItemStack.EMPTY else stack.internal
                })
                ModRecipes.combinerRecipes.add(recipe)
            }
            else Alchemistry.logger.info("Unable to add crafttweaker recipe")
        }

        override fun describe(): String? = null
    }

    class Remove(var input: Array<IItemStack?>) : IAction {

        override fun apply() {
            val inputs = input.map { it?.internal as? ItemStack ?: ItemStack.EMPTY }
            val handler = ItemStackHandler(9)
            for (i in 0 until inputs.size) {
                handler.setStackInSlot(i, inputs[i])
            }
            val recipe = CombinerRecipe.match(handler)
            if (recipe != null) ModRecipes.combinerRecipes.remove(recipe)
        }

        override fun describe(): String? = null
    }
}