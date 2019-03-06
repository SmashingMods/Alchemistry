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

    companion object {
        var BASE_TICKS_PER_OPERATION = ConfigHandler.evaporatorProcessingTicks ?: 160
    }

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
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    override fun update() {
        if (!world.isRemote) {
            if(inputTank.fluidAmount > 0) {
                this.currentRecipe = ModRecipes.evaporatorRecipes.firstOrNull {
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
    }

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)

    fun canProcess(): Boolean {
        return currentRecipe != null
                && inputTank.fluidAmount >= currentRecipe!!.input.amount
                && output[0].count + currentRecipe!!.outputs[0].count <= currentRecipe!!.outputs[0].maxStackSize
    }

    fun calculateProcessingTime(): Int { //TODO more elaborate calculation?
        var temp = BASE_TICKS_PER_OPERATION
        if (!BiomeDictionary.hasType(world.getBiomeForCoordsBody(this.pos), BiomeDictionary.Type.DRY)) {// != BiomeDesert::class.java) {
            temp += (BASE_TICKS_PER_OPERATION * .5).toInt()
        }
        return temp
    }

    fun process() {
        if (progressTicks % 5 == 0) calculatedProcessingTime = calculateProcessingTime()

        if (progressTicks < calculatedProcessingTime) progressTicks++
        else {
            progressTicks = 0
            inputTank.drainInternal(currentRecipe!!.input.amount, true)
            output.setOrIncrement(0, currentRecipe!!.outputs[0])
        }
    }
}