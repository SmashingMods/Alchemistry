package al132.alchemistry.client;

import al132.alchemistry.tiles.TileEvaporator
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fluids.Fluid
import org.lwjgl.opengl.GL11

//Peeked off of https://github.com/McJtyMods/DeepResonance/blob/1.12/src/main/java/mcjty/deepresonance/blocks/tank/TankTESR.java
//for guidance
 class EvaporatorTESR : TileEntitySpecialRenderer<TileEvaporator>() {

    override fun render(tile: TileEvaporator, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val fluidStack = tile.inputTank.fluid
        if (fluidStack != null) {
            val fluid = fluidStack.fluid
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting()
            GlStateManager.color(1F, 1F, 1F, 1F)
            GlStateManager.disableBlend()
            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            renderFluid(Tessellator.getInstance(), fluid, tile)
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting()
            GlStateManager.popMatrix()
        }
    }

    fun renderFluid(tess: Tessellator, fluid: Fluid, tile: TileEvaporator) {
        val buffer: BufferBuilder = tess.buffer
        val sprite: TextureAtlasSprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(fluid.getStill(tile.inputTank.fluid).toString());
        val capacity: Double = tile.inputTank.capacity.toDouble()
        val amount: Double = tile.inputTank.fluidAmount.toDouble()
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        val u1 = sprite.minU.toDouble()
        val v1 = sprite.minV.toDouble()
        val u2 = sprite.maxU.toDouble()
        val v2 = sprite.maxV.toDouble()


        //Block is 12 bits tall, with liquid offset 2 bits up
        val scale = (10.0 / 16.0) * (amount / capacity)
        val offset = (2.0 / 16.0) + .001

        //Render in inner 12 bits, trim off 2 bits on each side
        val margin = 2.0 / 16.0
        buffer.pos(margin, scale + offset, margin).tex(u1, v1).color(255, 255, 255, 128).endVertex();
        buffer.pos(margin, scale + offset, 1.0 - margin).tex(u1, v2).color(255, 255, 255, 128).endVertex();
        buffer.pos(1.0 - margin, scale + offset, 1.0 - margin).tex(u2, v2).color(255, 255, 255, 128).endVertex();
        buffer.pos(1.0 - margin, scale + offset, margin).tex(u2, v1).color(255, 255, 255, 128).endVertex();
        tess.draw()
    }
}
