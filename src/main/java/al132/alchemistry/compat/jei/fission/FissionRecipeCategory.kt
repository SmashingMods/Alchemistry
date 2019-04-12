package al132.alchemistry.compat.jei.fission

import al132.alchemistry.client.GuiFissionController
import al132.alchemistry.compat.jei.AlchemistryRecipeCategory
import al132.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alib.utils.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class FissionRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<FissionRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 120, 50),
        "jei.fission_controller.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.fission_controller.name")

    override fun getUid(): String = AlchemistryRecipeUID.FISSION

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FissionRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks

        var x = 48 - u
        var y = 59 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, ingredients.getInputs(VanillaTypes.ITEM)[0])

        x = 121 - u
        val output1 = ingredients.getOutputs(VanillaTypes.ITEM)[0]
        val output2 = ingredients.getOutputs(VanillaTypes.ITEM)[1]
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        guiItemStacks.set(OUTPUT_ONE, output1)
        x += 18
        guiItemStacks.init(OUTPUT_TWO, false, x, y)
        guiItemStacks.set(OUTPUT_TWO, output2)
    }

    companion object {

        private val INPUT_ONE = 0
        private val OUTPUT_ONE = 1
        private val OUTPUT_TWO = 2

        private val u = 45
        private val v = 41

        private val guiTexture = GuiFissionController.textureLocation
    }
}