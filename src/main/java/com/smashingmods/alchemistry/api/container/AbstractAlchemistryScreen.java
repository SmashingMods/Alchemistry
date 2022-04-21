package com.smashingmods.alchemistry.api.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAlchemistryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final List<DisplayData> displayData = new ArrayList<>();
    private final int arrowPixels = 9;
    private final int arrowSmallPixels = 7;

    public AbstractAlchemistryScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public void bindWidgets() {
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png"));
    }

    public static int getBarScaled(int pPixels, int pCount, int pMax) {
        if (pCount > 0 && pMax > 0) {
            return pCount * pPixels / pMax;
        } else {
            return 0;
        }
    }

    public void drawEnergyBar(PoseStack pPoseStack, EnergyDisplayData pData, int pTextureX, int pTextureY) {
        int x = pData.getX() + (this.width - this.imageWidth) / 2;
        int y = pData.getY() + (this.height - this.imageHeight) / 2;
        int vHeight = getBarScaled(pData.getHeight(), pData.getStored(), pData.getMaxStored());
        bindWidgets();
        this.blit(pPoseStack, x, y + pData.getHeight() - vHeight, pTextureX, pTextureY, pData.getWidth(), vHeight);
    }

    public void drawFluidTank(FluidDisplayData pData, int pTextureX, int pTextureY) {
        if (pData.getStored() > 0) {
            FluidStack fluidStack = pData.getFluidHandler().getFluidStack();
            setShaderColor(fluidStack.getFluid().getAttributes().getColor());
            TextureAtlasSprite icon = getResourceTexture(fluidStack.getFluid().getAttributes().getStillTexture());
            drawTexture(pData, icon, pTextureX, pTextureY);
        }
    }

    public void drawTexture(DisplayData pData, TextureAtlasSprite pSprite, int pTextureX, int pTextureY) {

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        int renderAmount = Math.max(Math.min(pData.getHeight(), pData.getStored() * pData.getHeight() / pData.getMaxStored()), 1);
        int posY = pTextureY + pData.getHeight() - renderAmount;

        float minU = pSprite.getU0();
        float maxU = pSprite.getU1();
        float minV = pSprite.getV0();
        float maxV = pSprite.getV1();

        for (int width = 0; width < pData.getWidth(); width++) {
            for (int height = 0; height < pData.getHeight(); height++) {

                int drawHeight = Math.min(renderAmount - height, 16);
                int drawWidth = Math.min(pData.getWidth() - width, 16);

                int x1 = pTextureX + width;
                float x2 = x1 + drawWidth;
                int y1 = posY + height;
                float y2 = y1 + drawHeight;

                float scaleV = minV + (maxV - minV) * drawHeight / 16f;
                float scaleU = minU + (maxU - minU) * drawWidth / 16f;

                float blitOffset = 0;

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.getBuilder();

                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.vertex(x1, y2, blitOffset).uv(minU, scaleV).endVertex();
                bufferBuilder.vertex(x2, y2, blitOffset).uv(scaleU, scaleV).endVertex();
                bufferBuilder.vertex(x2, y1, blitOffset).uv(scaleU, minV).endVertex();
                bufferBuilder.vertex(x1, y1, blitOffset).uv(minU, minV).endVertex();
                tesselator.end();

                height += 15;
            }
            width += 16;
        }

//        RenderSystem.disableTexture();
    }

    public static TextureAtlasSprite getResourceTexture(@Nonnull ResourceLocation pResourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pResourceLocation);
    }

    public static void setShaderColor(int pColor) {
        float alpha = (pColor >> 24 & 255) / 255f;
        float red = (pColor >> 16 & 255) / 255f;
        float green = (pColor >> 8 & 255) / 255f;
        float blue = (pColor & 255) / 255f;
        RenderSystem.setShaderColor(red, green, alpha, blue);
    }

    @SuppressWarnings("unused")
    public void drawArrowLeft(PoseStack pPoseStack, int pX, int pY, int pWidth) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 120, pWidth, arrowPixels);
    }

    @SuppressWarnings("unused")
    public void drawArrowRight(PoseStack pPoseStack, int pX, int pY, int pWidth) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 129, pWidth, arrowPixels);
    }

    @SuppressWarnings("unused")
    public void drawArrowUp(PoseStack pPoseStack, int pX, int pY, int pHeight) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 9, 138, arrowPixels, pHeight);
    }

    @SuppressWarnings("unused")
    public void drawArrowDown(PoseStack pPoseStack, int pX, int pY, int pHeight) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 138, arrowPixels, pHeight);
    }

    @SuppressWarnings("unused")
    public void drawSmallArrowLeft(PoseStack pPoseStack, int pX, int pY, int pHeight) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 138, arrowSmallPixels, pHeight);
    }

    @SuppressWarnings("unused")
    public void drawSmallArrowRight(PoseStack pPoseStack, int pX, int pY, int pHeight) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 138, arrowSmallPixels, pHeight);
    }

    @SuppressWarnings("unused")
    public void drawSmallArrowUp(PoseStack pPoseStack, int pX, int pY, int pWidth) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 138, pWidth, arrowSmallPixels);
    }

    @SuppressWarnings("unused")
    public void drawSmallArrowDown(PoseStack pPoseStack, int pX, int pY, int pWidth) {
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 138, pWidth, arrowSmallPixels);
    }
}
