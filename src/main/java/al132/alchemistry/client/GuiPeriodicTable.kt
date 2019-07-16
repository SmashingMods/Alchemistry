package al132.alchemistry.client

import al132.alchemistry.Reference
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

class GuiPeriodicTable : GuiScreen() {
    init {
        this.width = Minecraft.getMinecraft().displayWidth
        this.height = Minecraft.getMinecraft().displayHeight
    }

    val textureLocation = ResourceLocation(Reference.MODID, "textures/images/periodic_table.png")
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val scaledRes = ScaledResolution(Minecraft.getMinecraft())
        val w = Math.min(scaledRes.scaledWidth, 1304)
        val h = Math.min(scaledRes.scaledHeight, 624)
        //drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f,w,h,w.toFloat(),h.toFloat())
        drawScaledCustomSizeModalRect(0, 0, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())
    }
}