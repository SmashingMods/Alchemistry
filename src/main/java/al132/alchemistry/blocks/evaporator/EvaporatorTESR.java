package al132.alchemistry.blocks.evaporator;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class EvaporatorTESR extends TileEntityRenderer<EvaporatorTile> {

    public EvaporatorTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void func_225616_a_(EvaporatorTile tile, float p_225616_2_, MatrixStack matrix, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
        FluidStack fluidStack = tile.fluidTank.getFluidInTank(0);
        /*
        if(!fluidStack.isEmpty()) {
            Fluid fluid = fluidStack.getFluid();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.disableBlend();
            RenderSystem.pushMatrix();
            RenderSystem.translated(x, y, z);
            bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            renderFluid(Tessellator.getInstance(), fluid, tile);
            RenderHelper.enableStandardItemLighting();
            RenderSystem.popMatrix();
        }*/
    }

    private void renderFluid(Tessellator tess, Fluid fluid, EvaporatorTile tile) {
        BufferBuilder buffer = tess.getBuffer();
        FluidStack inputFluid = tile.fluidTank.getFluidInTank(0);
        TextureAtlasSprite sprite = Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
                .apply(fluid.getAttributes().getStillTexture(inputFluid));//.toString());
        double capacity = tile.fluidTank.getTankCapacity(0);
        double amount = inputFluid.getAmount();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        float u1 = sprite.getMinU();
        float v1 = sprite.getMinV();
        float u2 = sprite.getMaxU();
        float v2 = sprite.getMaxV();


        //Block is 12 bits tall, with liquid offset 2 bits up
        double scale = (10.0 / 16.0) * (amount / capacity);
        double offset = (2.0 / 16.0) + .001;

        //Render in inner 12 bits, trim off 2 bits on each side
        double margin = 2.0 / 16.0;
        buffer.func_225582_a_(margin, scale + offset, margin).func_225583_a_(u1, v1).func_227885_a_(255, 255, 255, 128).endVertex();
        buffer.func_225582_a_(margin, scale + offset, 1.0 - margin).func_225583_a_(u1, v2).func_227885_a_(255, 255, 255, 128).endVertex();
        buffer.func_225582_a_(1.0 - margin, scale + offset, 1.0 - margin).func_225583_a_(u2, v2).func_227885_a_(255, 255, 255, 128).endVertex();
        buffer.func_225582_a_(1.0 - margin, scale + offset, margin).func_225583_a_(u2, v1).func_227885_a_(255, 255, 255, 128).endVertex();
        tess.draw();
    }

}
