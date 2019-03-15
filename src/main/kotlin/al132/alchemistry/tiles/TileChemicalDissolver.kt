package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.DissolverRecipe
import al132.alib.tiles.ALTileStackHandler
import al132.alib.tiles.IEnergyTile
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.Utils.canStacksMerge
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ITickable
import net.minecraftforge.common.util.Constants
import java.util.*

/**
 * Created by al132 on 1/16/2017.
 */
class TileChemicalDissolver : TileBase(), IGuiTile, ITickable, IEnergyTile, IItemTile {

    companion object {
        var ENERGY_PER_TICK: Int = ConfigHandler.dissolverEnergyPerTick ?: 100
    }

    private var outputSuccessful = true
    var outputThisTick: ItemStack = ItemStack.EMPTY
    var currentRecipe: DissolverRecipe? = null
    private var outputBuffer: MutableList<ItemStack> = ArrayList()


    init {
        this.initInventoryCapability(1, 10)
        this.initEnergyCapability(100000)
    }

    override fun initInventoryInputCapability() {

        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (DissolverRecipe.match(stack, false) != null) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }
        }
    }

    override fun update() {
        if (!getWorld().isRemote) {
            if (!input[0].isEmpty || outputBuffer.isNotEmpty()) {
                this.currentRecipe = DissolverRecipe.match(input[0], true)
                if (canProcess()) process()
            }
            this.markDirtyClientEvery(5)
        }
    }

    fun canProcess(): Boolean {
        return (currentRecipe != null || !outputBuffer.isEmpty())
                && energyCapability.energyStored >= ENERGY_PER_TICK
    }


    //tries to output a stack with getCount of one from the current recipe output buffer each tick
    fun process() {
        //if no output buffer, set the buffer to recipe outputs
        if (outputBuffer.isEmpty()) {
            outputBuffer = currentRecipe!!.outputs.calculateOutput().toMutableList()
            input.decrementSlot(0, currentRecipe!!.inputs[0].count)
        }

        //If output didn't happen or didn't fail last tick, queue up next output single stack
        if (outputSuccessful) {
            if (outputBuffer.size > 0) outputThisTick = outputBuffer[0].splitStack(1)
            else outputThisTick = ItemStack.EMPTY

            if (outputBuffer.size > 0 && outputBuffer[0].isEmpty) outputBuffer.removeAt(0)
            outputSuccessful = false
        }

        //Try to stack output with existing stacks in output, if possible
        for (i in 0 until output.slots) {
            if (canStacksMerge(outputThisTick, output[i], stacksCanbeEmpty = false)) {
                output.setOrIncrement(i, outputThisTick)
                outputSuccessful = true
                break
            }
        }
        //Otherwise try the empty stacks
        if (!outputSuccessful) {
            for (i in 0 until output.slots) {
                if (canStacksMerge(outputThisTick, output[i], stacksCanbeEmpty = true)) {
                    output.setOrIncrement(i, outputThisTick)
                    outputSuccessful = true
                    break
                }
            }
        }

        //consume energy and single stack if successful, won't be designated as such until there's a "hit" above
        if (outputSuccessful) {
            this.energyCapability.extractEnergy(ENERGY_PER_TICK, false)
            outputThisTick = ItemStack.EMPTY
        }
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.outputSuccessful = compound.getBoolean("OutputSuccessful")

        val outputBufferList = compound.getTagList("OutputBuffer", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until outputBufferList.tagCount()) {
            outputBuffer.add(ItemStack(outputBufferList.getCompoundTagAt(i)))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setBoolean("OutputSuccessful", this.outputSuccessful)

        val outputBufferList = NBTTagList()
        for (i in outputBuffer.indices) {
            val outputBufferEntry = NBTTagCompound()
            val tempStack = outputBuffer[i]

            tempStack.writeToNBT(outputBufferEntry)
            outputBufferList.appendTag(outputBufferEntry)
        }
        compound.setTag("OutputBuffer", outputBufferList)
        return compound
    }
}