package al132.alchemistry.client

import al132.alchemistry.ConfigHandler
import al132.alchemistry.network.FusionModePacket
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.tiles.TileFusionController
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.client.gui.GuiButton
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
    lateinit var modeButton: GuiButton
    override val displayName = Translator.translateToLocal("tile.fusion_controller.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyStorage))
    }

    override fun initGui() {
        super.initGui()
        modeButton = GuiButton(0, this.guiLeft + 30, this.guiTop + 40, 80, 20, "Test")
        this.buttonList.add(modeButton)
    }

    override fun actionPerformed(guibutton: GuiButton) {
        when (guibutton.id) {
            modeButton.id -> PacketHandler.INSTANCE!!.sendToServer(FusionModePacket(tile.pos, singleMode = true))
        }
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

    fun updateButtonStrings() {
        if (tile.singleMode) modeButton.displayString = Translator.translateToLocal("tile.fusion.regular_mode")
        else modeButton.displayString = Translator.translateToLocal("tile.fusion.single_mode")
    }

    fun updateStatus() {
        if (tile.isValidMultiblock) statusText = ""
        else statusText = Translator.translateToLocal("tile.fusion.invalid_multiblock")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        updateStatus()
        updateButtonStrings()
        this.fontRenderer.drawStringWithShadow(statusText, 30.0f, 110.0f, Color.WHITE.rgb)
    }
}