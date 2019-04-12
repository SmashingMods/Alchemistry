package al132.alchemistry.compat.jei.evaporator

import al132.alchemistry.client.GuiEvaporator
import al132.alchemistry.compat.jei.AlchemistryRecipeCategory
import al132.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alib.utils.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class EvaporatorRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<EvaporatorRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 100, 125),
        "jei.evaporator.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.evaporator.name")

    override fun getUid(): String = AlchemistryRecipeUID.EVAPORATOR

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: EvaporatorRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val guiFluidStacks = recipeLayout.fluidStacks


        var x = 121 - u
        var y = 51 - v
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        guiItemStacks.set(OUTPUT_ONE, ingredients.getOutputs(VanillaTypes.ITEM)[0])

        x = 48 - u
        y = 69 - u
        val inputStack = ingredients.getInputs(VanillaTypes.FLUID)[0][0]
        guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 60, inputStack.amount, false, null)
        guiFluidStacks.set(FLUID_ONE, inputStack)
    }

    companion object {

        private val OUTPUT_ONE = 1
        private val FLUID_ONE = 1

        private val u = 40
        private val v = 11

        private val guiTexture = GuiEvaporator.textureLocation
    }
}