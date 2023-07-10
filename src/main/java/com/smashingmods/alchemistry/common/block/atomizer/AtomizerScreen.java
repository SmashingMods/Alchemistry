package com.smashingmods.alchemistry.common.block.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.container.button.IOConfigurationButton;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.FluidDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.client.button.PauseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class AtomizerScreen extends AbstractProcessingScreen<AtomizerMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();
    private final PauseButton pauseButton = new PauseButton(this);
    private final IOConfigurationButton sideConfigButton = new IOConfigurationButton(this);

    public AtomizerScreen(AtomizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 78, 35, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 12, 12, 16, 54));
        displayData.add(new FluidDisplayData((AbstractFluidBlockEntity) pMenu.getBlockEntity(), 48, 12, 16, 54));
    }

    @Override
    protected void init() {
        widgets.add(pauseButton);
        widgets.add(sideConfigButton);
        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        renderDisplayData(displayData, pGuiGraphics, leftPos, topPos);
        renderDisplayTooltip(displayData, pGuiGraphics, leftPos, topPos, pMouseX, pMouseY);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        MutableComponent title = MutableComponent.create(new TranslatableContents("alchemistry.container.atomizer", null, TranslatableContents.NO_ARGS));
        pGuiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }
}