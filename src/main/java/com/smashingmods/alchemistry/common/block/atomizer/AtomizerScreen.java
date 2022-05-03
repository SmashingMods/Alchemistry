package com.smashingmods.alchemistry.common.block.atomizer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.*;
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
        displayData.add(new ProgressDisplayData(pMenu.getContainerData(), 58, 39, 60, 9, Direction.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 134, 21, 16, 46));
        displayData.add(new FluidDisplayData(pMenu.getBlockEntity(), pMenu.getContainerData(), 26, 21, 16, 46));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        this.renderDisplayData(displayData, pPoseStack, this.leftPos, this.topPos);
        this.renderDisplayTooltip(displayData, pPoseStack, this.leftPos, this.topPos, pMouseX, pMouseY);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png"));
        this.blit(pPoseStack, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.atomizer");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, Color.WHITE.getRGB());
    }
}