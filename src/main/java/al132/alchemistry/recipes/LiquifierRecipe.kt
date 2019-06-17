package al132.alchemistry.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * Created by al132 on 4/29/2017.
 */

data class LiquifierRecipe(val input: ItemStack, val output: FluidStack) {

    constructor(input: ItemStack, fluid: Fluid, fluidQuantity: Int) : this(input, FluidStack(fluid, fluidQuantity))
}