package com.smashingmods.alchemistry.api.blockentity.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemistry.api.blockentity.container.button.LockButton;
import com.smashingmods.alchemistry.api.blockentity.container.button.PauseButton;
import com.smashingmods.alchemistry.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.FluidDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public abstract class AbstractProcessingScreen<M extends AbstractProcessingMenu> extends AbstractContainerScreen<M> {

    protected final String modId;
    private final AbstractProcessingBlockEntity blockEntity;
    protected final LockButton lockButton;
    protected final PauseButton pauseButton;

    public AbstractProcessingScreen(M pMenu, Inventory pPlayerInventory, Component pTitle, String pModId) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 184;
        this.imageHeight = 162;
        this.modId = pModId;
        this.blockEntity = pMenu.getBlockEntity();
        this.lockButton = new LockButton(this, pMenu.getBlockEntity());
        this.pauseButton = new PauseButton(this, pMenu.getBlockEntity());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderWidget(lockButton, leftPos - 24, topPos);
        renderWidget(pauseButton, leftPos - 24, topPos + 24);
    }

    public void drawFluidTank(FluidDisplayData pData) {
        if (pData.getValue() > 0) {
            FluidStack fluidStack = pData.getFluidHandler().getFluidStack();
            setShaderColor(fluidStack.getFluid().getAttributes().getColor());
            TextureAtlasSprite icon = getResourceTexture(fluidStack.getFluid().getAttributes().getStillTexture());
            drawTexture(pData, icon, leftPos + pData.getX(), topPos + pData.getY());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void drawTexture(AbstractDisplayData pData, TextureAtlasSprite pSprite, int pTextureX, int pTextureY) {

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        int renderAmount = Math.max(Math.min(pData.getHeight(), pData.getValue() * pData.getHeight() / pData.getMaxValue()), 1);
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
    }

    public static TextureAtlasSprite getResourceTexture(ResourceLocation pResourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pResourceLocation);
    }

    public static void setShaderColor(int pColor) {
        float alpha = (pColor >> 24 & 255) / 255f;
        float red = (pColor >> 16 & 255) / 255f;
        float green = (pColor >> 8 & 255) / 255f;
        float blue = (pColor & 255) / 255f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public static int getScaled(int pPixels, int pValue, int pMaxValue) {
        if (pValue > 0 && pMaxValue > 0) {
            return pValue * pPixels / pMaxValue;
        } else {
            return 0;
        }
    }

    public void drawEnergyBar(PoseStack pPoseStack, EnergyDisplayData pData) {
        int x = pData.getX() + (this.width - this.imageWidth) / 2;
        int y = pData.getY() + (this.height - this.imageHeight) / 2;
        directionalBlit(pPoseStack, x, y + pData.getHeight(), 0, 0, pData.getWidth(), pData.getHeight(), pData.getValue(), pData.getMaxValue(), Direction2D.UP, true);
    }

    private void directionalBlit(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pU, int pV, int pValue, int pMaxValue, Direction2D pDirection2D) {
        directionalBlit(pPoseStack, pX, pY, pUOffset, pVOffset, pU, pV, pValue, pMaxValue, pDirection2D, false);
    }

    private void directionalBlit(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pU, int pV, int pValue, int pMaxValue, Direction2D pDirection2D, boolean pScaleOffset) {

        int x = pX;
        int y = pY;
        int uOffset = pUOffset;
        int vOffset = pVOffset;
        int uWidth = pU;
        int vHeight = pV;

        int pVScaled = getScaled(pV, pValue, pMaxValue);
        int pVScalePercent = (int) ((pV * 1.8f) - (pVScaled * 1.8f));
        int finalVOffset = pScaleOffset ? pVScalePercent : pVOffset + pV - pVScaled;

        switch (pDirection2D) {
            case LEFT -> {
                x = pX -pVScaled;
                uOffset = pU - pVScaled;
                uWidth = pVScaled;
            }
            case UP -> {
                y = pY - pVScaled;
                vOffset = finalVOffset;
                vHeight = pVScaled;
            }
            case RIGHT -> {
                uWidth = pVScaled;
                vHeight = pU;
            }
            case DOWN -> vHeight = pVScaled;
        }
        RenderSystem.setShaderTexture(0, new ResourceLocation(modId, "textures/gui/widgets.png"));
        blit(pPoseStack, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    public void directionalArrow(PoseStack pPoseStack, int pX, int pY, ProgressDisplayData pData) {
        int uOffset = 0;
        int vOffset = 100;
        int width = 0;
        int height = 0;
        switch (pData.getDirection()) {
            case LEFT -> {
                height = 9;
                width = 30;
            }
            case UP -> {
                vOffset = vOffset + 18;
                height = 9;
                width = 30;
            }
            case RIGHT -> {
                vOffset = vOffset + 9;
                height = 9;
                width = 30;
            }
            case DOWN -> {
                uOffset = uOffset + 9;
                vOffset = vOffset + 18;
                height = 9;
                width = 30;
            }
        }
        directionalBlit(pPoseStack, pX + pData.getX(), pY + pData.getY(), uOffset, vOffset, height, width, pData.getValue(), pData.getMaxValue(), pData.getDirection());
    }

    public void renderDisplayData(List<AbstractDisplayData> pDisplayData, PoseStack pPoseStack, int pX, int pY) {
        pDisplayData.forEach(data -> {
            if (data instanceof ProgressDisplayData progressData) {
                directionalArrow(pPoseStack, pX, pY, progressData);
            }
            if (data instanceof EnergyDisplayData energyData) {
                drawEnergyBar(pPoseStack, energyData);
            }
            if (data instanceof FluidDisplayData fluidData) {
                drawFluidTank(fluidData);
            }
        });
    }

    public void renderDisplayTooltip(List<AbstractDisplayData> pDisplayData, PoseStack pPoseStack, int pX, int pY, int pMouseX, int pMouseY) {
        pDisplayData.stream().filter(data ->
                pMouseX >= data.getX() + pX &&
                        pMouseX <= data.getX() + pX + data.getWidth() &&
                        pMouseY >= data.getY() + pY &&
                        pMouseY <= data.getY() + pY + data.getHeight()
        ).forEach(data -> {
            if (!(data instanceof ProgressDisplayData)) {
                renderTooltip(pPoseStack, data.toTextComponent(), pMouseX, pMouseY);
            }
        });
    }

    public void renderItemTooltip(PoseStack pPoseStack, ItemStack pItemStack, BaseComponent pComponent, int pMouseX, int pMouseY) {
        renderTooltip(pPoseStack, RecipeDisplayUtil.getItemTooltipComponent(pItemStack, pComponent), Optional.empty(), pMouseX, pMouseY);
    }

    public <W extends GuiEventListener & Widget & NarratableEntry> void renderWidget(W pWidget, int pX, int pY) {
        if (!renderables.contains(pWidget)) {
            if (pWidget instanceof AbstractWidget widget) {
                widget.x = pX;
                widget.y = pY;
            }
            addRenderableWidget(pWidget);
        }
    }

    public AbstractProcessingBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int getLeftPos() {
        return leftPos;
    }

    public int getTopPos() {
        return topPos;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }
}
