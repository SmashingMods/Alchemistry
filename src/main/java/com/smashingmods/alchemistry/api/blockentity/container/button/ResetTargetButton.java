package com.smashingmods.alchemistry.api.blockentity.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.compactor.CompactorBlockEntity;
import com.smashingmods.alchemistry.common.network.CompactorResetPacket;
import com.smashingmods.alchemistry.common.network.PacketHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ResetTargetButton extends Button {

    private final Screen parent;

    public ResetTargetButton(Screen pParent, CompactorBlockEntity pBlockEntity) {
        super(0,
                0,
                20,
                20,
                TextComponent.EMPTY,
                pButton -> {
                    pBlockEntity.setRecipe(null);
                    pBlockEntity.setTarget(ItemStack.EMPTY);
                    pBlockEntity.setChanged();
                    PacketHandler.INSTANCE.sendToServer(new CompactorResetPacket(pBlockEntity.getBlockPos()));
                });
        this.parent = pParent;
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

        blit(pPoseStack, x, y, 25, 60, width, height);
        renderButtonToolTip(pPoseStack, pMouseX, pMouseY);
    }

    public void renderButtonToolTip(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            TranslatableComponent component = new TranslatableComponent("alchemistry.container.reset_target");
            parent.renderTooltip(pPoseStack, component, pMouseX, pMouseY);
        }
    }
}
