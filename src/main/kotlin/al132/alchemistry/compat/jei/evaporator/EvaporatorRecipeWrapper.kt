package al132.alchemistry.compat.jei.evaporator

import al132.alchemistry.compat.jei.AlchemistryRecipeWrapper
import al132.alchemistry.recipes.EvaporatorRecipe
import mezz.jei.api.ingredients.IIngredients
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class EvaporatorRecipeWrapper(recipe: EvaporatorRecipe) : AlchemistryRecipeWrapper<EvaporatorRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(FluidStack::class.java, recipe.input)
        ingredients.setOutputs(ItemStack::class.java, recipe.outputs)
    }
}