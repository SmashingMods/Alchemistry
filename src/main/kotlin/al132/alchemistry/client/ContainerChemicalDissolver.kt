package al132.alchemistry.client

import al132.alchemistry.tiles.TileChemicalDissolver
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerChemicalDissolver(playerInv: InventoryPlayer,
                                 tile: TileChemicalDissolver) :
        ContainerBase<TileChemicalDissolver>(playerInv, tile) {

    override fun addOwnSlots() {
        addSlotToContainer(SlotItemHandler(tile.input, 0, 83, 14))
        addSlotArray(x_start = 48, y_start = 85, rows = 2, columns = 5, handler = tile.output)
    }
}