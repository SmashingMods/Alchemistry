package com.smashingmods.alchemistry.client.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.InventoryBlockEntity;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class SideModeConfigurationScreen extends Screen {

    private static final ResourceLocation TEXTURE_SOURCE = new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png");

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
        addChildButton(new SideConfigButton(this, 0, 0, Direction.UP));
        addChildButton(new SideConfigButton(this, 1, 0, Direction.NORTH));
        addChildButton(new SideConfigButton(this, 0, 1, Direction.WEST));
        addChildButton(new SideConfigButton(this, 1, 1, null));
        addChildButton(new SideConfigButton(this, 2, 1, Direction.EAST));
        addChildButton(new SideConfigButton(this, 1, 2, Direction.SOUTH));
        addChildButton(new SideConfigButton(this, 2, 2, Direction.DOWN));
    }

    private void addChildButton(SideConfigButton button) {
        addWidget(button);
        addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        drawnTooltip = null;
        renderBackground(pGuiGraphics);
        pGuiGraphics.drawCenteredString(font, title, width / 2, getMinY() - 7, 0xFF_FFFFFF);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (drawnTooltip != null) {
            pGuiGraphics.renderComponentTooltip(font, drawnTooltip, pMouseX, pMouseY);
        }
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
        // Blitting a Ninepatch to screen by hand - because why not?
        pGuiGraphics.blit(TEXTURE_SOURCE, getMinX() - 4, getMinY() - 14, 4, 4, 0, 146, 4, 4, 256, 256); // Upper left corner
        pGuiGraphics.blit(TEXTURE_SOURCE, getMinX() - 4, getMaxY(), 4, 4, 0, 151, 4, 4, 256, 256); // Lower left corner
        pGuiGraphics.blit(TEXTURE_SOURCE, getMaxX(), getMinY() - 14, 4, 4, 5, 146, 4, 4, 256, 256); // Upper right corner
        pGuiGraphics.blit(TEXTURE_SOURCE, getMaxX(), getMaxY(), 4, 4, 5, 151, 4, 4, 256, 256); // Lower right corner
        pGuiGraphics.blit(TEXTURE_SOURCE, getMinX(), getMinY() - 14, getMaxX() - getMinX(), 4, 4, 146, 1, 4, 256, 256); // Upper edge
        pGuiGraphics.blit(TEXTURE_SOURCE, getMinX(), getMaxY(), getMaxX() - getMinX(), 4, 4, 151, 1, 4, 256, 256); // Lower edge
        pGuiGraphics.blit(TEXTURE_SOURCE, getMinX() - 4, getMinY() - 10, 4, getMaxY() - getMinY() + 10, 0, 150, 4, 1, 256, 256); // Left edge
        pGuiGraphics.blit(TEXTURE_SOURCE, getMaxX(), getMinY() - 10, 4, getMaxY() - getMinY() + 10, 5, 150, 4, 1, 256, 256); // Right edge
        pGuiGraphics.blit(TEXTURE_SOURCE, getMinX(), getMinY() - 10, getMaxX() - getMinX(), getMaxY() - getMinY() + 10, 4, 150, 1, 1, 256, 256); // Fill
    }

    public int getRasterStartX() {
        return (width - (3 * 20 + 4)) / 2;
    }

    public int getMinX() {
        return (width - Math.max(3 * 20, font.width(title)) - 4) / 2;
    }

    public int getMinY() {
        return (height - (3 * 20 + 4)) / 2;
    }

    public int getMaxX() {
        return (width + Math.max(3 * 20, font.width(title)) + 4) / 2;
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
