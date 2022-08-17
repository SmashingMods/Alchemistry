package com.smashingmods.alchemistry.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.ForgeHooksClient;

public class TestScreen extends Screen {

    private Button close;

    public TestScreen() {
        super(new TextComponent("Test Screen"));

        close = new Button(0, 0, 100, 20, new TextComponent("Close"), handleClose());
    }

    private Button.OnPress handleClose() {
        return pButton -> {
            if (minecraft != null) {
                ForgeHooksClient.popGuiLayer(minecraft);
            }
        };
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        drawCenteredString(pPoseStack, Minecraft.getInstance().font, new TextComponent("THIS IS A TEST.").withStyle(ChatFormatting.BOLD), width / 2, 24, 0xFFFFFF);
        renderWidgets();

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderWidgets() {
        renderables.clear();
        renderWidget(close, (width - 100) / 2, (height - 20) / 2);
    }

    private <W extends GuiEventListener & Widget & NarratableEntry> void renderWidget(W pWidget, int pX, int pY) {
        if (!renderables.contains(pWidget)) {
            if (pWidget instanceof AbstractWidget widget) {
                widget.x = pX;
                widget.y = pY;
            }
            addRenderableWidget(pWidget);
        }
    }
}
