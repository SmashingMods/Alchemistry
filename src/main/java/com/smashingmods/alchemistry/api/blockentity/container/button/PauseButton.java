package com.smashingmods.alchemistry.api.blockentity.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.common.network.TogglePauseButtonPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class PauseButton extends Button {

    private final Screen parent;
    private final AbstractProcessingBlockEntity blockEntity;

    public PauseButton(Screen pParent, AbstractProcessingBlockEntity pBlockEntity) {
        super(0,
                0,
                20,
                20,
                TextComponent.EMPTY,
                pButton -> {
                    boolean togglePause = !pBlockEntity.isProcessingPaused();
                    pBlockEntity.setPaused(!togglePause);
                    pBlockEntity.setChanged();
                    PacketHandler.INSTANCE.sendToServer(new TogglePauseButtonPacket(pBlockEntity.getBlockPos(), togglePause));
                });
        this.parent = pParent;
        this.blockEntity = pBlockEntity;
    }

    @Override
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation buttonTexture = new ResourceLocation(Alchemistry.MODID, "textures/gui/widgets.png");

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, buttonTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        blit(pPoseStack, x, y, 25 + ((blockEntity.isProcessingPaused() ? 1 : 0) * 20), 20, width, height);
        renderButtonToolTip(pPoseStack, pMouseX, pMouseY);
    }

    public void renderButtonToolTip(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            TranslatableComponent component = blockEntity.isProcessingPaused() ? new TranslatableComponent("alchemistry.container.resume") : new TranslatableComponent("alchemistry.container.pause");
            parent.renderTooltip(pPoseStack, component, pMouseX, pMouseY);
        }
    }
}
