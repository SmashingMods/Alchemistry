package com.smashingmods.alchemistry.common.block.combiner;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryScreen;
import com.smashingmods.alchemistry.api.container.DisplayData;
import com.smashingmods.alchemistry.api.container.EnergyDisplayData;
import com.smashingmods.alchemistry.api.container.ProgressDisplayData;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CombinerScreen extends AbstractAlchemistryScreen<CombinerMenu> {

    protected final List<DisplayData> displayData = new ArrayList<>();

    public CombinerScreen(CombinerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        displayData.add(new ProgressDisplayData(pMenu.getContainerData(), 66, 45, 60, 9));
        displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 134, 21, 16, 46));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int relX = (width - imageWidth) / 2;
        int relY = (height - imageHeight) / 2;
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderDisplayData(displayData, pPoseStack, relX, relY);
        this.renderDisplayTooltip(displayData, pPoseStack, relX, relY, pMouseX, pMouseY);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        int relX = (width - imageWidth) / 2;
        int relY = (height - imageHeight) / 2;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_gui.png"));
        this.blit(pPoseStack, relX, relY, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.combiner");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, Color.WHITE.getRGB());
    }
}
