package al132.alchemistry.tiles

import al132.alchemistry.recipes.AlloyRecipe
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.extensions.get
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

/**
 * Created by al132 on 6/25/2017.
 */

class TileAlloyFurnace : TileBase(), IGuiTile, IItemTile, ITickable {

    private var currentRecipe: AlloyRecipe? = null
    var progressTicks = 0

    init {
        initInventoryCapability(5, 1)
    }


    override fun update() {
        if (!world.isRemote) {
           // this.currentRecipe = ModRecipes.alloyRecipes.firstOrNull { AlloyRecipe.matches(input.toStackList()) }
            if (canProcess()) process()
            this.markDirtyEvery(5)
        }
    }

    fun canProcess(): Boolean {
        return currentRecipe != null
                && (output[0].isEmpty || output[0].isItemEqual(currentRecipe!!.output))
                && (output[0].count + currentRecipe!!.output.count) <= currentRecipe!!.output.maxStackSize

    }

    fun process() {

    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.progressTicks = compound.getInteger("ProgressTicks")
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("ProgressTicks", this.progressTicks)
        return super.writeToNBT(compound)
    }

}