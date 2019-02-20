package al132.alchemistry.compat.ct

import al132.alchemistry.Alchemistry
import al132.alchemistry.recipes.DissolverRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.ProbabilityGroup
import al132.alchemistry.recipes.ProbabilitySet
import al132.alib.utils.extensions.containsItem
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import crafttweaker.api.oredict.IOreDictEntry
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Dissolver")
@ModOnly("alchemistry")
@ZenRegister
object CTChemicalDissolver {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, relativeProbability: Boolean, rolls: Int, outputs: Array<Array<Any>>) {
        Alchemistry.LATE_ADDITIONS.add(Add(input, relativeProbability, rolls, outputs))
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IIngredient) {
        Alchemistry.LATE_REMOVALS.add(Remove(input))
    }

    class Add(val input: IIngredient, val relativeProbability: Boolean, val rolls: Int, val outputs: Array<Array<Any>>) : IAction {

        override fun apply() {
            val groups = ArrayList<ProbabilityGroup>()
            outputs.forEach { rawArray ->
                val probability = rawArray[0] as Int
                val group = rawArray.drop(1).map { (it as IItemStack).internal as ItemStack }
                groups.add(ProbabilityGroup(group, probability))
            }
            val outputSet = ProbabilitySet(_set = groups, relativeProbability = relativeProbability, rolls = rolls)
            if (input is IOreDictEntry) {
                ModRecipes.dissolverRecipes.add(DissolverRecipe(input.name as String, ItemStack.EMPTY, input.amount, false, outputSet))
            } else if (input is IItemStack) {
                ModRecipes.dissolverRecipes.add(DissolverRecipe("", input.internal as ItemStack, input.amount, false, outputSet))
            }
        }

        override fun describe(): String? {
            return null
        }
    }

    class Remove(val input: IIngredient) : IAction {

        override fun apply() {
            val inputStack = input.internal
            if (inputStack is ItemStack) ModRecipes.dissolverRecipes.removeIf { it.inputs.containsItem(inputStack) }
            else if (inputStack is String) ModRecipes.dissolverRecipes.removeIf { it.dictName == inputStack }
        }

        override fun describe(): String? {
            return null
        }
    }
}