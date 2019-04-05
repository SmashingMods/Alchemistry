package al132.alchemistry.compat.jei.evaporator

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.EvaporatorRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class EvaporatorRecipeWrapper(recipe: EvaporatorRecipe) : AlchemistryRecipeWrapper<EvaporatorRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(VanillaTypes.FLUID, recipe.input)
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs)
    }
}