package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.AtomizerRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alib.tiles.IEnergyTile
import al132.alib.tiles.IFluidTile
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileAtomizer : TileBase(), IGuiTile, ITickable, IItemTile, IFluidTile, IEnergyTile {

    val inputTank: FluidTank
    private var currentRecipe: AtomizerRecipe? = null
    var progressTicks = 0

    init {
        initInventoryCapability(0, 1)
        initEnergyCapability(ConfigHandler.atomizerEnergyCapacity!!)

        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return ModRecipes.atomizerRecipes.any { it.input.fluid == fluid?.fluid }
            }
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    override fun update() {
        if (!world.isRemote) {
            if(inputTank.fluidAmount > 0) {
                this.currentRecipe = ModRecipes.atomizerRecipes.firstOrNull {
                    inputTank.fluid?.containsFluid(it.input) ?: false
                }
                if (canProcess()) process()
            }
            this.markDirtyClientEvery(5)
        }
    }


    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        val inputTankNBT = NBTTagCompound()
        this.inputTank.writeToNBT(inputTankNBT)
        compound.setTag("InputTankNBT", inputTankNBT)
        compound.setInteger("ProgressTicks", progressTicks)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.inputTank.readFromNBT(compound.getCompoundTag("InputTankNBT"))
        this.progressTicks = compound.getInteger("ProgressTicks")
        val energyStored = compound.getInteger("EnergyStored")
        energyCapability = EnergyStorage(ConfigHandler.atomizerEnergyCapacity!!)
        energyCapability.receiveEnergy(energyStored, false)
    }

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)

    fun canProcess(): Boolean {
        return currentRecipe != null
                && (ItemStack.areItemsEqual(output[0], currentRecipe!!.output) || output[0].isEmpty)
                && inputTank.fluidAmount >= currentRecipe!!.input.amount
                && output[0].count + currentRecipe!!.output.count <= currentRecipe!!.output.maxStackSize
                && energyCapability.energyStored >= ConfigHandler.atomizerEnergyPerTick!!

    }

    fun process() {
        if (progressTicks < ConfigHandler.atomizerProcessingTicks!!) {
            progressTicks++
        } else {
            progressTicks = 0
            inputTank.drainInternal(currentRecipe!!.input.amount, true)

            output.setOrIncrement(0, currentRecipe!!.output)
        }
        this.energyCapability.extractEnergy(ConfigHandler.atomizerEnergyPerTick!!, false)
    }
}