package al132.alchemistry.client

import al132.alchemistry.ConfigHandler
import al132.alchemistry.compat.jei.Translator
import al132.alchemistry.tiles.TileAtomizer
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiAtomizer(playerInv: InventoryPlayer, tile: TileAtomizer) :
        GuiBase<TileAtomizer>(ContainerAtomizer(playerInv, tile), tile,GuiAtomizer.textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "atomizer_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.atomizer.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyCapability))
        this.displayData.add(CapabilityFluidDisplayWrapper(48, 40, 16, 60, tile::inputTank))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(28, tile.progressTicks, ConfigHandler.atomizerProcessingTicks!!)
            this.drawTexturedModalRect(i + 79, j+63, 175, 0, k, 9)
        }
    }
}