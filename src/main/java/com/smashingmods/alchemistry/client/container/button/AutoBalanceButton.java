package com.smashingmods.alchemistry.client.container.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.network.ToggleAutoBalanceButtonPacket;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import javax.annotation.Nonnull;

public class AutoBalanceButton extends AbstractAlchemyButton {

    public AutoBalanceButton(AbstractProcessingScreen<?> pParent, FusionControllerBlockEntity pBlockEntity) {
        super(pParent, pBlockEntity, pButton -> {
                    boolean toggleAutoBalance = !pBlockEntity.isAutoBalanced();
                    pBlockEntity.setAutoBalanced(toggleAutoBalance);
                    pBlockEntity.setChanged();
                    Alchemistry.PACKET_HANDLER.sendToServer(new ToggleAutoBalanceButtonPacket(pBlockEntity.getBlockPos(), toggleAutoBalance));
                });
    }

    @Override
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 25 + ((((FusionControllerBlockEntity) blockEntity).isAutoBalanced() ? 0 : 1) * 20), 40, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return ((FusionControllerBlockEntity) blockEntity).isAutoBalanced() ? MutableComponent.create(new TranslatableContents("alchemistry.container.disable_autobalance")) : MutableComponent.create(new TranslatableContents("alchemistry.container.enable_autobalance"));
    }
}
