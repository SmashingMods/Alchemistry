package al132.alchemistry.client

import al132.alchemistry.tiles.TileFusionController
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerFusionController(playerInv: InventoryPlayer,
                                tile: TileFusionController) :
        ContainerBase<TileFusionController>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotArray(44, 79, 1, 2, tile.input)
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 132, 79))
    }
}