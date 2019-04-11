package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.CombinerRecipe
import al132.alib.tiles.ALTileStackHandler
import al132.alib.tiles.IEnergyTile
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.extensions.areItemStacksEqual
import al132.alib.utils.extensions.areItemsEqual
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ITickable
import net.minecraftforge.common.util.Constants
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by al132 on 1/22/2017.
 */
class TileChemicalCombiner : TileBase(), IGuiTile, ITickable, IEnergyTile, IItemTile {

    var currentRecipe: CombinerRecipe? = null
    var recipeIsLocked = false
    var progressTicks = 0
    var paused = false

    val clientRecipeTarget = ALTileStackHandler(1, this)

    init {
        initInventoryCapability(9, 1)
        initEnergyCapability(ConfigHandler.combinerEnergyCapacity!!)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (!recipeIsLocked) return super.insertItem(slot, stack, simulate)
                else if (recipeIsLocked && (currentRecipe?.inputs?.get(slot)?.areItemsEqual(stack) ?: false)) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }

            override fun onContentsChanged(slot: Int) {
                if (!recipeIsLocked) updateRecipe()
            }
        }
    }

    fun updateRecipe() {
        currentRecipe = CombinerRecipe.matchInputs(this.input)
    }

    override fun update() {
        if (!getWorld().isRemote) {
            if (recipeIsLocked) clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output) ?: ItemStack.EMPTY)
            if (!this.paused && canProcess()) process()
            this.markDirtyClientEvery(5)
        }
    }

    fun process() {
        this.energyCapability.extractEnergy(ConfigHandler.combinerEnergyPerTick!!, false)

        if (progressTicks < ConfigHandler.combinerProcessingTicks!!) progressTicks++
        else {
            progressTicks = 0
            currentRecipe?.let { output.setOrIncrement(0, it.output.copy()) }
            for ((index, stack) in currentRecipe!!.inputs.withIndex()) {
                if (!stack.isEmpty) {
                    (input.decrementSlot(index, stack.count))
                }
            }
        }
    }

    fun canProcess(): Boolean {
        return currentRecipe != null
                && currentRecipe!!.matchesHandlerStacks(this.input)
                //&& recipeIsLocked
                && (!recipeIsLocked || CombinerRecipe.matchInputs(input)?.output?.areItemStacksEqual(currentRecipe!!.output) ?: false)
                && (ItemStack.areItemsEqual(output[0], currentRecipe?.output) || output[0].isEmpty) //output item types can stack
                && (currentRecipe!!.output.count + output[0].count <= currentRecipe!!.output.maxStackSize) //output quantities can stack
                && energyCapability.energyStored >= ConfigHandler.combinerEnergyPerTick!! //has enough energy
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.recipeIsLocked = compound.getBoolean("RecipeIsLocked")
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.paused = compound.getBoolean("Paused")
        if (this.recipeIsLocked) {
            val tempItemHandler = ItemStackHandler(9)
            val recipeInputsList = compound.getTagList("RecipeInputs", Constants.NBT.TAG_COMPOUND)
            for (i in 0 until recipeInputsList.tagCount()) {
                tempItemHandler.setStackInSlot(i, ItemStack(recipeInputsList.getCompoundTagAt(i)))
            }
            val recipeTarget = ItemStack(compound.getCompoundTag("RecipeTarget"))
            this.currentRecipe = CombinerRecipe.matchOutput(recipeTarget)
            clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output) ?: ItemStack.EMPTY!!)
        } else {
            this.currentRecipe = null
            clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY)
        }

        val energyStored = compound.getInteger("EnergyStored")
        energyCapability = EnergyStorage(ConfigHandler.combinerEnergyCapacity!!)
        energyCapability.receiveEnergy(energyStored, false)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setBoolean("RecipeIsLocked", this.recipeIsLocked)
        compound.setInteger("ProgressTicks", this.progressTicks)
        compound.setBoolean("Paused", this.paused)
        if (this.recipeIsLocked && this.currentRecipe != null) {
            val recipeInputs = NBTTagList()
            for (i in this.currentRecipe!!.inputs.indices) {
                val recipeInputEntry = NBTTagCompound()
                val tempStack = this.currentRecipe!!.inputs[i]
                tempStack.writeToNBT(recipeInputEntry)
                recipeInputs.appendTag(recipeInputEntry)
            }
            compound.setTag("RecipeInputs", recipeInputs)
        }
        compound.setTag("RecipeTarget", clientRecipeTarget[0].serializeNBT())
        return super.writeToNBT(compound)
    }
}