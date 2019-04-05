package al132.alchemistry.client

import al132.alchemistry.tiles.TileLiquifier
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerLiquifier(playerInv: InventoryPlayer,
                         tile: TileLiquifier) :
        ContainerBase<TileLiquifier>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotToContainer(SlotItemHandler(tile.input,0,49,58))
    }
}