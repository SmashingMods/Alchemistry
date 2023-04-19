package com.smashingmods.alchemistry.client.container.button;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.reactor.AbstractReactorBlockEntity;
import com.smashingmods.alchemistry.common.network.SetReactorIOModePacket;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ReactorOutputBehaviourButton extends AbstractAlchemyButton {

    public ReactorOutputBehaviourButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            if (pParent.getBlockEntity() instanceof AbstractReactorBlockEntity reactorControllerBlockEntity) {
                boolean activeOutput = !reactorControllerBlockEntity.isPushingOutputActively();
                reactorControllerBlockEntity.setPushingOutputActively(activeOutput);
                reactorControllerBlockEntity.setChanged();
                Alchemistry.PACKET_HANDLER.sendToServer(new SetReactorIOModePacket(reactorControllerBlockEntity.getBlockPos(), false, activeOutput));
            }
        });
    }

    @Override
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 65 + ((((AbstractReactorBlockEntity) blockEntity).isPushingOutputActively() ? 1 : 0) * 20), 0, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return ((AbstractReactorBlockEntity) blockEntity).isPushingOutputActively() ? MutableComponent.create(new TranslatableContents("alchemistry.container.disable_active_pushing")) : MutableComponent.create(new TranslatableContents("alchemistry.container.enable_active_pushing"));
    }
}
