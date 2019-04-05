package al132.alchemistry.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * Created by al132 on 4/29/2017.
 */


data class LiquifierRecipe(private val inputStack: ItemStack, private val outputFluid: FluidStack) {

    constructor(input: ItemStack, fluid: Fluid, fluidQuantity: Int) : this(input, FluidStack(fluid, fluidQuantity))

    val input: ItemStack
        get() = this.inputStack.copy()

    val output: FluidStack
        get() = outputFluid.copy()
}