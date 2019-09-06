package al132.alchemistry.blocks.evaporator;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class EvaporatorTESR extends TileEntityRenderer<EvaporatorTile> {

    @Override
    public void render(EvaporatorTile tile, double x, double y, double z, float partialTicks, int destroyStage) {
        FluidStack fluidStack = tile.fluidTank.getFluidInTank(0);
        if(!fluidStack.isEmpty()) {
            Fluid fluid = fluidStack.getFluid();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            GlStateManager.color4f(1F, 1F, 1F, 1F);
            GlStateManager.disableBlend();
            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            renderFluid(Tessellator.getInstance(), fluid, tile);
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    private void renderFluid(Tessellator tess, Fluid fluid, EvaporatorTile tile) {
        BufferBuilder buffer = tess.getBuffer();
        FluidStack inputFluid = tile.fluidTank.getFluidInTank(0);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap()
                .getAtlasSprite(fluid.getAttributes().getStill(inputFluid).toString());
        double capacity = tile.fluidTank.getTankCapacity(0);
        double amount = inputFluid.getAmount();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        double u1 = sprite.getMinU();
        double v1 = sprite.getMinV();
        double u2 = sprite.getMaxU();
        double v2 = sprite.getMaxV();


        //Block is 12 bits tall, with liquid offset 2 bits up
        double scale = (10.0 / 16.0) * (amount / capacity);
        double offset = (2.0 / 16.0) + .001;

        //Render in inner 12 bits, trim off 2 bits on each side
        double margin = 2.0 / 16.0;
        buffer.pos(margin, scale + offset, margin).tex(u1, v1).color(255, 255, 255, 128).endVertex();
        buffer.pos(margin, scale + offset, 1.0 - margin).tex(u1, v2).color(255, 255, 255, 128).endVertex();
        buffer.pos(1.0 - margin, scale + offset, 1.0 - margin).tex(u2, v2).color(255, 255, 255, 128).endVertex();
        buffer.pos(1.0 - margin, scale + offset, margin).tex(u2, v1).color(255, 255, 255, 128).endVertex();
        tess.draw();
    }
}
