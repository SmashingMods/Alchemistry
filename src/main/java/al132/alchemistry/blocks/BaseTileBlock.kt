package al132.alchemistry.blocks

import al132.alchemistry.Alchemistry
import al132.alchemistry.Reference
import al132.alib.blocks.ALTileBlock
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess


open class BaseTileBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int)
    : ALTileBlock(name, Reference.creativeTab, tileClass, Alchemistry, guiID) {
    init {
        ModBlocks.blocks.add(this)
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        super.getDrops(drops, world, pos, state, fortune)
        val thisItem = Item.getItemFromBlock(this)
        if (thisItem != Items.AIR) {
            val droppedItem: ItemStack? = drops.firstOrNull { it.item == thisItem }
            val tag = droppedItem?.tagCompound
            tag?.apply {
                if (this.hasKey("id")) removeTag("id")
                if (this.hasKey("input")) {
                    val input = this.getCompoundTag("input")
                    if (input.hasKey("Size") && input.getInteger("Size") == 0) {
                        this.removeTag("input")
                    }
                    if (input.getTagList("Items", 10).isEmpty) {
                        this.removeTag("input")
                    }
                }
                if (this.hasKey("ProgressTicks") && this.getInteger("ProgressTicks") == 0) {
                    this.removeTag("ProgressTicks")
                }
                if (this.hasKey("output")) {
                    val output = this.getCompoundTag("output")
                    if (output.hasKey("Size") && output.getInteger("Size") == 0) {
                        this.removeTag("output")
                    }
                    if (output.getTagList("Items", 10).isEmpty) {
                        this.removeTag("output")
                    }
                }
                if (this.hasKey("InputTankNBT")) {
                    val inputTank = this.getCompoundTag("InputTankNBT")
                    if (inputTank.hasKey("Empty")) this.removeTag("InputTankNBT")
                }
                if (this.hasKey("OutputTankNBT")) {
                    val inputTank = this.getCompoundTag("OutputTankNBT")
                    if (inputTank.hasKey("Empty")) this.removeTag("OutputTankNBT")
                }
                if(this.hasKey("EnergyStored") && this.getInteger("EnergyStored") == 0){
                    this.removeTag("EnergyStored")
                }
            }
            if (tag != null && tag.size == 0) droppedItem.tagCompound = null
        }
    }
}