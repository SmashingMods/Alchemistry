package com.smashingmods.alchemistry.common.block.atomizer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemistry.api.blockentity.container.Direction2D;
import com.smashingmods.alchemistry.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.FluidDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractFluidBlockEntity;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class AtomizerScreen extends AbstractProcessingScreen<AtomizerMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();

    public AtomizerScreen(AtomizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, Alchemistry.MODID);
        displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 78, 35, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 12, 12, 16, 54));
        displayData.add(new FluidDisplayData((AbstractFluidBlockEntity) pMenu.getBlockEntity(), 48, 12, 16, 54));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png"));
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.atomizer");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }
}