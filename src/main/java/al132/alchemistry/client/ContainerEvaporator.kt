package al132.alchemistry.client

import al132.alchemistry.tiles.TileEvaporator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 4/29/2017.
 */
class ContainerEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator) :
        ContainerBase<TileEvaporator>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotToContainer(SlotItemHandler(tile.output,0,122,52))
    }
}