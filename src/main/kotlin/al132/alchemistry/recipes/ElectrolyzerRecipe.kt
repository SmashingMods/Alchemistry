package al132.alchemistry.recipes

import al132.alib.utils.extensions.copy
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.*

/**
 * Created by al132 on 1/20/2017.
 */
data class ElectrolyzerRecipe(private val inputFluid: FluidStack,
                              private val electrolytesInternal: List<ItemStack>,
                              val electrolyteConsumptionChanceInternal: Int,
                              private val outputOne: ItemStack,
                              private val outputTwo: ItemStack,
                              private val outputThree: ItemStack = ItemStack.EMPTY,
                              val output3Probability: Int = 0,
                              private val outputFour: ItemStack = ItemStack.EMPTY,
                              val output4Probability: Int = 0) {

    constructor(fluid: Fluid, fluidQuantity: Int, electrolytes: List<ItemStack>, elecConsumption: Int,
                outputOne: ItemStack, outputTwo: ItemStack)
            : this(FluidStack(fluid, fluidQuantity), electrolytes, elecConsumption, outputOne, outputTwo)

    val input: FluidStack
        get() = this.inputFluid.copy()

    val electrolytes: List<ItemStack>
        get() = electrolytesInternal.copy()

    val electrolyteConsumptionChance: Int
        get() = this.electrolyteConsumptionChanceInternal

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
}