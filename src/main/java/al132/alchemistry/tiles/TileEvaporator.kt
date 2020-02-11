package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.EvaporatorRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alib.tiles.IFluidTile
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.extensions.get
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileEvaporator : TileBase(), IGuiTile, ITickable, IItemTile, IFluidTile {

    val inputTank: FluidTank
    private var currentRecipe: EvaporatorRecipe? = null
    var progressTicks = 0
    var calculatedProcessingTime = 0

    init {
        initInventoryCapability(0, 1)

        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return ModRecipes.evaporatorRecipes.any { it.input.fluid == fluid?.fluid }
            }

            override fun onContentsChanged() {
                super.onContentsChanged()
                updateRecipe()
                markDirtyClient()
            }
        }
        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }


    fun updateRecipe() {
        val inputStack = this.inputTank.fluid
        if ((inputStack != null) && (currentRecipe == null || currentRecipe!!.input.fluid == inputStack.fluid)){
            this.currentRecipe = ModRecipes.evaporatorRecipes.firstOrNull { it.input.fluid == inputStack.fluid }
        }
        if (inputStack == null) currentRecipe = null
    }

    override fun update() {
        if (!world.isRemote) {
            if (inputTank.fluidAmount > 0) {
                this.currentRecipe = ModRecipes.evaporatorRecipes.firstOrNull {
                    inputTank.fluid?.containsFluid(it.input) ?: false
                }
                if (canProcess()) process()
                markDirtyGUIEvery(5)
            }
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
            return inputTank.fluidAmount >= currentRecipe!!.input.amount
                    && (inputTank.fluid == null || inputTank.fluid!!.fluid == currentRecipe!!.input.fluid)
                    && (output[0].isEmpty || output[0].item == currentRecipe!!.output.item)
                    && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
        } else return false;
    }

    fun calculateProcessingTime(): Int { //TODO more elaborate calculation?
        var temp = ConfigHandler.evaporatorProcessingTicks!!
        if (!BiomeDictionary.hasType(world.getBiomeForCoordsBody(this.pos), BiomeDictionary.Type.DRY)) {// != BiomeDesert::class.java) {
            temp += (ConfigHandler.evaporatorProcessingTicks!! * .5).toInt()
        }
        return temp
    }

    fun process() {
        if (progressTicks % 5 == 0) calculatedProcessingTime = calculateProcessingTime()

        if (progressTicks < calculatedProcessingTime) progressTicks++
        else {
            progressTicks = 0
            output.setOrIncrement(0, currentRecipe!!.output.copy())
            inputTank.drainInternal(currentRecipe!!.input.amount, true)
        }
    }
}