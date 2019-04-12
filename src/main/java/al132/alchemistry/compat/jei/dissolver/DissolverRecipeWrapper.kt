package al132.alchemistry.compat.jei.dissolver

import al132.alchemistry.Reference
import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.DissolverRecipe
import al132.alib.utils.Translator
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.minecraft.client.Minecraft
import java.awt.Color

class DissolverRecipeWrapper(recipe: DissolverRecipe) : AlchemistryRecipeWrapper<DissolverRecipe>(recipe) {

    fun formatProbability(probability: Double): String {
        if (recipe.outputs.relativeProbability) return Reference.DECIMAL_FORMAT.format(probability * 100) + "%"
        else return Reference.DECIMAL_FORMAT.format(probability) + "%"
    }

    override fun drawInfo(minecraft: Minecraft?, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {

        var y = 50
        for (index in recipe.outputs.set.indices) {
            val text = formatProbability(recipe.outputs.probabilityAtIndex(index))
            minecraft!!.fontRenderer?.drawString(text, 0/*-5*/, y, Color.BLACK.rgb)
            y += 18
        }

        var probabilityType = ""
        if (recipe.outputs.relativeProbability) probabilityType = Translator.translateToLocal("jei.dissolver.relative")
        else probabilityType = Translator.translateToLocal("jei.dissolver.absolute")

        minecraft!!.fontRenderer.drawString("${Translator.translateToLocal("jei.dissolver.type")}: $probabilityType", 5, 4, Color.BLACK.rgb)
        minecraft.fontRenderer.drawString("${Translator.translateToLocal("jei.dissolver.rolls")}: ${recipe.outputs.rolls}", 5, 16, Color.BLACK.rgb)
    }

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.inputs)
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs.toStackList())
    }
}