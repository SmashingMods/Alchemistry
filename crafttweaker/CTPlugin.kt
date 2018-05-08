package al132.alchemistry.compat.crafttweaker

import al132.alchemistry.compat.crafttweaker.utils.InputHelper
import minetweaker.MineTweakerAPI
import minetweaker.api.item.IItemStack
import minetweaker.api.liquid.ILiquidStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

/**
 * Created by al132 on 4/28/2017.
 */

fun IItemStack.toStack(): ItemStack = InputHelper.toStack(this)

fun ILiquidStack.toFluid(): FluidStack = InputHelper.toFluid(this)

object CTPlugin {
    fun init() {
        MineTweakerAPI.registerClass(CTElectrolyzer::class.java)
    }
}