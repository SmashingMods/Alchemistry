package com.smashingmods.alchemistry.api.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.ProcessingButtonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public abstract class AbstractAlchemistryScreen<M extends AbstractAlchemistryMenu> extends AbstractContainerScreen<M> {

    protected final Button lockButton;
    protected final Button unlockButton;

    protected final Button pauseButton;
    protected final Button resumeButton;

    public AbstractAlchemistryScreen(M pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        lockButton = new Button(0, 0, 100, 20, new TranslatableComponent("alchemistry.container.lock_recipe"), handleLock());
        unlockButton = new Button(0, 0, 100, 20, new TranslatableComponent("alchemistry.container.unlock_recipe"), handleLock());
        pauseButton = new Button(0, 0, 100, 20, new TranslatableComponent("alchemistry.container.pause"), handlePause());
        resumeButton = new Button(0, 0, 100, 20, new TranslatableComponent("alchemistry.container.resume"), handlePause());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderWidgets();
    }

    public void drawFluidTank(FluidDisplayData pData, int pTextureX, int pTextureY) {
        if (pData.getValue() > 0) {
            FluidStack fluidStack = pData.getFluidHandler().getFluidStack();
            setShaderColor(fluidStack.getFluid().getAttributes().getColor());
            TextureAtlasSprite icon = getResourceTexture(fluidStack.getFluid().getAttributes().getStillTexture());
            drawTexture(pData, icon, pTextureX, pTextureY);
        }
    }

    public void drawTexture(DisplayData pData, TextureAtlasSprite pSprite, int pTextureX, int pTextureY) {

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

    public static int getBarScaled(int pPixels, int pProgress, int pMaxProgress) {
        if (pProgress > 0 && pMaxProgress > 0) {
            return pProgress * pPixels / pMaxProgress;
        } else {
            return 0;
        }
    }

    public void drawEnergyBar(PoseStack pPoseStack, EnergyDisplayData pData, int pTextureX, int pTextureY) {
        int x = pData.getX() + (this.width - this.imageWidth) / 2;
        int y = pData.getY() + (this.height - this.imageHeight) / 2;
        this.directionalBlit(pPoseStack, x, y + pData.getHeight(), pTextureX, pTextureY, pData.getWidth(), pData.getHeight(), pData.getValue(), pData.getMaxValue(), Direction2D.UP);
    }

    private void directionalBlit(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pU, int pV, int pProgress, int pMaxProgress, Direction2D pDirection2D) {
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png"));

        switch (pDirection2D) {
            case LEFT -> {
                int scaled = getBarScaled(pV, pProgress, pMaxProgress);
                blit(pPoseStack, pX - scaled, pY, pUOffset + pU - scaled, pVOffset, scaled, pV);
            }
            case UP -> {
                int scaled = getBarScaled(pV, pProgress, pMaxProgress);
                blit(pPoseStack, pX, pY - scaled, pUOffset, pVOffset + pV - scaled, pU, scaled);
            }
            case RIGHT -> {
                int scaled = getBarScaled(pV, pProgress, pMaxProgress);
                blit(pPoseStack, pX, pY, pUOffset, pVOffset, scaled, pU);
            }
            case DOWN -> {
                int scaled = getBarScaled(pV, pProgress, pMaxProgress);
                blit(pPoseStack, pX, pY, pUOffset, pVOffset, pU, scaled);
            }
        }
    }

    public void directionalArrow(PoseStack pPoseStack, int pX, int pY, int pProgress, int pMaxProgress, Direction2D pDirection2D) {
        switch (pDirection2D) {
            case LEFT -> directionalBlit(pPoseStack, pX, pY, 0, 120, 9, 30, pProgress, pMaxProgress, Direction2D.LEFT);
            case UP -> directionalBlit(pPoseStack, pX, pY, 0, 138, 9, 30, pProgress, pMaxProgress, Direction2D.UP);
            case RIGHT -> directionalBlit(pPoseStack, pX, pY, 0, 129, 9, 30, pProgress, pMaxProgress, Direction2D.RIGHT);
            case DOWN -> directionalBlit(pPoseStack, pX, pY, 9, 138, 9, 30, pProgress, pMaxProgress, Direction2D.DOWN);
        }
    }

    public void renderDisplayData(List<DisplayData> pDisplayData, PoseStack pPoseStack, int pX, int pY) {
        pDisplayData.forEach(data -> {
            if (data instanceof ProgressDisplayData) {
                directionalArrow(pPoseStack, pX + data.getX(), pY + data.getY(), data.getValue(), data.getMaxValue(), ((ProgressDisplayData) data).getDirection());
            }
            if (data instanceof EnergyDisplayData) {
                drawEnergyBar(pPoseStack, (EnergyDisplayData) data, 0, 40);
            }
            if (data instanceof FluidDisplayData) {
                drawFluidTank((FluidDisplayData) data, pX + data.getX(), pY + data.getY());
            }
        });
    }

    public void renderDisplayTooltip(List<DisplayData> pDisplayData, PoseStack pPoseStack, int pX, int pY, int pMouseX, int pMouseY) {
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

    public <W extends GuiEventListener & Widget & NarratableEntry> void renderWidget(W pWidget, int pX, int pY) {
        if (!renderables.contains(pWidget)) {
            if (pWidget instanceof AbstractWidget widget) {
                widget.x = pX;
                widget.y = pY;
            }
            addRenderableWidget(pWidget);
        }
    }

    public void renderWidgets() {
        renderables.clear();
        if (menu.getBlockEntity().isRecipeLocked()) {
            renderWidget(unlockButton, leftPos - 104, topPos);
        } else {
            renderWidget(lockButton, leftPos - 104, topPos);
        }
        if (menu.getBlockEntity().isProcessingPaused()) {
            renderWidget(resumeButton, leftPos - 104, topPos + 24);
        } else {
            renderWidget(pauseButton, leftPos - 104, topPos + 24);
        }
    }

    private Button.OnPress handleLock() {
        return pButton -> {
            boolean lockState = menu.getBlockEntity().isRecipeLocked();
            boolean pausedState = menu.getBlockEntity().isProcessingPaused();
            AlchemistryPacketHandler.INSTANCE.sendToServer(new ProcessingButtonPacket(menu.getBlockEntity().getBlockPos(), !lockState, pausedState));
        };
    }

    private Button.OnPress handlePause() {
        return pButton -> {
            boolean lockState = menu.getBlockEntity().isRecipeLocked();
            boolean pausedState = menu.getBlockEntity().isProcessingPaused();
            AlchemistryPacketHandler.INSTANCE.sendToServer(new ProcessingButtonPacket(menu.getBlockEntity().getBlockPos(), lockState, !pausedState));
        };
    }
}