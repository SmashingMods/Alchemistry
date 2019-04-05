package al132.alchemistry.client


import al132.alib.client.ALGuiBase
import al132.alib.tiles.ALTile
import al132.alib.tiles.IGuiTile
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation


abstract class GuiBase<T>(container: Container, tile: T, textureLocation: ResourceLocation)
    : ALGuiBase<T>(container, tile, textureLocation) where T : ALTile, T : IGuiTile {

    override var powerBarTexture: ResourceLocation? = ResourceLocation(root + "template.png")

    companion object {
        val root = "alchemistry:textures/gui/container/"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }
}