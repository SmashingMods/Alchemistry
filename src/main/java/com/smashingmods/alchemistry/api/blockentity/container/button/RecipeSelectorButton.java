package com.smashingmods.alchemistry.api.blockentity.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class RecipeSelectorButton extends Button {

    private final Screen parent;
    private final Component tooltip;
    private final int u;
    private final int v;

    public RecipeSelectorButton(int pX, int pY, Screen pParentScreen, Screen pNewScreen, Component pToolTip, int pU, int pV) {
        super(pX, pY, 20, 20, TextComponent.EMPTY, pButton -> ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), pNewScreen));
        this.parent = pParentScreen;
        this.tooltip = pToolTip;
        this.u = pU;
        this.v = pV;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        ResourceLocation widgets = new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png");
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, widgets);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        blit(pPoseStack, x, y, u, v, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private void renderButtonTooltip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            parent.renderTooltip(pPoseStack, tooltip, pMouseX, pMouseY);
        }
    }
}
