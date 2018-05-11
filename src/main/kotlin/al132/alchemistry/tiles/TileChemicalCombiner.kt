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
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by al132 on 1/22/2017.
 */
class TileChemicalCombiner : TileBase(), IGuiTile, ITickable, IEnergyTile, IItemTile {

    var currentRecipe: CombinerRecipe? = null
        private set
    var recipeIsLocked = false
    var progressTicks = 0
    var paused = false

    val clientRecipeTarget = ALTileStackHandler(1, this)


    companion object {
        val ENERGY_PER_TICK: Int = ConfigHandler.combinerEnergyPerTick ?: 250
        val TICKS_PER_PROCESS = ConfigHandler.combinerProcessingTicks ?: 20
    }

    init {
        initInventoryCapability(9, 1)
        initEnergyCapability(100000)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {

                if (currentRecipe == null) return super.insertItem(slot, stack, simulate)
                else if (currentRecipe!!.inputs[slot].areItemsEqual(stack)) {
                    return super.insertItem(slot, stack, simulate)
                }
                return stack
            }
        }
    }

    override fun update() {
        if (!getWorld().isRemote) {
            if (!recipeIsLocked) this.currentRecipe = CombinerRecipe.match(input)
            clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output) ?: ItemStack.EMPTY!!)
            if (!this.paused && canProcess()) process()
            this.markDirtyEvery(5)
        }
    }

    fun process() {
        this.energyCapability.extractEnergy(TileChemicalDissolver.ENERGY_PER_TICK, false)

        if (progressTicks < TICKS_PER_PROCESS) progressTicks++
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
                && (!recipeIsLocked || CombinerRecipe.match(input)?.output?.areItemStacksEqual(currentRecipe!!.output) ?: false)
                && (ItemStack.areItemsEqual(output[0], currentRecipe?.output) || output[0].isEmpty)
                && (currentRecipe!!.output.count + output[0].count <= 64)
                && energyCapability.energyStored >= ENERGY_PER_TICK
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.recipeIsLocked = compound.getBoolean("RecipeIsLocked")
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.paused = compound.getBoolean("Paused")
        if(this.recipeIsLocked){
            val tempItemHandler = ItemStackHandler(9)
            val recipeInputsList = compound.getTagList("RecipeInputs", Constants.NBT.TAG_COMPOUND)
            for(i in 0 until recipeInputsList.tagCount()){
                tempItemHandler.setStackInSlot(i,ItemStack(recipeInputsList.getCompoundTagAt(i)))
            }
            this.currentRecipe = CombinerRecipe.match(tempItemHandler)
            clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output) ?: ItemStack.EMPTY!!)
            this.markDirtyClient()
        }
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
        //TODO save recipe to NBT tag
        return super.writeToNBT(compound)
    }
}