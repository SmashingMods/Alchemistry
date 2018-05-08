package al132.alchemistry.recipes

import al132.alib.utils.Utils.getOres
import al132.alib.utils.extensions.equalsIgnoreMeta
import al132.alib.utils.extensions.toImmutable
import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by al132 on 1/20/2017.
 */
data class DissolverRecipe(var dictName: String = "",
                           var stack: ItemStack? = ItemStack.EMPTY,
                           var inputQuantity: Int = 1,
                           var reversible: Boolean = false,
                           var _outputs: ProbabilitySet? = null) {


    inline val inputs: ImmutableList<ItemStack>
        get(): ImmutableList<ItemStack> {
            val temp = ArrayList<ItemStack>()
            stack?.let { if (!(stack!!.isEmpty)) temp.add(stack!!) }
            if (dictName != "") temp.addAll(getOres(dictName, inputQuantity))
            return temp.toImmutable()
        }

    inline fun output(crossinline init: ProbabilitySetDSL.() -> Unit) {
        this._outputs = ProbabilitySetDSL().apply { init() }.build()
    }

    inline val outputs: ProbabilitySet
        get() = _outputs!!.copy()


    companion object {

        fun match(input: ItemStack, quantitySensitive: Boolean): DissolverRecipe? {
            for (recipe in ModRecipes.dissolverRecipes) {
                for (recipeStack in recipe.inputs) {
                    if (recipeStack.equalsIgnoreMeta(input)
                            && (input.itemDamage == recipeStack.itemDamage
                            || recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                        if (quantitySensitive && input.count >= recipeStack.count) return recipe.copy()
                        else if (!quantitySensitive) return recipe.copy()
                    }
                }
            }
            return null
        }
    }
}