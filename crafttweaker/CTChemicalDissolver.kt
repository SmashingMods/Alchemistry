package al132.alchemistry.compat.crafttweaker

import al132.alchemistry.recipes.DissolverRecipe
import minetweaker.IUndoableAction
import minetweaker.api.item.IIngredient
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

/**
 * Created by al132 on 4/28/2017.
 */

//TODO
@ZenClass("mods.alchemistry.ChemicalDissolver")
class CTChemicalDissolver {

    val name = "Alchemistry Chemical Dissolver"

    @ZenMethod
    fun addRecipe(input: IIngredient, recipeType: String) {
    }

    @ZenMethod
    fun remove() {
    }


    private class Add(var recipe: DissolverRecipe) : IUndoableAction {
        override fun getOverrideKey(): Any? = null

        override fun describe() = ""

        override fun apply() {}

        override fun undo() {}

        override fun canUndo() = true

        override fun describeUndo() = ""
    }

    private class Remove : IUndoableAction {
        override fun getOverrideKey(): Any? = null

        override fun describe() = ""

        override fun apply() {}

        override fun undo() {}

        override fun canUndo() = true

        override fun describeUndo() = ""

    }
}