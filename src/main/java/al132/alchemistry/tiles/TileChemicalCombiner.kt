package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.items.ModItems
import al132.alchemistry.recipes.CombinerRecipe
import al132.alib.tiles.*
import al132.alib.utils.extensions.areItemStacksEqual
import al132.alib.utils.extensions.areItemsEqual
import al132.alib.utils.extensions.get
import net.darkhax.gamestages.GameStageHelper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ITickable
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by al132 on 1/22/2017.
 */
class TileChemicalCombiner : TileBase(), IGuiTile, ITickable, IItemTile,
        IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.combinerEnergyCapacity!!) {

    var currentRecipe: CombinerRecipe? = null
    var recipeIsLocked = false
    var progressTicks = 0
    var paused = false
    val clientRecipeTarget: ALTileStackHandler
    var owner: String = ""

    init {
        initInventoryCapability(9, 1)
        clientRecipeTarget = object : ALTileStackHandler(1, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
        }
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (!recipeIsLocked) return super.insertItem(slot, stack, simulate)
                else if (recipeIsLocked && (currentRecipe?.inputs?.get(slot)?.areItemsEqual(stack) == true)) {
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
            if (recipeIsLocked) clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output?.copy()) ?: ItemStack.EMPTY)
            if (!this.paused && canProcess()) process()
            this.markDirtyGUIEvery(5)
        }
    }

    fun process() {
        this.energyStorage.extractEnergy(ConfigHandler.combinerEnergyPerTick!!, false)

        if (progressTicks < ConfigHandler.combinerProcessingTicks!!) progressTicks++
        else {
            progressTicks = 0
            currentRecipe?.let { output.setOrIncrement(0, it.output.copy()) }
            currentRecipe?.inputs?.forEachIndexed { index, stack ->
                if (!stack.isEmpty) {
                    (input.decrementSlot(index, stack.count))
                }
                if (input.getStackInSlot(index).item == ModItems.slotFiller) {
                    input.decrementSlot(index, 1)
                }
            }
        }
    }

    fun hasCurrentRecipeStage(): Boolean {
        if(Loader.isModLoaded("gamestages")) {
            val playerList = FMLCommonHandler.instance().minecraftServerInstance.playerList
            val playerOwner: EntityPlayerMP = playerList.getPlayerByUsername(owner) ?: return false
            return GameStageHelper.hasStage(playerOwner, currentRecipe?.gamestage)
        } else return true
    }

    fun canProcess(): Boolean {
        return currentRecipe != null
                && (currentRecipe!!.gamestage == "" || hasCurrentRecipeStage())
                && energyStorage.energyStored >= ConfigHandler.combinerEnergyPerTick!! //has enough energy
                && (currentRecipe!!.output.count + output[0].count <= currentRecipe!!.output.maxStackSize) //output quantities can stack
                && (ItemStack.areItemsEqual(output[0], currentRecipe!!.output) || output[0].isEmpty) //output item types can stack
                && currentRecipe!!.matchesHandlerStacks(this.input)
                && (!recipeIsLocked || CombinerRecipe.matchInputs(input)?.output?.areItemStacksEqual(currentRecipe!!.output) ?: false)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.recipeIsLocked = compound.getBoolean("RecipeIsLocked")
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.paused = compound.getBoolean("Paused")
        this.owner = compound.getString("Owner")

        if (this.recipeIsLocked) {
            val tempItemHandler = ItemStackHandler(9)
            val recipeInputsList = compound.getTagList("RecipeInputs", Constants.NBT.TAG_COMPOUND)
            for (i in 0 until recipeInputsList.tagCount()) {
                tempItemHandler.setStackInSlot(i, ItemStack(recipeInputsList.getCompoundTagAt(i)))
            }
            val recipeTarget = ItemStack(compound.getCompoundTag("RecipeTarget"))
            this.currentRecipe = CombinerRecipe.matchOutput(recipeTarget)
            clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output?.copy()) ?: ItemStack.EMPTY!!)
        } else {
            this.updateRecipe()
            clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setBoolean("RecipeIsLocked", this.recipeIsLocked)
        compound.setInteger("ProgressTicks", this.progressTicks)
        compound.setBoolean("Paused", this.paused)
        compound.setString("Owner", this.owner)
        if (this.recipeIsLocked && this.currentRecipe != null) {
            val recipeInputs = NBTTagList()
            for (i in this.currentRecipe!!.inputs.indices) {
                val recipeInputEntry = NBTTagCompound()
                val tempStack = this.currentRecipe!!.inputs[i].copy()
                tempStack.writeToNBT(recipeInputEntry)
                recipeInputs.appendTag(recipeInputEntry)
            }
            compound.setTag("RecipeInputs", recipeInputs)
        }
        compound.setTag("RecipeTarget", clientRecipeTarget[0].serializeNBT())
        return super.writeToNBT(compound)
    }
}