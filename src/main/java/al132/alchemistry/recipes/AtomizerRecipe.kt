package al132.alchemistry.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * Created by al132 on 4/29/2017.
 */


data class AtomizerRecipe(val reversible: Boolean = false, val input: FluidStack, val output: ItemStack) {

    constructor(reversible: Boolean = false, fluid: Fluid, fluidQuantity: Int, output: ItemStack)
            : this(reversible, FluidStack(fluid, fluidQuantity), output)
}