package al132.alchemistry.client

import al132.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/23/2017.
 */

class ContainerChemicalCombiner(val playerInv: IInventory,
                                tileCombiner: TileChemicalCombiner) :
        ContainerBase<TileChemicalCombiner>(playerInv, tileCombiner) {

    override fun addOwnSlots() {
        this.addSlotToContainer(SlotItemHandler(tile.clientRecipeTarget,0,140,5))
        println("SIZE: ${inventorySlots.size}")
        println("SLOT[0]: " + inventorySlots[0])
        this.addSlotArray(x_start = 39, y_start = 14, rows = 3, columns = 3, handler = tile.input)
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 140, 33))
    }

    val tileContainerStart = 1 //Skip over the clientRecipeTarget at slot 0

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]
        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index < tile.SIZE) {
                if (!this.mergeItemStack(itemstack1, tile.SIZE, this.inventorySlots.size, true)) {
                    return ItemStack.EMPTY!!
                }
            } else if (!this.mergeItemStack(itemstack1, tileContainerStart, tile.SIZE, false)) return ItemStack.EMPTY!!

            if (itemstack1.count <= 0) slot.putStack(ItemStack.EMPTY)
            else slot.onSlotChanged()
        }
        return itemstack!!
    }
}