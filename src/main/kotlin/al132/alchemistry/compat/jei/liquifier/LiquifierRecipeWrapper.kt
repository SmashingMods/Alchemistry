package al132.alchemistry.compat.jei.liquifier

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.LiquifierRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class LiquifierRecipeWrapper(recipe: LiquifierRecipe) : AlchemistryRecipeWrapper<LiquifierRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.input)
        ingredients.setOutput(VanillaTypes.FLUID, recipe.output)
    }
}