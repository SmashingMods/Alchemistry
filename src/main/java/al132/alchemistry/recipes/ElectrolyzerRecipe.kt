package al132.alchemistry.recipes

import al132.alib.utils.extensions.areItemStacksEqual
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack
import java.util.*

/**
 * Created by al132 on 1/20/2017.
 */
data class ElectrolyzerRecipe(private val inputFluid: FluidStack,
                              private val _electrolyte: Ingredient,
                              val electrolyteConsumptionChance: Int,
                              private val outputOne: ItemStack,
                              private val outputTwo: ItemStack,
                              private val outputThree: ItemStack = ItemStack.EMPTY,
                              val output3Probability: Int = 50,
                              private val outputFour: ItemStack = ItemStack.EMPTY,
                              val output4Probability: Int = 50) {

    val input: FluidStack
        get() = this.inputFluid.copy()

    val electrolytes: List<ItemStack>
        get() = _electrolyte.matchingStacks.toList()

    val outputs: List<ItemStack>
        get():List<ItemStack> = arrayListOf(outputOne.copy(), outputTwo.copy(), outputThree.copy(), outputFour.copy())

    fun calculatedInSlot(index: Int): ItemStack {
        val random = Random()
        when (index) {
            0 -> return outputOne.copy()
            1 -> return outputTwo.copy()
            2 -> if (random.nextInt(100) <= output3Probability) return outputThree.copy()
            3 -> if (random.nextInt(100) <= output4Probability) return outputFour.copy()
        }
        return ItemStack.EMPTY
    }

    fun matchesElectrolyte(target: ItemStack): Boolean = this._electrolyte.matchingStacks.any { it.areItemStacksEqual(target) }
}