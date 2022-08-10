package com.smashingmods.alchemistry.common.block.compactor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.*;
import com.smashingmods.alchemistry.common.network.AlchemistryPacketHandler;
import com.smashingmods.alchemistry.common.network.CompactorResetPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class CompactorScreen extends AbstractAlchemistryScreen<CompactorMenu> {

    protected final List<DisplayData> displayData = new ArrayList<>();
    private final Button resetTargetButton;

    public CompactorScreen(CompactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = 184;
        imageHeight = 163;

        displayData.add(new ProgressDisplayData(pMenu.getContainerData(), 0, 1, 75, 39, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 2, 3, 17, 16, 16, 54));

        resetTargetButton = new Button(0, 0, 100, 20, MutableComponent.create(new TranslatableContents("alchemistry.container.reset_target")), handleResetTargetButton());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);

        renderTarget(pPoseStack, pMouseX, pMouseY);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/compactor_gui.png"));
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = MutableComponent.create(new TranslatableContents("alchemistry.container.compactor"));
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    @Override
    public void renderWidgets() {
        super.renderWidgets();
        renderWidget(resetTargetButton, leftPos - 104, topPos + 48);
    }

    private void renderTarget(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        ItemStack target = ((CompactorBlockEntity) this.menu.getBlockEntity()).getTarget();

        int xStart = leftPos + 80;
        int xEnd = xStart + 18;
        int yStart = topPos + 12;
        int yEnd = yStart + 18;

        if (!target.isEmpty()) {
            FakeItemRenderer.renderFakeItem(target, xStart, yStart, 0.5f);
            if (pMouseX >= xStart && pMouseX < xEnd && pMouseY >= yStart && pMouseY < yEnd) {
                List<Component> components = new ArrayList<>();
                components.add(0, MutableComponent.create(new TranslatableContents("alchemistry.container.target")).withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                components.addAll(target.getTooltipLines(getMinecraft().player, TooltipFlag.Default.NORMAL));
                renderTooltip(pPoseStack, components, target.getTooltipImage(), pMouseX, pMouseY);
            }
        }
    }

    private Button.OnPress handleResetTargetButton() {
        return pButton -> AlchemistryPacketHandler.INSTANCE.sendToServer(new CompactorResetPacket(menu.getBlockEntity().getBlockPos()));
    }
}
