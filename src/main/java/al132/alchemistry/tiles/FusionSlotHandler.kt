package al132.alchemistry.tiles

import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class FusionSlotHandler(val tile: TileFusionController, itemHandler: IItemHandler, val index: Int, val xPos: Int, val yPos: Int) :
        SlotItemHandler(itemHandler, index, xPos, yPos) {

    override fun getSlotStackLimit(): Int {
        if(this.tile.singleMode) return 1
        else return super.getSlotStackLimit()
    }
}