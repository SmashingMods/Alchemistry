package al132.alchemistry.compat.jei.electrolyzer

import al132.alchemistry.client.GuiElectrolyzer
import al132.alchemistry.compat.jei.AlchemistryRecipeCategory
import al132.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alchemistry.compat.jei.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.gui.ITooltipCallback
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.minecraft.item.ItemStack

class ElectrolyzerRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<ElectrolyzerRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 100, 125),
        "jei.electrolyzer.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.electrolyzer.name")

    override fun getUid(): String = AlchemistryRecipeUID.ELECTROLYZER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: ElectrolyzerRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val guiFluidStacks = recipeLayout.fluidStacks


        var x = 84 - u
        var y = 38 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, recipeWrapper.recipe.electrolytes)

        x = 121 - u
        y = 51 - v
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        y += 18
        guiItemStacks.init(OUTPUT_TWO, false, x, y)
        y += 18
        guiItemStacks.init(OUTPUT_THREE, false, x, y)
        y += 18
        guiItemStacks.init(OUTPUT_FOUR, false, x, y)

        guiItemStacks.set(OUTPUT_ONE, ingredients.getOutputs(VanillaTypes.ITEM)[0])
        guiItemStacks.set(OUTPUT_TWO, ingredients.getOutputs(VanillaTypes.ITEM)[1])
        guiItemStacks.set(OUTPUT_THREE, ingredients.getOutputs(VanillaTypes.ITEM)[2])
        guiItemStacks.set(OUTPUT_FOUR, ingredients.getOutputs(VanillaTypes.ITEM)[3])


        x = 48 - u
        y = 69 - u
        val inputStack = ingredients.getInputs(VanillaTypes.FLUID)[0][0]
        guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 60, inputStack.amount, false, null)
        guiFluidStacks.set(FLUID_ONE, inputStack)

        guiItemStacks.addTooltipCallback(object : ITooltipCallback<ItemStack> {
            override fun onTooltip(slotIndex: Int, input: Boolean, ingredient: ItemStack?, tooltip: MutableList<String>?) {
                if (input) {
                    tooltip?.add(Translator.translateToLocal("jei.electrolyzer.electrolyte"))
                    tooltip?.add(Translator.translateToLocal("jei.electrolyzer.consumption_probability") + ": ${recipeWrapper.recipe.electrolyteConsumptionChance}%")
                }
            }
        })
    }

    companion object {

        private val INPUT_ONE = 0
        private val OUTPUT_ONE = 1
        private val OUTPUT_TWO = 2
        private val OUTPUT_THREE = 3
        private val OUTPUT_FOUR = 4
        private val FLUID_ONE = 1

        private val u = 40
        private val v = 11

        private val guiTexture = GuiElectrolyzer.textureLocation()
    }
}