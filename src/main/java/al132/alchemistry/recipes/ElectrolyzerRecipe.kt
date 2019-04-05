package al132.alchemistry.recipes

import al132.alib.utils.extensions.areItemStacksEqual
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import java.util.*

/**
 * Created by al132 on 1/20/2017.
 */
data class ElectrolyzerRecipe(private val inputFluid: FluidStack,
                              private val electrolyteInternal: ItemStack = ItemStack.EMPTY,
                              private val electrolyteOre: String = "",
                              val electrolyteConsumptionChanceInternal: Int,
                              private val outputOne: ItemStack,
                              private val outputTwo: ItemStack,
                              private val outputThree: ItemStack = ItemStack.EMPTY,
                              val output3Probability: Int = 50,
                              private val outputFour: ItemStack = ItemStack.EMPTY,
                              val output4Probability: Int = 50) {

    constructor(fluid: Fluid,
                fluidQuantity: Int,
                electrolyte: ItemStack,
                elecConsumption: Int,
                outputOne: ItemStack,
                outputTwo: ItemStack,
                outputThree: ItemStack = ItemStack.EMPTY,
                output3Probability: Int = 50,
                outputFour: ItemStack = ItemStack.EMPTY,
                output4Probability: Int = 50)
            : this(inputFluid = FluidStack(fluid, fluidQuantity),
            electrolyteInternal = electrolyte,
            electrolyteConsumptionChanceInternal = elecConsumption,
            outputOne = outputOne,
            outputTwo = outputTwo,
            outputThree = outputThree,
            output3Probability = output3Probability,
            outputFour = outputFour,
            output4Probability = output4Probability)

    constructor(fluid: Fluid,
                fluidQuantity: Int,
                electrolyte: String,
                elecConsumption: Int,
                outputOne: ItemStack,
                outputTwo: ItemStack,
                outputThree: ItemStack = ItemStack.EMPTY,
                output3Probability: Int = 50,
                outputFour: ItemStack = ItemStack.EMPTY,
                output4Probability: Int = 50)
            : this(inputFluid = FluidStack(fluid, fluidQuantity),
            electrolyteOre = electrolyte,
            electrolyteConsumptionChanceInternal = elecConsumption,
            outputOne = outputOne,
            outputTwo = outputTwo,
            outputThree = outputThree,
            output3Probability = output3Probability,
            outputFour = outputFour,
            output4Probability = output4Probability)

    val input: FluidStack
        get() = this.inputFluid.copy()

    val electrolytes: List<ItemStack>
        get() {
            return when {
                !electrolyteInternal.isEmpty                   -> listOf(electrolyteInternal.copy())
                OreDictionary.doesOreNameExist(electrolyteOre) -> OreDictionary.getOres(electrolyteOre)
                else                                           -> listOf()
            }
        }

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

    fun matchesElectrolyte(target: String) = (target == this.electrolyteOre)
    fun matchesElectrolyte(target: ItemStack): Boolean = (target.areItemStacksEqual(this.electrolyteInternal))


}