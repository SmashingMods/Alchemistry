package al132.alchemistry.blocks.evaporator;


import al132.alchemistry.Registration;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatorRenderer implements BlockEntityRenderer<EvaporatorTile> {

    public EvaporatorRenderer(BlockEntityRendererProvider.Context c) {

    }

    @Override
    public void render(EvaporatorTile tile, float p_225616_2_, PoseStack matrix, MultiBufferSource p_225616_4_, int p_225616_5_, int p_225616_6_) {
        FluidStack fluidStack = tile.fluidTank.getFluidInTank(0);
        /*
        if(!fluidStack.isEmpty()) {
            Fluid fluid = fluidStack.getFluid();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.disableBlend();
            RenderSystem.pushMatrix();
            RenderSystem.translated(x, y, z);
            bindForSetup(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            renderFluid(Tessellator.getInstance(), fluid, tile);
            RenderHelper.enableStandardItemLighting();
            RenderSystem.popMatrix();
        }*/
    }

    private void renderFluid(Tesselator tess, Fluid fluid, EvaporatorTile tile) {
        BufferBuilder buffer = tess.getBuilder();
        FluidStack inputFluid = tile.fluidTank.getFluidInTank(0);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(fluid.getAttributes().getStillTexture(inputFluid));//.toString());
        double capacity = tile.fluidTank.getTankCapacity(0);
        double amount = inputFluid.getAmount();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float u1 = sprite.getU0();
        float v1 = sprite.getV0();
        float u2 = sprite.getU1();
        float v2 = sprite.getV1();


        //Block is 12 bits tall, with liquid offset 2 bits up
        double scale = (10.0 / 16.0) * (amount / capacity);
        double offset = (2.0 / 16.0) + .001;

        //Render in inner 12 bits, trim off 2 bits on each side
        double margin = 2.0 / 16.0;
        buffer.vertex(margin, scale + offset, margin).uv(u1, v1).color(255, 255, 255, 128).endVertex();
        buffer.vertex(margin, scale + offset, 1.0 - margin).uv(u1, v2).color(255, 255, 255, 128).endVertex();
        buffer.vertex(1.0 - margin, scale + offset, 1.0 - margin).uv(u2, v2).color(255, 255, 255, 128).endVertex();
        buffer.vertex(1.0 - margin, scale + offset, margin).uv(u2, v1).color(255, 255, 255, 128).endVertex();
        tess.end();
    }

    public static void register() {
        BlockEntityRenderers.register(Registration.EVAPORATOR_BE.get(), EvaporatorRenderer::new);
    }

}
