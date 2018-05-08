package al132.alchemistry.compat.jei.combiner

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.CombinerRecipe
import mezz.jei.api.ingredients.IIngredients
import net.minecraft.item.ItemStack

class CombinerRecipeWrapper(recipe: CombinerRecipe) : AlchemistryRecipeWrapper<CombinerRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInputs(ItemStack::class.java, recipe.inputs)
        ingredients.setOutput(ItemStack::class.java, recipe.output)
    }
}