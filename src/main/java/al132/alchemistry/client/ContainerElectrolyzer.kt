package al132.alchemistry.client

import al132.alchemistry.tiles.TileElectrolyzer
import al132.alib.tiles.ALTileStackHandler
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer) :
        ContainerBase<TileElectrolyzer>(playerInv, tile) {

    override fun addOwnSlots() {
        if (tile.input.slots < 1) {
            tile.input = ALTileStackHandler(1, tile)
        }
        addSlotToContainer(SlotItemHandler(tile.input, 0, 85, 39))
        addSlotArray(x_start = 122, y_start = 52, rows = 4, columns = 1, handler = tile.output)
    }
}