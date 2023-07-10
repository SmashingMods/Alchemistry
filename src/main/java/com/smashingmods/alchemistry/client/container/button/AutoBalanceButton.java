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
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

public class AutoBalanceButton extends AbstractAlchemyButton {

    public AutoBalanceButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            if (pParent.getBlockEntity() instanceof FusionControllerBlockEntity fusionControllerBlockEntity) {
                boolean toggleAutoBalance = !fusionControllerBlockEntity.isAutoBalanced();
                fusionControllerBlockEntity.setAutoBalanced(toggleAutoBalance);
                fusionControllerBlockEntity.setChanged();
                Alchemistry.PACKET_HANDLER.sendToServer(new ToggleAutoBalanceButtonPacket(fusionControllerBlockEntity.getBlockPos(), toggleAutoBalance));
            }
        });
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 25 + ((((FusionControllerBlockEntity) blockEntity).isAutoBalanced() ? 0 : 1) * 20), 40, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return ((FusionControllerBlockEntity) blockEntity).isAutoBalanced() ?
                MutableComponent.create(new TranslatableContents("alchemistry.container.disable_autobalance", "Disable Auto-Balance", TranslatableContents.NO_ARGS))
                :
                MutableComponent.create(new TranslatableContents("alchemistry.container.enable_autobalance", "Enable Auto-Balance", TranslatableContents.NO_ARGS));
    }
}
