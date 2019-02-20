package al132.alchemistry.recipes

import al132.alib.utils.Utils.areItemsEqualIgnoreMeta
import al132.alib.utils.extensions.toStackList
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.oredict.OreDictionary
import java.util.*

/**
 * Created by al132 on 1/22/2017.
 */

data class CombinerRecipe(val output: ItemStack, private val objsIn: List<Any?>) {

    val inputs: List<ItemStack>
        get() = inputsInternal.toList()

    private val inputsInternal = ArrayList<ItemStack>()

    init {

        val tempInputs = objsIn

        (0 until INPUT_COUNT).forEach { index ->
            val tempInput = tempInputs.getOrNull(index)

            when (tempInput) {
                is ItemStack -> inputsInternal.add(tempInput)
                is Item      -> inputsInternal.add(ItemStack(tempInput))
                is Block     -> inputsInternal.add(ItemStack(tempInput))
                is String    -> {
                } //TODO oredict input
                else         -> inputsInternal.add(ItemStack.EMPTY)
            }
        }
    }

    companion object {

        private const val INPUT_COUNT = 9

        fun match(handler: IItemHandler): CombinerRecipe? {
            if (handler.slots == INPUT_COUNT) {
                val inputStacks: ArrayList<ItemStack> = handler.toStackList()
                for (recipe in ModRecipes.combinerRecipes) {
                    var matchingStacks = 0
                    for ((index: Int, recipeStack: ItemStack) in recipe.inputs.withIndex()) {
                        val inputStack: ItemStack = inputStacks[index]

                        if (inputStack.isEmpty && recipeStack.isEmpty) matchingStacks++
                        else if (inputStack.isEmpty || recipeStack.isEmpty) continue
                        else if (areItemsEqualIgnoreMeta(inputStack, recipeStack)
                                && inputStack.count >= recipeStack.count
                                && (inputStack.itemDamage == recipeStack.itemDamage || recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                            matchingStacks++
                        }
                    }
                    if (matchingStacks == INPUT_COUNT) return recipe.copy()
                }
            }
            return null
        }
    }
}