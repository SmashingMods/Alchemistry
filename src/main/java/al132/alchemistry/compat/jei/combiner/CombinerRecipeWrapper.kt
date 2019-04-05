package al132.alchemistry.compat.jei.combiner

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.CombinerRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class CombinerRecipeWrapper(recipe: CombinerRecipe) : AlchemistryRecipeWrapper<CombinerRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.inputs)
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output)
    }
}