package com.smashingmods.alchemistry.api.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class OpenScreenButton extends Button {

    public OpenScreenButton(int pX, int pY, Screen pParentScreen, Screen pNewScreen) {
        super(pX, pY, 20, 20, TextComponent.EMPTY,
                pButton -> ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), pNewScreen),
                (pButton, pPoseStack, pMouseX, pMouseY) -> pParentScreen.renderTooltip(pPoseStack, new TextComponent("Open Test Screen"), pMouseX, pMouseY));
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        ResourceLocation buttonTexture = new ResourceLocation(Alchemistry.MODID, "textures/gui/button.png");

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, buttonTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        int i = getYImage(isHoveredOrFocused());

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        blit(pPoseStack, x, y, 0, (i - 1) * 20, width, height);
        blit(pPoseStack, x + width / 2, y, 200 - width / 2, 46 + i * 20, width, height);

        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private void renderButtonTooltip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }
}
