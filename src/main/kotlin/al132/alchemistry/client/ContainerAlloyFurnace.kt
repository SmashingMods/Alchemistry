package al132.alchemistry.client

import al132.alchemistry.tiles.TileAlloyFurnace
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 4/29/2017.
 */
class ContainerAlloyFurnace(playerInv: InventoryPlayer, tile: TileAlloyFurnace) :
        ContainerBase<TileAlloyFurnace>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotArray(44, 49, 1, 5, tile.input)
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 80, 102))
    }
}