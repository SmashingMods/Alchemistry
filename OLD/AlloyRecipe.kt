package al132.alchemistry.recipes

import net.minecraft.item.ItemStack

/**
 * Created by al132 on 6/25/2017.
 */

data class AlloyRecipe(private val _output: ItemStack, private val _inputs: List<ArrayList<ItemStack>>) {

    val output: ItemStack
        get() = _output.copy()

    val inputs: List<ArrayList<ItemStack>>
        get() = ArrayList(_inputs)

    val allPossibleInputs: List<ItemStack>
        get() = _inputs.flatten()

    fun match(other: List<ItemStack>): AlloyRecipe? {
        var matches = 0

        /*this.inputs.forEach { ingredientList ->
            ingredientList.forEach { ingredientStack ->
                if(Utils.listContainsItemStack()
            }
        }*/
        //TODO
        return null
    }

    fun matches(input: List<ItemStack>) = (match(input) != null)
}