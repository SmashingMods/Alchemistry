package com.smashingmods.alchemistry.api.blockentity.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import com.smashingmods.alchemistry.common.network.ToggleAutoBalanceButtonPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class AutoBalanceButton extends Button {

    private final Screen parent;
    private final FusionControllerBlockEntity blockEntity;

    public AutoBalanceButton(Screen pParent, FusionControllerBlockEntity pBlockEntity) {
        super(0,
                0,
                20,
                20,
                TextComponent.EMPTY,
                pButton -> {
                    boolean toggleAutoBalance = !pBlockEntity.isAutoBalanced();
                    pBlockEntity.setAutoBalanced(toggleAutoBalance);
                    pBlockEntity.setChanged();
                    PacketHandler.INSTANCE.sendToServer(new ToggleAutoBalanceButtonPacket(pBlockEntity.getBlockPos(), toggleAutoBalance));
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

        blit(pPoseStack, x, y, 25 + ((blockEntity.isAutoBalanced() ? 0 : 1) * 20), 40, width, height);
        renderButtonToolTip(pPoseStack, pMouseX, pMouseY);
    }

    public void renderButtonToolTip(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            TranslatableComponent component = blockEntity.isAutoBalanced() ? new TranslatableComponent("alchemistry.container.enable_autobalance") : new TranslatableComponent("alchemistry.container.disable_autobalance");
            parent.renderTooltip(pPoseStack, component, pMouseX, pMouseY);
        }
    }
}
