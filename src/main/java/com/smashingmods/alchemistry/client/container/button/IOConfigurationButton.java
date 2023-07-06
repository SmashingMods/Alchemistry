package com.smashingmods.alchemistry.client.container.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.client.container.SideModeConfigurationScreen;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class IOConfigurationButton extends AbstractAlchemyButton {

    public IOConfigurationButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            BlockEntity entity = pParent.getBlockEntity();
            SideModeConfigurationScreen screen = new SideModeConfigurationScreen(entity);
            Minecraft.getInstance().pushGuiLayer(screen);
        });
    }

    @Override
    public void renderWidget(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, getX(), getY(), 85, 0, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return Component.translatable("alchemistry.container.sides.button");
    }
}
