package al132.alchemistry.compat.jei.atomizer

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.AtomizerRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class AtomizerRecipeWrapper(recipe: AtomizerRecipe) : AlchemistryRecipeWrapper<AtomizerRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(VanillaTypes.FLUID, recipe.input)
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output)
    }
}