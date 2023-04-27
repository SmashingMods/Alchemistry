package com.smashingmods.alchemistry.client.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.SetSideConfigurationPacket;
import com.smashingmods.alchemylib.api.storage.SideMode;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

class SideConfigButton extends AbstractWidget {

    private final SideModeConfigurationScreen parentScreen;
    @Nullable
    private final Direction side;
    private final List<Component> tooltip;

    private final int baseOffsetX;
    private final int baseOffsetY;

    SideConfigButton(SideModeConfigurationScreen parentScreen, int rasterX, int rasterY, @Nullable Direction side) {
        super(0, 0, 16, 16, null);
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
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, tooltip.get(0));
        pNarrationElementOutput.add(NarratedElementType.HINT, tooltip.get(2));
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        x = baseOffsetX + parentScreen.getRasterStartX();
        y = baseOffsetY + parentScreen.getMinY();
        fill(pPoseStack, x, y, x + width, y + width, 0xFF_DDDDDD);
        switch (getCurrentMode()) {
        case DISABLED:
            // No box
            break;
        case ENABLED:
            fill(pPoseStack, x + 4, y + 4, x + width - 4, y + height - 4, 0xFF_00_FF_00); // Green box
            break;
        case PULL:
            fill(pPoseStack, x + 4, y + 4, x + width - 4, y + height - 4, 0xFF_FF_FF_00); // Yellow box
            break;
        case PUSH:
            fill(pPoseStack, x + 4, y + 4, x + width - 4, y + height - 4, 0xFF_00_00_FF); // Blue box
            break;
        default:
            throw new AssertionError("Unexpected mode: " + getCurrentMode());
        }
        if (isHovered) {
            parentScreen.setDrawnTooltip(tooltip);
        }
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        SideMode newMode = SideMode.getFromOrdinal((getCurrentMode().ordinal() + 1) % 4);
        parentScreen.getInventory().setSideMode(side, newMode);
        Alchemistry.PACKET_HANDLER.sendToServer(new SetSideConfigurationPacket(parentScreen.getOwner().getBlockPos(), parentScreen.getInventory().sideModesToShort()));
        tooltip.set(2, getCurrentModeComponent());
    }

    private Component getCurrentModeComponent() {
        return Component.translatable("alchemistry.container.sides.current")
                .append(Component.translatable("alchemistry.container.sides.mode." + getCurrentMode().name().toLowerCase(Locale.ROOT)));
    }

    public SideMode getCurrentMode() {
        return parentScreen.getInventory().getSideMode(side);
    }
}