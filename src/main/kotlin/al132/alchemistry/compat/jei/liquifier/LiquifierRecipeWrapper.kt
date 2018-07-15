package al132.alchemistry.compat.jei.liquifier

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.LiquifierRecipe
import mezz.jei.api.ingredients.IIngredients
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class LiquifierRecipeWrapper(recipe: LiquifierRecipe) : AlchemistryRecipeWrapper<LiquifierRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(ItemStack::class.java, recipe.input)
        ingredients.setOutput(FluidStack::class.java, recipe.output)
    }
}