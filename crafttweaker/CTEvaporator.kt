package al132.alchemistry.compat.crafttweaker

import al132.alchemistry.compat.jei.evaporator.EvaporatorRecipeWrapper
import al132.alchemistry.recipes.EvaporatorRecipe
import al132.alchemistry.recipes.ModRecipes
import minetweaker.IUndoableAction
import minetweaker.MineTweakerAPI
import minetweaker.api.item.IItemStack
import minetweaker.api.liquid.ILiquidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

/**
 * Created by al132 on 4/28/2017.
 */

//TODO recipe addition/removal works, but doesn't show correctly in JEI
@ZenClass("mods.alchemistry.Evaporator")
object CTEvaporator {
    val name = "Alchemistry Evaporator"

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: ILiquidStack, output: IItemStack) {
        MineTweakerAPI.apply(Add(EvaporatorRecipe(input.toFluid(), output.toStack())))

    }

    @ZenMethod
    @JvmStatic
    fun remove(input: ILiquidStack) {
        MineTweakerAPI.apply(Remove(input))
    }

    private class Add(var recipe: EvaporatorRecipe) : IUndoableAction {

        override fun getOverrideKey(): Any? = null

        override fun describe() = "Adding" //TODO

        override fun apply() {
            ModRecipes.evaporatorRecipes.add(recipe)
            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(EvaporatorRecipeWrapper(recipe))
        }

        override fun undo() {
            MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(EvaporatorRecipeWrapper(recipe))
            ModRecipes.evaporatorRecipes.remove(recipe)
        }

        override fun canUndo() = true

        override fun describeUndo() = "Undoing Adding" //TODO
    }


    private class Remove(val input: ILiquidStack) : IUndoableAction {

        override fun getOverrideKey(): Any? = null
        lateinit var recipe: EvaporatorRecipe

        override fun describe() = "Removing" //TODO

        override fun apply() {
            ModRecipes.evaporatorRecipes.filter { it.input.isFluidEqual(input.toFluid()) }
                    .forEach {
                        recipe = it
                        MineTweakerAPI.ijeiRecipeRegistry.removeRecipe(EvaporatorRecipeWrapper(it))
                        ModRecipes.evaporatorRecipes.remove(it)
                    }
        }

        override fun undo() {
            ModRecipes.evaporatorRecipes.add(recipe)
            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(EvaporatorRecipeWrapper(recipe))
        }

        override fun canUndo(): Boolean = true

        override fun describeUndo() = "Undoing removal" //TODO
    }
}