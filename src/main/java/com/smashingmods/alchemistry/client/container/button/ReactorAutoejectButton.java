package com.smashingmods.alchemistry.client.container.button;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.network.ToggleReactorAutoejectPacket;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 65 + ((((AbstractReactorBlockEntity) blockEntity).isAutoEject() ? 1 : 0) * 20), 0, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public void renderButtonTooltip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // You may wonder: Why override #renderButtonTooltip method instead of overriding #getMessage?
        // The answer is simple: For some reason forge doesn't wrap components - especially not on newlines,
        // although according to https://github.com/Darkhax-Minecraft/Enchantment-Descriptions/issues/60#issuecomment-825041462
        // forge does. As a workaround we "wrap" manually simply by using a list of components.
        // As such we invoke Screen#renderComponentTooltip directly and give it the list of components.

        if (pMouseX >= getX() && pMouseX <= getX() + width && pMouseY >= getX() && pMouseY <= getX() + height) {
            boolean autoeject = ((AbstractReactorBlockEntity) blockEntity).isAutoEject();
            pGuiGraphics.renderTooltip(Minecraft.getInstance().font, autoeject ? TOOLTIP_ENABLED : TOOLTIP_DISABLED, Optional.empty(), pMouseX, pMouseY);
        }
    }
}
