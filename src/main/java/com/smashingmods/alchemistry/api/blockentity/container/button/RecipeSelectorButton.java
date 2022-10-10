package com.smashingmods.alchemistry.api.blockentity.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.container.AbstractProcessingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class RecipeSelectorButton extends Button {

    private final AbstractProcessingScreen<?> parentScreen;
    private final Component tooltip;

    public RecipeSelectorButton(int pX, int pY, AbstractProcessingScreen<?> pParentScreen, Screen pNewScreen, Component pToolTip) {
        super(pX, pY, 20, 20, TextComponent.EMPTY, pButton -> {
            if (pParentScreen.getBlockEntity().isRecipeSelectorOpen()) {
                pNewScreen.onClose();
            } else {
                ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), pNewScreen);
            }
        });
        this.parentScreen = pParentScreen;
        this.tooltip = pToolTip;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation widgets = new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png");
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, widgets);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        boolean open = parentScreen.getBlockEntity().isRecipeSelectorOpen();
        int u = open ? 25: 45;
        int v = open ? 80 : 60;

        blit(pPoseStack, x, y, u, v, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private void renderButtonTooltip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            parentScreen.renderTooltip(pPoseStack, tooltip, pMouseX, pMouseY);
        }
    }
}
