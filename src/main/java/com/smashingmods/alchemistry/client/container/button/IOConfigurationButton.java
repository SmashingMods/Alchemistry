package com.smashingmods.alchemistry.client.container.button;

import com.smashingmods.alchemistry.client.container.SideModeConfigurationScreen;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class IOConfigurationButton extends AbstractAlchemyButton {

    public IOConfigurationButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            BlockEntity entity = pParent.getBlockEntity();
            SideModeConfigurationScreen screen = new SideModeConfigurationScreen(entity);
            Minecraft.getInstance().pushGuiLayer(screen);
        });
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 85, 0, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return MutableComponent.create(new TranslatableContents("alchemistry.container.sides.button", "Input/Output Configuration", TranslatableContents.NO_ARGS));
    }
}
