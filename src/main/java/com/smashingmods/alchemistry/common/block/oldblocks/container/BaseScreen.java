package com.smashingmods.alchemistry.common.block.oldblocks.container;

import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.oldblocks.blockentity.GuiBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {

    protected ResourceLocation GUI;
    private final T container;
    protected final List<CapabilityDisplayWrapper> displayData = new ArrayList<>();
    private final ResourceLocation powerBarTexture;

    public BaseScreen(T pContainer, Inventory pInventory, Component pName, ResourceLocation pResourceLocation) {
        super(pContainer, pInventory, pName);
        this.powerBarTexture = new ResourceLocation(Alchemistry.MODID, "textures/gui/template.png");
        this.container = pContainer;
        this.imageWidth = ((GuiBlockEntity) pContainer.blockEntity).getWidth();
        this.imageHeight = ((GuiBlockEntity) pContainer.blockEntity).getHeight();
        this.GUI = pResourceLocation;
    }

    //drawGuiContainerBackgroundLayer->func_230459_a_
    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //this.getMinecraft().getTextureManager().bindForSetup(GUI);
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(pPoseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        String displayName = I18n.get(container.blockEntity.getDisplayName().getString());
        drawString(pPoseStack, this.font, displayName,this.imageWidth / 2 - this.font.width(displayName) / 2, -10, Color.WHITE.getRGB());
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        //renderHoveredToolTip->func_230459_a_
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        displayData.forEach(data -> {
            if (data instanceof CapabilityEnergyDisplayWrapper) {
                this.drawPowerBar(pPoseStack, (CapabilityEnergyDisplayWrapper) data, powerBarTexture, 0, 0);
            } else if (data instanceof CapabilityFluidDisplayWrapper) {
                this.drawFluidTank((CapabilityFluidDisplayWrapper) data, x + data.x, y + data.y);
            }
        });

        this.displayData.stream().filter(data -> (pMouseX >= data.x + x
                && pMouseX <= data.x + x + data.width
                && pMouseY >= data.y + y
                && pMouseY <= data.y + y + data.height))
                .forEach(it -> renderTooltip(pPoseStack, it.toTextComponent(), pMouseX, pMouseY));
    }

    public int getBarScaled(int pPixels, int pCount, int pMax) {
        if (pCount > 0 && pMax > 0) {
            return pCount * pPixels / pMax;
        } else {
            return 0;
        }
    }

    public void drawPowerBar(PoseStack pPoseStack, CapabilityEnergyDisplayWrapper pWrapper, ResourceLocation pResourceLocation, int pTextureX, int pTextureY) {

        if (pWrapper.getStored() > 0) {
            int x = pWrapper.x + ((this.width - this.imageWidth) / 2);
            int y = pWrapper.y + ((this.height - this.imageHeight) / 2);
            int vHeight = this.getBarScaled(pWrapper.height, pWrapper.getStored(), pWrapper.getMaxStored());
            RenderSystem.setShaderTexture(0, pResourceLocation);
            //this.getMinecraft().textureManager.bindForSetup(texture);
            this.blit(pPoseStack, x, y + pWrapper.height - vHeight, pTextureX, pTextureY, pWrapper.width, vHeight);
            //this.getMinecraft().textureManager.bindForSetup(this.GUI);
            RenderSystem.setShaderTexture(0, this.GUI);
        }
    }

    public void drawFluidTank(CapabilityFluidDisplayWrapper pWrapper, int pPosX, int pPosY) {
        drawFluidTank(pWrapper, pPosX, pPosY, 16, 60);
    }

    public void drawFluidTank(CapabilityFluidDisplayWrapper pWrapper, int pPosX, int pPosY, int pWidth, int pHeight) {
        if (pWrapper.getStored() > 5) {
            bindBlockTexture();
            renderGuiTank(pWrapper.getHandler().getFluidInTank(0),
                    pWrapper.getMaxStored(),
                    pWrapper.getStored(),
                    pPosX, pPosY,
                    getBlitOffset(),
                    pWidth, pHeight);
        }
    }

    public void bindWidgets() {
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png"));
    }

    public void drawRightArrow(PoseStack ps, int x, int y, int width) {
        int height = 9;
        bindWidgets();
        blit(ps, x, y, 0, 120, width, height);
    }

    public void drawDownArrow(PoseStack ps, int x, int y, int height) {
        int width = 9;
        bindWidgets();
        blit(ps, x, y, 9, 129, width, height);
    }

    public void drawUpArrow(PoseStack ps, int x, int y, int height) {
        int width = 9;
        bindWidgets();
        blit(ps, x, y, 0, 129, width, height);
    }

    @SuppressWarnings("deprecation")
    public static void bindBlockTexture() {
        Minecraft.getInstance().textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);
    }

    public static TextureAtlasSprite getStillTexture(Fluid fluid) {
        ResourceLocation iconKey = fluid.getAttributes().getStillTexture();
        if (iconKey == null) return null;
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(iconKey);
    }

    public static void renderGuiTank(FluidStack pFluidStack, int pCapacity, int pAmount, double pX, double pY, double pZ, double pWidth, double pHeight) {
        if (pFluidStack == null || pFluidStack.getFluid() == null || pFluidStack.getAmount() <= 0) return;

        TextureAtlasSprite icon;
        if (getStillTexture(pFluidStack.getFluid()) != null) {
            icon = getStillTexture(pFluidStack.getFluid());
        } else {
            return;
        }

        int renderAmount = (int) Math.max(Math.min(pHeight, pAmount * pHeight / pCapacity), 1.0);
        int posY = (int) (pY + pHeight - renderAmount);

        //RenderUtils.bindBlockTexture();
        int color = pFluidStack.getFluid().getAttributes().getColor();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        setGLColorFromInt(color);//GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

        //Minecraft.getInstance().getEntityRenderDispatcher().textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);

        //GlStateManager.enableBlend();
        //RenderSystem.enableBlend();
        int i = 0;
        while (i < pWidth) {
            int j = 0;
            while (j < renderAmount) {
                int drawWidth = (int) Math.min(pWidth - i, 16.0);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (pX + i);
                int drawY = posY + j;

                float minU = icon.getU0();
                float maxU = icon.getU1();
                float minV = icon.getV0();
                float maxV = icon.getV1();

                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder buf = tessellator.getBuilder();
                buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buf.vertex(drawX, (drawY + drawHeight), 0.0).uv(minU, minV + (maxV - minV) * drawHeight / 16f).endVertex();
                buf.vertex((drawX + drawWidth), (drawY + drawHeight), 0.0).uv(minU + (maxU - minU) * drawWidth / 16f, minV + (maxV - minV) * drawHeight / 16f).endVertex();
                buf.vertex((drawX + drawWidth), drawY, 0.0).uv(minU + (maxU - minU) * drawWidth / 16f, minV).endVertex();
                buf.vertex(drawX, drawY, 0.0).uv(minU, minV).endVertex();
                tessellator.end();
                j += 16;
            }
            i += 16;
        }

        //RenderSystem.disableBlend();
        //GlStateManager.disableBlend();
    }

    private static void setGLColorFromInt(int pColor) {
        Color color = new Color(pColor);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

//        RenderSystem.setShaderColor(red, green, blue, 1.0F);
    }
}