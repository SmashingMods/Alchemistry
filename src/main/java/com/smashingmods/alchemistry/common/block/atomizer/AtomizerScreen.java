package com.smashingmods.alchemistry.common.block.atomizer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryScreen;
import com.smashingmods.alchemistry.api.container.DisplayData;
import com.smashingmods.alchemistry.api.container.EnergyDisplayData;
import com.smashingmods.alchemistry.api.container.FluidDisplayData;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AtomizerScreen extends AbstractAlchemistryScreen<AtomizerMenu> {

    protected final List<DisplayData> displayData = new ArrayList<>();

    public AtomizerScreen(AtomizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 8, 21, 16, 46));
        displayData.add(new FluidDisplayData(pMenu.getBlockEntity(), pMenu.getContainerData(), 44, 21, 16, 46));
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        ResourceLocation bgTexture = new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png");
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, bgTexture);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component displayname = new TranslatableComponent("alchemistry.container.atomizer");
        drawString(pPoseStack, this.font, displayname, this.imageWidth / 2 - this.font.width(displayname) / 2, -10, Color.WHITE.getRGB());
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pDelta) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pDelta, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pDelta);

        int drawX = (this.width - this.imageWidth) / 2;
        int drawY = (this.height - this.imageHeight) / 2;

        displayData.forEach(data -> {
            if (data instanceof EnergyDisplayData) {
                this.drawEnergyBar(pPoseStack, (EnergyDisplayData) data, 0, 0);
            }
            if (data instanceof FluidDisplayData) {
                this.drawFluidTank((FluidDisplayData) data, drawX + data.getX(), drawY + data.getY());
            }
        });

        int progress = this.getMenu().getContainerData().get(0);
        int maxProgress = this.getMenu().getContainerData().get(1);
        if (progress > 0) {
            int barScaled = this.getBarScaled(60, progress, maxProgress);
            this.drawRightArrow(pPoseStack, drawX + 74, drawY + 39, barScaled);
        }

        displayData.stream().filter(data ->
                pMouseX >= data.getX() + drawX &&
                        pMouseX <= data.getX() + drawX + data.getWidth() &&
                        pMouseY >= data.getY() + drawY &&
                        pMouseY <= data.getY() + drawY + data.getHeight()
        ).forEach(displayWrapper -> renderTooltip(pPoseStack, displayWrapper.toTextComponent(), pMouseX, pMouseY));

        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }
}