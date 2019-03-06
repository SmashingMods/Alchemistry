package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.LiquifierRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alib.tiles.*
import al132.alib.utils.extensions.areItemsEqual
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileLiquifier : TileBase(), IGuiTile, ITickable, IItemTile, IFluidTile, IEnergyTile {

    companion object {
        val ENERGY_PER_TICK: Int = ConfigHandler.liquifierEnergyPerTick ?: 50
        var BASE_TICKS_PER_OPERATION = ConfigHandler.liquifierProcessingTicks ?: 100
    }

    val outputTank: FluidTank
    private var currentRecipe: LiquifierRecipe? = null
    var progressTicks = 0

    init {
        initInventoryCapability(1, 0)
        initEnergyCapability(100000)

        outputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return ModRecipes.liquifierRecipes.any { it.output.fluid == fluid?.fluid }
            }
        }

        outputTank.setTileEntity(this)
        outputTank.setCanFill(false)
        outputTank.setCanDrain(true)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (ModRecipes.liquifierRecipes.any { it.input.isItemEqual(stack) }) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }
        }
    }


    override fun update() {
        if (!world.isRemote) {
            if(!this.input[0].isEmpty) {
                this.currentRecipe = ModRecipes.liquifierRecipes.firstOrNull { it.input.areItemsEqual(this.input[0]) }
                if (canProcess()) process()
            }
            this.markDirtyClientEvery(5)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        val outputTankNBT = NBTTagCompound()
        this.outputTank.writeToNBT(outputTankNBT)
        compound.setTag("OutputTankNBT", outputTankNBT)
        compound.setInteger("ProgressTicks", progressTicks)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.outputTank.readFromNBT(compound.getCompoundTag("OutputTankNBT"))
        this.progressTicks = compound.getInteger("ProgressTicks")
    }

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(outputTank)

    fun canProcess(): Boolean {
        return currentRecipe != null
                && ((outputTank.fluid?.fluid == currentRecipe!!.output.fluid ?: false) || outputTank.fluid == null)
                && outputTank.capacity >= outputTank.fluidAmount + currentRecipe!!.output.amount
                && this.energyCapability.energyStored >= TileLiquifier.ENERGY_PER_TICK
                && input[0].count >= currentRecipe!!.input.count
    }

    fun process() {
        if (progressTicks < TileLiquifier.BASE_TICKS_PER_OPERATION) {
            progressTicks++
        } else {
            progressTicks = 0
            input[0].shrink(currentRecipe!!.input.count)
            outputTank.fillInternal(currentRecipe!!.output, true)//; .setOrIncrement(0, currentRecipe!!.output)
        }
        this.energyCapability.extractEnergy(TileLiquifier.ENERGY_PER_TICK, false)
    }
}