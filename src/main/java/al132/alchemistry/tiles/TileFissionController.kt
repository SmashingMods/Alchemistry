package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.FissionControllerBlock
import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ModItems
import al132.alib.tiles.ALTileStackHandler
import al132.alib.tiles.IEnergyTile
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.extensions.get
import al132.alib.utils.extensions.toStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.EnergyStorage

/**
 * Created by al132 on 4/29/2017.
 */
class TileFissionController : TileBase(), IGuiTile, ITickable, IItemTile, IEnergyTile {

    var progressTicks = 0
    var recipeOutput1: ItemStack = ItemStack.EMPTY
    var recipeOutput2: ItemStack = ItemStack.EMPTY
    var isValidMultiblock: Boolean = false
    var checkMultiblockTicks: Int = 0

    init {
        initInventoryCapability(1, 2)
        initEnergyCapability(ConfigHandler.fissionEnergyCapacity!!)
    }


    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (stack.item == ModItems.elements && stack.metadata > 1) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }

            override fun onContentsChanged(slot: Int) {
                (tile as TileFissionController).refreshRecipe()
                super.onContentsChanged(slot)
            }
        }
    }

    fun refreshRecipe() {
        val meta = this.input[0].metadata
        if (meta != 0) {
            if (meta % 2 == 0) {
                if (ElementRegistry[meta / 2] != null) {
                    recipeOutput1 = ModItems.elements.toStack(quantity = 2, meta = meta / 2)
                    recipeOutput2 = ItemStack.EMPTY
                    return
                }
            } else {
                if (ElementRegistry[meta / 2] != null && ElementRegistry[(meta / 2) + 1] != null) {
                    recipeOutput1 = ModItems.elements.toStack(meta = (meta / 2) + 1)
                    recipeOutput2 = ModItems.elements.toStack(meta = meta / 2)
                    return
                }
            }
        }
        recipeOutput1 = ItemStack.EMPTY
        recipeOutput2 = ItemStack.EMPTY
    }

    override fun update() {
        checkMultiblockTicks++
        if (checkMultiblockTicks >= 20) {
            updateMultiblock()
            checkMultiblockTicks = 0
        }
        if (!world.isRemote) {
            if (canProcess()) process()
            this.markDirtyClientEvery(5)
        }
    }


    fun canProcess(): Boolean {
        return this.isValidMultiblock
                && !recipeOutput1.isEmpty
                && (ItemStack.areItemsEqual(output[0], recipeOutput1) || output[0].isEmpty)
                && (ItemStack.areItemsEqual(output[1], recipeOutput2) || output[1].isEmpty)
                && output[0].count + recipeOutput1.count <= recipeOutput1.maxStackSize
                && output[1].count + recipeOutput2.count <= recipeOutput1.maxStackSize
                && energyCapability.energyStored >= ConfigHandler.fissionEnergyPerTick!!

    }

    fun process() {
        if (progressTicks < ConfigHandler.fissionProcessingTicks!!) {
            progressTicks++
        } else {
            progressTicks = 0
            output.setOrIncrement(0, recipeOutput1.copy())
            if (!recipeOutput2.isEmpty) output.setOrIncrement(1, recipeOutput2.copy())
            input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyCapability.extractEnergy(ConfigHandler.fissionEnergyPerTick!!, false)
    }


    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("ProgressTicks", progressTicks)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.progressTicks = compound.getInteger("ProgressTicks")
        val energyStored = compound.getInteger("EnergyStored")
        energyCapability = EnergyStorage(ConfigHandler.fissionEnergyCapacity!!)
        energyCapability.receiveEnergy(energyStored, false)
        this.refreshRecipe()
    }

    private fun containsCasing(pos: BlockPos): Boolean = (this.world.getBlockState(pos).block == ModBlocks.fissionCasing)
    private fun containsCore(pos: BlockPos): Boolean = (this.world.getBlockState(pos).block == ModBlocks.fissionCore)
    private fun containsFissionPart(pos: BlockPos): Boolean {
        val block = this.world.getBlockState(pos).block
        return block == ModBlocks.fissionCasing || block == ModBlocks.fissionCore || block == ModBlocks.fissionController
    }

    fun updateMultiblock() {
        this.isValidMultiblock = validateMultiblock()
    }

    fun validateMultiblock(): Boolean {
        val multiblockDirection = (world.getBlockState(this.pos).getValue(FissionControllerBlock.FACING) as EnumFacing).opposite

        fun BlockPos.offsetUp(amt: Int = 1) = this.offset(EnumFacing.UP, amt)
        fun BlockPos.offsetLeft(amt: Int = 1) = this.offset(multiblockDirection.rotateY(), amt)
        fun BlockPos.offsetRight(amt: Int = 1) = this.offset(multiblockDirection.rotateY(), -1 * amt)
        fun BlockPos.offsetBack(amt: Int = 1) = this.offset(multiblockDirection, amt)
        fun BlockPos.offsetDown(amt: Int = 1) = this.offset(EnumFacing.DOWN, amt)

        val coreBottom = this.pos.offsetBack(3).offsetUp()
        val coreTop = coreBottom.offsetUp(2)
        val coreMatches = BlockPos.getAllInBox(coreBottom, coreTop).all(::containsCore)

        val insideCorner1 = this.pos.offsetUp().offsetLeft().offsetBack(2)
        val insideCorner2 = insideCorner1.offsetBack(2).offsetRight(2).offsetUp(2)
        val middleXZ = this.pos.offsetBack(3)
        val emptyInsideMatches = BlockPos.getAllInBox(insideCorner1, insideCorner2)
                .filterNot { it.x == middleXZ.x && it.z == middleXZ.z }
                .all(world::isAirBlock)

        //A cube of all blocks surrounding the fission multiblock, checking to ensure no other fission multiblocks are overlapping/sharing
        val outsideCorner1 = this.pos.offsetLeft(3).offsetDown()
        val outsideCorner2 = outsideCorner1.offsetRight(6).offsetUp(6).offsetBack(6)
        val borderingParts = BlockPos.getAllInBox(outsideCorner1, outsideCorner2).filter {
            var sharedAxes = 0
            if (it.x == outsideCorner1.x || it.x == outsideCorner2.x) sharedAxes++
            if (it.y == outsideCorner1.y || it.y == outsideCorner2.y) sharedAxes++
            if (it.z == outsideCorner1.z || it.z == outsideCorner2.z) sharedAxes++
            sharedAxes >= 1
        }.filterNot(this.pos::equals)
                .count(this::containsFissionPart)


        val casingCorner1 = this.pos.offsetLeft(2).offsetBack(1)
        val casingCorner2 = casingCorner1.offsetRight(4).offsetBack(4).offsetUp(4)
        val casingMatches = BlockPos.getAllInBox(casingCorner1, casingCorner2).filter {
            var sharedAxes = 0
            if (it.x == casingCorner1.x || it.x == casingCorner2.x) sharedAxes++
            if (it.y == casingCorner1.y || it.y == casingCorner2.y) sharedAxes++
            if (it.z == casingCorner1.z || it.z == casingCorner2.z) sharedAxes++
            sharedAxes >= 1
        }.all(::containsCasing)

        return casingMatches && coreMatches && emptyInsideMatches && (borderingParts == 0)
    }


    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (this.isValidMultiblock) return super.hasCapability(capability, facing)
        else return false
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (this.isValidMultiblock) return super.getCapability(capability, facing)
        else return null
    }
}