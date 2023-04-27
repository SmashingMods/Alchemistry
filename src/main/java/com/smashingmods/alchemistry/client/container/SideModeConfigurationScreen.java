package com.smashingmods.alchemistry.client.container;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SideModeConfigurationScreen extends Screen {

    // Side mod configuration screen layout:
    // N: North
    // E: East
    // S: South
    // W: West
    // U: Up
    // D: Down
    // X: Null/External

    // U | N | _
    // W | X | E
    // _ | S | D

    private final BlockEntity owner;
    @Nullable
    private List<Component> drawnTooltip = null;

    public SideModeConfigurationScreen(BlockEntity owner) {
        super(Component.translatable("alchemistry.container.sides.title"));
        this.owner = owner;
    }

    @Override
    protected void init() {
        addRenderableWidget(new SideConfigButton(this, 0, 0, Direction.UP));
        addRenderableWidget(new SideConfigButton(this, 1, 0, Direction.NORTH));
        addRenderableWidget(new SideConfigButton(this, 0, 1, Direction.WEST));
        addRenderableWidget(new SideConfigButton(this, 1, 1, null));
        addRenderableWidget(new SideConfigButton(this, 2, 1, Direction.EAST));
        addRenderableWidget(new SideConfigButton(this, 1, 2, Direction.SOUTH));
        addRenderableWidget(new SideConfigButton(this, 2, 2, Direction.DOWN));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        drawnTooltip = null;
        renderBackground(pPoseStack);
        drawCenteredString(pPoseStack, font, title, width / 2, getMinY() - 5, 0xFF_FF_FF_FF);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (drawnTooltip != null) {
            renderComponentTooltip(pPoseStack, drawnTooltip, pMouseX, pMouseY);
        }
    }

    @Override
    public void renderBackground(PoseStack pPoseStack) {
        super.renderBackground(pPoseStack);
        fill(pPoseStack, getMinX(), getMinY() - 10, getMaxX(), getMaxY(), 0xFF_30_30_30);
    }

    public int getRasterStartX() {
        return (width - (3 * 20 + 4)) / 2;
    }

    public int getMinX() {
        return (width - Math.max(3 * 20 + 4, font.width(title))) / 2;
    }

    public int getMinY() {
        return (height - (3 * 20 + 4)) / 2;
    }

    public int getMaxX() {
        return (width + Math.max(3 * 20 + 4, font.width(title))) / 2;
    }

    public int getMaxY() {
        return (height + (3 * 20 + 4)) / 2;
    }

    public void setDrawnTooltip(List<Component> drawnTooltip) {
        this.drawnTooltip = drawnTooltip;
    }

    public SidedProcessingSlotWrapper getInventory() {
        return ((InventoryBlockEntity) owner).getCombinedSlotHandler();
    }

    public BlockEntity getOwner() {
        return owner;
    }
}
