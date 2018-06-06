package al132.alchemistry.compat.jei.atomizer

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.AtomizerRecipe
import mezz.jei.api.ingredients.IIngredients
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class AtomizerRecipeWrapper(recipe: AtomizerRecipe) : AlchemistryRecipeWrapper<AtomizerRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(FluidStack::class.java, recipe.input)
        ingredients.setOutput(ItemStack::class.java, recipe.output)
    }
}