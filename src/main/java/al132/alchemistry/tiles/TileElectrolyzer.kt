package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.ElectrolyzerRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alib.tiles.*
import al132.alib.utils.extensions.containsItem
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 1/16/2017.
 */
class TileElectrolyzer : TileBase(), IGuiTile, ITickable, IFluidTile, IItemTile,
        IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.electrolyzerEnergyCapacity!!) {

    val inputTank: FluidTank
    var progressTicks = 0
    private var currentRecipe: ElectrolyzerRecipe? = null

    init {
        this.initInventoryCapability(1, 4)

        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return ModRecipes.electrolyzerRecipes.any { it.input.fluid == fluid?.fluid }
            }

            override fun onContentsChanged() {
                super.onContentsChanged()
                markDirtyGUI()
            }
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (ModRecipes.electrolyzerRecipes.any { it.electrolytes.containsItem(stack) }) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }

        }
    }

    override fun update() {
        if (!world.isRemote) {
            if(inputTank.fluidAmount > 0) {
                this.currentRecipe = ModRecipes.electrolyzerRecipes.firstOrNull {
                    (inputTank.fluid?.containsFluid(it.input) ?: false) && it.electrolytes.containsItem(input[0])
                }
                if (canProcess()) process()
                this.markDirtyGUIEvery(5)
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        val inputTankNBT = NBTTagCompound()
        this.inputTank.writeToNBT(inputTankNBT)
        compound.setTag("InputTankNBT", inputTankNBT)
        compound.setInteger("ProgressTicks", this.progressTicks)
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
                && input[0].count >= currentRecipe!!.electrolytes[0].count
                && this.energyStorage.energyStored >= ConfigHandler.electrolyzerEnergyPerTick!!
                && (0 until 4).all {
            val outputStack = output[it]
            val recipeStack = currentRecipe!!.outputs[it].copy()
            (outputStack.isEmpty || ItemStack.areItemsEqual(outputStack, recipeStack))
                    && outputStack.count + recipeStack.count <= recipeStack.maxStackSize
        }
    }

    fun process() {
        if (progressTicks < ConfigHandler.electrolyzerProcessingTicks!!) {
            progressTicks++
        } else {
            progressTicks = 0
            inputTank.drainInternal(currentRecipe!!.input.amount, true)
            if (world.rand.nextInt(100) < currentRecipe!!.electrolyteConsumptionChance) {
                input.decrementSlot(0, currentRecipe!!.electrolytes[0].count)
            }

            (0 until 4).forEach { output.setOrIncrement(it, currentRecipe!!.calculatedInSlot(it)) }

            this.energyStorage.extractEnergy(ConfigHandler.electrolyzerEnergyPerTick!!, false)
        }
    }
}