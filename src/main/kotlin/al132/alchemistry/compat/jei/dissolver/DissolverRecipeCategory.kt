package al132.alchemistry.compat.jei.dissolver

import al132.alchemistry.compat.jei.AlchemistryRecipeCategory
import al132.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alchemistry.compat.jei.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class DissolverRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<DissolverRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, width, height),
        "jei.dissolver.name") {


    companion object {
        val INPUT_ONE = 2
        val OUTPUT_STARTING_INDEX = 3
        val u = 5
        val v = 5
        val width = 180//170
        val height = 256//170
        val guiTexture = ResourceLocation("alchemistry:textures/gui/container/chemical_dissolver_jei.png")
    }

    override fun getTitle() = Translator.translateToLocal("jei.dissolver.name")

    override fun getUid(): String = AlchemistryRecipeUID.DISSOLVER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: DissolverRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks

        val inputStack: List<ItemStack> = ingredients.getInputs(ItemStack::class.java)[0]
        val outputSet = recipeWrapper.recipe.outputs.set

        var x = 95 - u
        var y = 7 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, inputStack)
        x = 50 - u
        y = 50 - v

        var outputSlotIndex = OUTPUT_STARTING_INDEX
        for (component in outputSet) {
            for (stack in component.output) {
                guiItemStacks.init(outputSlotIndex, false, x, y)
                guiItemStacks.set(outputSlotIndex, stack)
                x += 18
                outputSlotIndex++
            }
            x = 50 - u
            y += 18
        }
    }
}