package com.smashingmods.alchemistry.client.container.button;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.block.fusion.FusionControllerBlockEntity;
import com.smashingmods.alchemistry.common.network.ToggleAutoBalanceButtonPacket;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.PacketDistributor;

public class AutoBalanceButton extends AbstractAlchemyButton {

    public AutoBalanceButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            if (pParent.getBlockEntity() instanceof FusionControllerBlockEntity fusionControllerBlockEntity) {
                boolean toggleAutoBalance = !fusionControllerBlockEntity.isAutoBalanced();
                fusionControllerBlockEntity.setAutoBalanced(toggleAutoBalance);
                fusionControllerBlockEntity.setChanged();
                PacketDistributor.sendToServer(new ToggleAutoBalanceButtonPacket(fusionControllerBlockEntity.getBlockPos(), toggleAutoBalance));
            }
        });
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(AlchemyLib.modLoc("textures/gui/widgets.png"), getX(), getY(), 25 + ((((FusionControllerBlockEntity) blockEntity).isAutoBalanced() ? 0 : 1) * 20), 40, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return ((FusionControllerBlockEntity) blockEntity).isAutoBalanced() ?
                Component.translatable("alchemistry.container.disable_autobalance")
                :
                Component.translatable("alchemistry.container.enable_autobalance");
    }
}

