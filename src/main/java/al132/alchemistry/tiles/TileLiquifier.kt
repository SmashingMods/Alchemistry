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
class TileLiquifier : TileBase(), IGuiTile, ITickable, IItemTile, IFluidTile,
        IEnergyTile by EnergyTileImpl(ConfigHandler.liquifierEnergyCapacity!!) {

    val outputTank: FluidTank
    private var currentRecipe: LiquifierRecipe? = null
    var progressTicks = 0

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(outputTank)

    init {
        initInventoryCapability(1, 0)
        outputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return ModRecipes.liquifierRecipes.any { it.output.fluid == fluid?.fluid }
            }

            override fun onContentsChanged() {
                super.onContentsChanged()
                markDirtyGUI()
                updateRecipe()
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

            override fun onContentsChanged(slot: Int) {
                updateRecipe()
                markDirtyGUI()
            }
        }
    }

    fun updateRecipe() {
        val inputStack = this.input.getStackInSlot(0)
        if (!inputStack.isEmpty && (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.input, inputStack))) {
            this.currentRecipe = ModRecipes.liquifierRecipes.firstOrNull { it.input.areItemsEqual(inputStack) }
        }
        if (inputStack.isEmpty) currentRecipe = null
    }


    override fun update() {
        if (!world.isRemote) {
            if (!this.input[0].isEmpty) {
                if (canProcess()) process()
            }
            this.markDirtyGUIEvery(5)
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
        updateRecipe()
    }

    fun canProcess(): Boolean {
        if(currentRecipe != null) {
            val recipeOutput = currentRecipe!!.output
            return outputTank.capacity >= outputTank.fluidAmount + recipeOutput.amount
                    && this.energyStorage.energyStored >= ConfigHandler.liquifierEnergyPerTick!!
                    && input[0].count >= currentRecipe!!.input.count
                    && ((outputTank.fluid?.fluid == recipeOutput.fluid ?: false) || outputTank.fluid == null)
        }else return false;
    }

    fun process() {
        if (progressTicks < ConfigHandler.liquifierProcessingTicks!!) {
            progressTicks++
        } else {
            progressTicks = 0
            outputTank.fillInternal(currentRecipe!!.output.copy(), true)//; .setOrIncrement(0, currentRecipe!!.output)
            input[0].shrink(currentRecipe!!.input.count)
        }
        this.energyStorage.extractEnergy(ConfigHandler.liquifierEnergyPerTick!!, false)
    }
}