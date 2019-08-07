package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.FusionControllerBlock
import al132.alchemistry.blocks.FusionControllerBlock.Companion.STATUS
import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.blocks.PropertyPowerStatus
import al132.alchemistry.chemistry.ChemicalElement
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ModItems
import al132.alib.tiles.*
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by al132 on 4/29/2017.
 */
class TileFusionController : TileBase(), IGuiTile, ITickable, IItemTile,
        IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.fusionEnergyCapacity!!) {

    var progressTicks = 0
    var recipeOutput: ItemStack = ItemStack.EMPTY
    var isValidMultiblock: Boolean = false
    var checkMultiblockTicks: Int = 0

    init {
        initInventoryCapability(2, 1)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (stack.item == ModItems.elements) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }

            override fun onContentsChanged(slot: Int) {
                (tile as TileFusionController).refreshRecipe()
                super.onContentsChanged(slot)
            }
        }
    }

    fun refreshRecipe() {
        val meta1 = this.input[0].metadata
        val meta2 = this.input[1].metadata
        val outputElement: ChemicalElement? = ElementRegistry[meta1 + meta2]
        if (outputElement != null) recipeOutput = outputElement.toItemStack(1)
        else recipeOutput = ItemStack.EMPTY
    }

    override fun update() {

        if (!world.isRemote) {
            checkMultiblockTicks++
            if (checkMultiblockTicks >= 20) {
                updateMultiblock()
                checkMultiblockTicks = 0
            }
            val isActive = !this.input[0].isEmpty && !this.input[1].isEmpty
            val state = this.world.getBlockState(this.pos)
            if(state.block != ModBlocks.fusionController) return;
            val currentStatus = state.getValue(STATUS)
            if (this.isValidMultiblock) {
                if (isActive) {
                    if (currentStatus != PropertyPowerStatus.ON) this.world.setBlockState(this.pos, state.withProperty(STATUS, PropertyPowerStatus.ON))
                } else if (currentStatus != PropertyPowerStatus.STANDBY) world.setBlockState(pos, state.withProperty(STATUS, PropertyPowerStatus.STANDBY))
            } else if (currentStatus != PropertyPowerStatus.OFF) world.setBlockState(pos, state.withProperty(STATUS, PropertyPowerStatus.OFF))

            if (canProcess()) process()
            this.markDirtyClientEvery(5)
        }
    }


    fun canProcess(): Boolean {
        return this.isValidMultiblock
                && !input[0].isEmpty
                && !input[1].isEmpty
                && !recipeOutput.isEmpty
                && (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
                && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
                && energyStorage.energyStored >= ConfigHandler.fusionEnergyPerTick!!

    }

    fun process() {
        if (progressTicks < ConfigHandler.fusionProcessingTicks!!) {
            progressTicks++
        } else {
            progressTicks = 0
            output.setOrIncrement(0, recipeOutput.copy())
            input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
            input.decrementSlot(1, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyStorage.extractEnergy(ConfigHandler.fusionEnergyPerTick!!, false)
    }


    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("ProgressTicks", progressTicks)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.refreshRecipe()
        this.updateMultiblock()
    }

    private fun containsCasing(pos: BlockPos): Boolean = (this.world.getBlockState(pos).block == ModBlocks.fusionCasing)
    private fun containsCore(pos: BlockPos): Boolean = (this.world.getBlockState(pos).block == ModBlocks.fusionCore)
    private fun containsFusionPart(pos: BlockPos): Boolean {
        val block = this.world.getBlockState(pos).block
        return block == ModBlocks.fusionCasing || block == ModBlocks.fusionCore || block == ModBlocks.fusionController
    }

    fun updateMultiblock() {
        this.isValidMultiblock = validateMultiblock()
    }

    fun validateMultiblock(): Boolean {
        val multiblockDirection: EnumFacing? = world?.getBlockState(this.pos)?.getValue(FusionControllerBlock.FACING)?.opposite
        if(multiblockDirection == null) return false
        fun BlockPos.offsetUp(amt: Int = 1) = this.offset(EnumFacing.UP, amt)
        fun BlockPos.offsetLeft(amt: Int = 1) = this.offset(multiblockDirection.rotateY(), amt)
        fun BlockPos.offsetRight(amt: Int = 1) = this.offset(multiblockDirection.rotateY(), -1 * amt)
        fun BlockPos.offsetBack(amt: Int = 1) = this.offset(multiblockDirection, amt)
        fun BlockPos.offsetDown(amt: Int = 1) = this.offset(EnumFacing.DOWN, amt)

        val coreBottom = this.pos.offsetBack(3).offsetUp()
        val coreTop = coreBottom.offsetUp(2)
        val coreMatches = BlockPos.getAllInBox(coreBottom, coreTop).all(::containsCore)
        /*
        val insideCorner1 = this.pos.offsetUp().offsetLeft().offsetBack(2)
        val insideCorner2 = insideCorner1.offsetBack(2).offsetRight(2).offsetUp(2)
        val middleXZ = this.pos.offsetBack(3)
        val emptyInsideMatches = BlockPos.getAllInBox(insideCorner1, insideCorner2)
                .filterNot { it.x == middleXZ.x && it.z == middleXZ.z }.all(world::isAirBlock)
        */
        //A cube of all blocks surrounding the fusion multiblock, checking to ensure no other fusion multiblocks are overlapping/sharing
        val outsideCorner1 = this.pos.offsetLeft(3).offsetDown()
        val outsideCorner2 = outsideCorner1.offsetRight(6).offsetUp(6).offsetBack(6)
        val borderingParts = BlockPos.getAllInBox(outsideCorner1, outsideCorner2).filter {
            var sharedAxes = 0
            if (it.x == outsideCorner1.x || it.x == outsideCorner2.x) sharedAxes++
            if (it.y == outsideCorner1.y || it.y == outsideCorner2.y) sharedAxes++
            if (it.z == outsideCorner1.z || it.z == outsideCorner2.z) sharedAxes++
            sharedAxes >= 1
        }.filterNot(this.pos::equals)
                .count(this::containsFusionPart)


        val casingCorner1 = this.pos.offsetLeft(2).offsetBack(1)
        val casingCorner2 = casingCorner1.offsetRight(4).offsetBack(4).offsetUp(4)
        val casingMatches = BlockPos.getAllInBox(casingCorner1, casingCorner2).filter {
            var sharedAxes = 0
            if (it.x == casingCorner1.x || it.x == casingCorner2.x) sharedAxes++
            if (it.y == casingCorner1.y || it.y == casingCorner2.y) sharedAxes++
            if (it.z == casingCorner1.z || it.z == casingCorner2.z) sharedAxes++
            sharedAxes >= 1
        }.all(::containsCasing)

        return casingMatches && coreMatches && /*emptyInsideMatches &&*/ (borderingParts == 0)
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