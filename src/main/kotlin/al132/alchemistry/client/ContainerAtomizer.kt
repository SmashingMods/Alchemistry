package al132.alchemistry.client

import al132.alchemistry.tiles.TileAtomizer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerAtomizer(playerInv: InventoryPlayer,
                        tile: TileAtomizer) :
        ContainerBase<TileAtomizer>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotToContainer(SlotItemHandler(tile.output,0,122,52))
    }
}