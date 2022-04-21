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
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAlchemistryScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final List<DisplayData> displayData = new ArrayList<>();

    public AbstractAlchemistryScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    public void bindWidgets() {
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png"));
    }

    public void drawRightArrow(PoseStack pPoseStack, int pX, int pY, int pWidth) {
        int height = 9;
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 120, pWidth, height);
    }

    @SuppressWarnings("unused")
    public void drawDownArrow(PoseStack pPoseStack, int pX, int pY, int pHeight) {
        int width = 9;
        bindWidgets();
        blit(pPoseStack, pX, pY, 9, 129, width, pHeight);
    }

    @SuppressWarnings("unused")
    public void drawUpArrow(PoseStack pPoseStack, int pX, int pY, int pHeight) {
        int width = 9;
        bindWidgets();
        blit(pPoseStack, pX, pY, 0, 129, width, pHeight);
    }

    public int getBarScaled(int pPixels, int pCount, int pMax) {
        if (pCount > 0 && pMax > 0) {
            return pCount * pPixels / pMax;
        } else {
            return 0;
        }
    }

    public void drawEnergyBar(PoseStack pPoseStack, EnergyDisplayData pData, int pTextureX, int pTextureY) {
        if (pData.getStored() > 0) {
            int x = pData.getX() + (this.width - this.imageWidth) / 2;
            int y = pData.getY() + (this.height - this.imageHeight) / 2;
            int vHeight = this.getBarScaled(pData.getHeight(), pData.getStored(), pData.getMaxStored());
            bindWidgets();
            this.blit(pPoseStack, x, y + pData.getHeight() - vHeight, pTextureX, pTextureY, pData.getWidth(), vHeight);
        }
    }

    public void drawFluidTank(FluidDisplayData pData, int pTextureX, int pTextureY) {
        if (pData.getStored() > 5) {
            FluidStack fluidStack = pData.getFluidHandler().getFluidStack();
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            setFluidColor(fluidStack.getFluid().getAttributes().getColor());

            if (pData.getStored() <= 0) return;

            TextureAtlasSprite icon = getStillTexture(fluidStack.getFluid().getAttributes().getStillTexture());
            float minU = icon.getU0();
            float maxU = icon.getU1();
            float minV = icon.getV0();
            float maxV = icon.getV1();

            int renderAmount = Math.max(Math.min(pData.getHeight(), pData.getStored() * pData.getHeight() / pData.getMaxStored()), 1);
            int posY = pTextureY + pData.getHeight() - renderAmount;

            for (int width = 0; width < pData.getWidth(); width++) {
                for (int height = 0; height < pData.getHeight(); height++) {
                    int drawWidth = Math.min(pData.getWidth() - width, 16);
                    int drawHeight = Math.min(renderAmount - height, 16);
                    int drawX = pTextureX + width;
                    int drawY = posY + height;

                    float pV = minV + (maxV - minV) * drawHeight / 16f;
                    float pU = minU + (maxU - minU) * drawWidth / 16f;

                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferBuilder = tesselator.getBuilder();

                    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    bufferBuilder.vertex(drawX, drawY + drawHeight, 0).uv(minU, pV).endVertex();
                    bufferBuilder.vertex((drawX + drawWidth), (drawY + drawHeight), 0).uv(pU, pV).endVertex();
                    bufferBuilder.vertex((drawX + drawWidth), drawY, 0).uv(pU, minV).endVertex();
                    bufferBuilder.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
                    tesselator.end();

                    height += 16;
                }
                width += 16;
            }
        }
    }

    @Nonnull
    public static TextureAtlasSprite getStillTexture(ResourceLocation pResourceLocation) {
        if (pResourceLocation != null) {
            return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pResourceLocation);
        } else {
            ResourceLocation waterFallback = Fluids.WATER.getAttributes().getStillTexture();
            return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(waterFallback);
        }
    }

    public static void setFluidColor(int pColor) {
        Color hexColor = new Color(pColor);
        float red = hexColor.getRed() / 255f;
        float green = hexColor.getGreen() / 255f;
        float blue = hexColor.getBlue() / 255f;
        RenderSystem.setShaderColor(red, green, blue, 1.0f);
    }
}
