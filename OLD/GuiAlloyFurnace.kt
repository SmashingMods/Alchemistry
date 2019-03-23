package al132.alchemistry.client

import al132.alchemistry.tiles.TileAlloyFurnace
import al132.alib.client.IResource
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 4/29/2017.
 */

class GuiAlloyFurnace(playerInv: InventoryPlayer, tile: TileAlloyFurnace)
    : GuiBase<TileAlloyFurnace>(ContainerAlloyFurnace(playerInv, tile),tile) {

    override val displayName = "Alloy Furnace"

    init {
        //this.displayData.add(CapabilityFluidDisplayWrapper(48, 40, 16, 60, tile::inputTank))
    }

    /*
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(GuiEvaporator.textureLocation())
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(28, tile.progressTicks, tile.calculateProcessingTime())
            this.drawTexturedModalRect(i + 79, j+63, 175, 0, k, 9)
        }
    }*/

    companion object : IResource {
        override fun textureLocation() = ResourceLocation(root + "alloy_furnace_gui.png")
    }
}