package al132.alchemistry.compat.jei.fission

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.FissionRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.minecraft.item.ItemStack

class FissionRecipeWrapper(recipe: FissionRecipe) : AlchemistryRecipeWrapper<FissionRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        val input = ElementRegistry[recipe.inputMeta]?.toItemStack(1)
        var output1 = ItemStack.EMPTY
        var output2 = ItemStack.EMPTY
        if (recipe.inputMeta % 2 == 0) {
            output1 = ElementRegistry[recipe.output1Meta]?.toItemStack(2)
        } else {
            output1 = ElementRegistry[recipe.output1Meta]?.toItemStack(1)
            output2 = ElementRegistry[recipe.output2Meta]?.toItemStack(1)
        }
        ingredients.setInput(VanillaTypes.ITEM, input ?: ItemStack.EMPTY)
        ingredients.setOutputs(VanillaTypes.ITEM, listOf(output1, output2))
    }
}