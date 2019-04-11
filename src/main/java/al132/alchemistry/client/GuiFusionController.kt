package al132.alchemistry.client

import al132.alchemistry.ConfigHandler
import al132.alchemistry.compat.jei.Translator
import al132.alchemistry.tiles.TileFusionController
import al132.alib.client.CapabilityEnergyDisplayWrapper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by al132 on 1/16/2017.
 */
class GuiFusionController(playerInv: InventoryPlayer, tile: TileFusionController) :
        GuiBase<TileFusionController>(ContainerFusionController(playerInv, tile), tile, GuiFusionController.textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "fusion_controller_gui.png")
    }

    var statusText = ""
    override val displayName = Translator.translateToLocal("tile.fusion_controller.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyCapability))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(28, tile.progressTicks, ConfigHandler.fusionProcessingTicks!!) //TODO
            this.drawTexturedModalRect(i + 90, j + 82, 175, 0, k, 9)
        }
    }

    fun updateStatus() {
        if (tile.isValidMultiblock) statusText = ""
        else statusText = Translator.translateToLocal("tile.fusion.invalid_multiblock")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        updateStatus()
        this.fontRenderer.drawStringWithShadow(statusText, 30.0f, 110.0f, Color.WHITE.rgb)
    }
}