package al132.alchemistry.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * Created by al132 on 4/29/2017.
 */


data class AtomizerRecipe(private val inputFluid: FluidStack, private val outputOne: ItemStack) {

    constructor(fluid: Fluid, fluidQuantity: Int, output: ItemStack) : this(FluidStack(fluid, fluidQuantity), output)

    val input: FluidStack
        get() = this.inputFluid.copy()

    val output: ItemStack
        get():ItemStack = outputOne.copy()
}