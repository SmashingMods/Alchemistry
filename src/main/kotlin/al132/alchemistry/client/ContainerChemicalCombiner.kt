package al132.alchemistry.client

import al132.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/23/2017.
 */

class ContainerChemicalCombiner(val playerInv: IInventory,
                                tileCombiner: TileChemicalCombiner) :
        ContainerBase<TileChemicalCombiner>(playerInv, tileCombiner) {

    override fun addOwnSlots() {
        this.addSlotArray(x_start = 39, y_start = 14, rows = 3, columns = 3, handler = tile.input)
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 140, 33))
    }
}