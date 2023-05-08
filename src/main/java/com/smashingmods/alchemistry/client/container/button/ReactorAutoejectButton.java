package com.smashingmods.alchemistry.client.container.button;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.network.ToggleReactorAutoejectPacket;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;

import net.minecraft.network.chat.Component;

public class ReactorAutoejectButton extends AbstractAlchemyButton {

    private static final List<Component> TOOLTIP_ENABLED = Arrays.asList(
            Component.translatable("alchemistry.container.autoeject.title.enabled"),
            Component.empty(),
            Component.translatable("alchemistry.container.autoeject.tooltip.enabled"));

    private static final List<Component> TOOLTIP_DISABLED = Arrays.asList(
            Component.translatable("alchemistry.container.autoeject.title.disabled"),
            Component.empty(),
            Component.translatable("alchemistry.container.autoeject.tooltip.disabled"));

    public ReactorAutoejectButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            if (pParent.getBlockEntity() instanceof AbstractReactorBlockEntity reactorControllerBlockEntity) {
                boolean autoeject = !reactorControllerBlockEntity.isAutoEject();
                reactorControllerBlockEntity.setAutoeject(autoeject);
                reactorControllerBlockEntity.setChanged();
                Alchemistry.PACKET_HANDLER.sendToServer(new ToggleReactorAutoejectPacket(reactorControllerBlockEntity.getBlockPos(), autoeject));
            }
        });
    }

    @Override
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 65 + ((((AbstractReactorBlockEntity) blockEntity).isAutoEject() ? 1 : 0) * 20), 0, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public void renderButtonTooltip(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        // You may wonder: Why override #renderButtonTooltip method instead of overriding #getMessage?
        // The answer is simple: For some reason forge doesn't wrap components - especially not on newlines,
        // although according to https://github.com/Darkhax-Minecraft/Enchantment-Descriptions/issues/60#issuecomment-825041462
        // forge does. As a workaround we "wrap" manually simply by using a list of components.
        // As such we invoke Screen#renderComponentTooltip directly and give it the list of components.

        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            boolean autoeject = ((AbstractReactorBlockEntity) blockEntity).isAutoEject();
            parent.renderComponentTooltip(pPoseStack, autoeject ? TOOLTIP_ENABLED : TOOLTIP_DISABLED, pMouseX, pMouseY);
        }
    }
}
