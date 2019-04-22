package al132.alchemistry.compat.ct

import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.CombinerRecipe
import al132.alchemistry.recipes.ModRecipes
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Combiner")
@ModOnly("alchemistry")
@ZenRegister
object CTChemicalCombiner {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: IItemStack?, input: Array<IItemStack?>) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe(): String? = "Added Chemical Combiner recipe for [${input.toList()}]->[$output]"

            override fun apply() {
                val inputStacks = input.toList()
                val outputStack: ItemStack? = output?.internal as? ItemStack
                if (outputStack != null) {
                    val recipe = CombinerRecipe(outputStack, inputStacks.map { stack: IItemStack? ->
                        (stack?.internal as? ItemStack) ?: ItemStack.EMPTY
                    })
                    ModRecipes.combinerRecipes.add(recipe)
                } else Alchemistry.logger.info("Unable to add crafttweaker recipe")
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack?) {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe(): String? = "Removed Chemical Combiner recipe for [$input]"

            override fun apply() {
                val unwrappedInput = input?.internal as? ItemStack ?: ItemStack.EMPTY
                val recipe = CombinerRecipe.matchOutput(unwrappedInput)
                if (recipe != null) ModRecipes.combinerRecipes.remove(recipe)
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeAllRecipes() {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed ALL Chemical Combiner recipes"

            override fun apply() = ModRecipes.combinerRecipes.clear()
        })
    }
}