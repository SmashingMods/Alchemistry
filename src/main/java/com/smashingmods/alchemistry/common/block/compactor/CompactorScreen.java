package com.smashingmods.alchemistry.common.block.compactor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CompactorScreen extends AbstractAlchemistryScreen<CompactorMenu> {

    protected final List<DisplayData> displayData = new ArrayList<>();

    public CompactorScreen(CompactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = 184;
        imageHeight = 163;

        displayData.add(new ProgressDisplayData(pMenu.getContainerData(), 0, 1, 75, 39, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 2, 3, 17, 16, 16, 54));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        this.renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/compactor_gui.png"));
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.compactor");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, ChatFormatting.WHITE.getColor());
    }
}
