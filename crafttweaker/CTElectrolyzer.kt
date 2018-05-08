package al132.alchemistry.compat.crafttweaker

import al132.alchemistry.recipes.ElectrolyzerRecipe
import al132.alchemistry.recipes.ModRecipes
import minetweaker.IUndoableAction
import minetweaker.MineTweakerAPI
import minetweaker.api.item.IItemStack
import minetweaker.api.liquid.ILiquidStack
import net.minecraftforge.fluids.FluidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

/**
 * Created by al132 on 4/28/2017.
 */

@ZenClass("mods.alchemistry.Electrolyzer")
object CTElectrolyzer {
    val name = "Alchemistry Electrolyzer"

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: ILiquidStack, stack1: IItemStack, stack2: IItemStack, consumptionProbability: Int,
                  electrolyte: IItemStack) {
        MineTweakerAPI.apply(Add(ElectrolyzerRecipe(
                input.toFluid(), listOf(electrolyte.toStack()), consumptionProbability, stack1.toStack(), stack2.toStack())))
        println("TRYING TO ADD NOW")

    }

    @ZenMethod
    @JvmStatic
    fun remove(input: ILiquidStack) {
        MineTweakerAPI.apply(Remove(input.toFluid()))
    }

    private class Add(val recipe: ElectrolyzerRecipe) : IUndoableAction {

        override fun getOverrideKey(): Any? = null
        override fun canUndo() = true
        override fun describe() = "Adding" //TODO
        override fun describeUndo() = "Undoing Adding" //TODO

        override fun apply() {
            ModRecipes.electrolyzerRecipes.add(recipe)
            MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe)//ElectrolyzerRecipeWrapper(recipe))
        }

        override fun undo() {
            MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe)//ElectrolyzerRecipeWrapper(recipe))
            ModRecipes.electrolyzerRecipes.remove(recipe)
        }
    }


    private class Remove(val input: FluidStack) : IUndoableAction {

        val removedRecipes = ArrayList<ElectrolyzerRecipe>()

        override fun getOverrideKey(): Any? = null
        override fun describe() = "Removing electrolyzer recipe" //TODO
        override fun canUndo(): Boolean = true
        override fun describeUndo() = "Undoing electrolyzer recipe removal" //TODO

        override fun apply() {
            ModRecipes.electrolyzerRecipes.filter { it.input.isFluidEqual(input) }
                    .forEach { recipe ->
                        MineTweakerAPI.ijeiRecipeRegistry.removeRecipe(recipe)//ElectrolyzerRecipeWrapper(recipe))
                        removedRecipes.add(recipe)
                        ModRecipes.electrolyzerRecipes.remove(recipe)
                    }
        }

        override fun undo() {
            removedRecipes.forEach { recipe ->
                ModRecipes.electrolyzerRecipes.add(recipe)
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe)//ElectrolyzerRecipeWrapper(recipe))
            }
        }
    }
}