package com.smashingmods.alchemistry.client.container.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.client.container.SideModeConfigurationScreen;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 85, 0, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return new TranslatableComponent("alchemistry.container.sides.button");
    }
}
