package com.smashingmods.alchemistry.client.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.SetSideConfigurationPacket;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.storage.SideMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class SideConfigButton extends AbstractWidget {

    private static final ResourceLocation ICONS_LOCATION = new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png");
    private static final ResourceLocation BARRIER_LOCATION = new ResourceLocation("minecraft", "textures/item/barrier.png");
    private final SideModeConfigurationScreen parentScreen;
    @Nullable
    private final Direction side;
    private final List<Component> tooltip;

    private final int baseOffsetX;
    private final int baseOffsetY;

    SideConfigButton(SideModeConfigurationScreen parentScreen, int rasterX, int rasterY, @Nullable Direction side) {
        super(0, 0, 16, 16, Component.empty());
        this.parentScreen = parentScreen;
        this.baseOffsetX = 4 + 20 * rasterX;
        this.baseOffsetY = 4 + 20 * rasterY;
        this.side = side;
        this.tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("alchemistry.container.sides." + (side == null ? "external" : side.getSerializedName())));
        tooltip.add(Component.literal(""));
        tooltip.add(getCurrentModeComponent());
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, tooltip.get(0).plainCopy().append(" ").append(tooltip.get(2)));
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        setX(baseOffsetX + parentScreen.getRasterStartX());
        setY(baseOffsetY + parentScreen.getMinY());
        int backgroundColor;
        if (isHoveredOrFocused()) {
            backgroundColor = 0xFF_FFFFFF;
        } else {
            backgroundColor = 0xFF_D0D0D0;
        }
        fill(pPoseStack, getX(), getY(), getX() + width, getY() + width, 0xFF_000000); // outline
        fill(pPoseStack, getX() + 1, getY() + 1, getX() + width - 1, getY() + width - 1, backgroundColor);

        switch (getCurrentMode()) {
            case DISABLED -> {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, BARRIER_LOCATION);
                blit(pPoseStack, getX() + 4, getY() + 4, width - 8, height - 8, 0, 0, 16, 16, 16, 16); // Barrier
            }
            case ENABLED -> {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, ICONS_LOCATION);
                blit(pPoseStack, getX() + 4, getY() + 4, width - 8, height - 8, 0, 154, 9, 8, 256, 256); // Checkmark
            }
            case PULL -> {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, ICONS_LOCATION);
                blit(pPoseStack, getX() + 4, getY() + 4, width - 8, height - 8, 9, 154, 9, 9, 256, 256); // Orange hollow circle
            }
            case PUSH -> {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, ICONS_LOCATION);
                blit(pPoseStack, getX() + 4, getY() + 4, width - 8, height - 8, 9, 145, 9, 9, 256, 256); // Blue filled circle
            }
            default -> throw new AssertionError("Unexpected mode: " + getCurrentMode());
        }

        if (isHovered) {
            parentScreen.setDrawnTooltip(tooltip);
        }
    }

    private Component getCurrentModeComponent() {
        return Component.translatable("alchemistry.container.sides.current")
                .append(Component.translatable("alchemistry.container.sides.mode." + getCurrentMode().name().toLowerCase(Locale.ROOT)));
    }

    public SideMode getCurrentMode() {
        return parentScreen.getInventory().getSideMode(side);
    }

    private void changeMode(int change) {
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        int ordinal = (getCurrentMode().ordinal() + change) % 4;
        if (ordinal < 0) {
            ordinal = 3;
        }
        SideMode newMode = SideMode.getFromOrdinal(ordinal);
        parentScreen.getInventory().setSideMode(side, newMode);
        Alchemistry.PACKET_HANDLER.sendToServer(new SetSideConfigurationPacket(parentScreen.getOwner().getBlockPos(), parentScreen.getInventory().sideModesToShort()));
        tooltip.set(2, getCurrentModeComponent());
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (active && visible && clicked(pMouseX, pMouseY)) {
            int delta;
            if (pButton == 0) {
                delta = -1;
            } else if (pButton == 1) {
                delta = 1;
            } else {
                return false;
            }
            changeMode(delta);
            return true;
         }
        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (active && visible
                && (pKeyCode == GLFW.GLFW_KEY_ENTER || pKeyCode == GLFW.GLFW_KEY_KP_ENTER || pKeyCode == GLFW.GLFW_KEY_SPACE)) {
            changeMode(1);
            return true;
        }
        return false;
    }
}
