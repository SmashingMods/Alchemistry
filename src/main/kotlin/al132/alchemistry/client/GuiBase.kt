package al132.alchemistry.client


import al132.alib.client.ALGuiBase
import al132.alib.client.IResource
import al132.alib.tiles.ALTile
import al132.alib.tiles.IGuiTile
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation


abstract class GuiBase<T>(container: Container, tile: T)
    : ALGuiBase<T>(container, tile) where T : ALTile, T : IGuiTile {

    override var powerBarTexture: ResourceLocation? = ResourceLocation(root + "template.png")

    companion object : IResource {
        override fun textureLocation(): ResourceLocation? = null
        val root = "alchemistry:textures/gui/container/"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }
}