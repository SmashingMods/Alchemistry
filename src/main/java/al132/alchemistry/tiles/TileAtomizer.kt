package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.AtomizerRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alib.tiles.*
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
class TileAtomizer : TileBase(), IGuiTile, ITickable, IItemTile, IFluidTile,
        IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.atomizerEnergyCapacity!!) {

    val inputTank: FluidTank
    private var currentRecipe: AtomizerRecipe? = null
    var progressTicks = 0

    init {
        initInventoryCapability(0, 1)
        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                if (this.fluid == null) return true
                else return this.fluid?.fluid == fluid?.fluid
            }

            override fun onContentsChanged() {
                updateRecipe()
                markDirtyGUI()
            }
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    fun updateRecipe() {
        if (inputTank.fluid != null &&
                (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.output, output.getStackInSlot(0)))) {
            currentRecipe = ModRecipes.atomizerRecipes.firstOrNull { it.input.fluid == inputTank.fluid?.fluid }
        }
        if (inputTank.fluid == null) currentRecipe = null
    }

    override fun update() {
        if (!world.isRemote) {
            if (inputTank.fluidAmount > 0) {
                if (canProcess()) process()
            }
            markDirtyGUIEvery(5)
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
        updateRecipe()
    }

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)

    fun canProcess(): Boolean {
        if (currentRecipe != null) {
            val recipeOutput = currentRecipe!!.output
            return energyStorage.energyStored >= ConfigHandler.atomizerEnergyPerTick!!
                    && inputTank.fluidAmount >= currentRecipe!!.input.amount
                    && (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
                    && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
        } else return false;
    }

    fun process() {
        if (progressTicks < ConfigHandler.atomizerProcessingTicks!!) {
            progressTicks++
        } else {
            progressTicks = 0
            output.setOrIncrement(0, currentRecipe!!.output.copy())
            inputTank.drainInternal(currentRecipe!!.input.amount, true)
        }
        this.energyStorage.extractEnergy(ConfigHandler.atomizerEnergyPerTick!!, false)
    }
}