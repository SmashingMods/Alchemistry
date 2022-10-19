package com.smashingmods.alchemistry.common.block.dissolver;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemistry.api.blockentity.container.Direction2D;
import com.smashingmods.alchemistry.api.blockentity.container.FakeItemRenderer;
import com.smashingmods.alchemistry.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DissolverScreen extends AbstractProcessingScreen<DissolverMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();
    private final DissolverBlockEntity blockEntity;

    public DissolverScreen(DissolverMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, Alchemistry.MODID);
        this.imageWidth = 184;
        this.imageHeight = 200;
        displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 69, 35, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 12, 12, 16, 54));
        this.blockEntity = (DissolverBlockEntity) pMenu.getBlockEntity();
    }

    @Override
    protected void init() {
        widgets.add(pauseButton);
        super.init();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderDisplayData(displayData, pPoseStack, this.leftPos, this.topPos);
        renderCurrentRecipe(pPoseStack, pMouseX, pMouseY);
        renderDisplayTooltip(displayData, pPoseStack, this.leftPos, this.topPos, pMouseX, pMouseY);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/dissolver_gui.png"));
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.dissolver");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    private void renderCurrentRecipe(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        DissolverRecipe currentRecipe = blockEntity.getRecipe();
        ProcessingSlotHandler handler = blockEntity.getInputHandler();

        if (currentRecipe != null && handler.getStackInSlot(0).isEmpty() && blockEntity.isRecipeLocked()) {
            ItemStack currentInput = currentRecipe.getInput().toStacks().get(0);

            int x = leftPos + 84;
            int y = topPos + 12;

            FakeItemRenderer.renderFakeItem(currentInput, x, y, 0.35f);
            if (pMouseX >= x - 1 && pMouseX <= x + 18 && pMouseY > y - 2 && pMouseY <= y + 18) {
                renderItemTooltip(pPoseStack, currentInput, new TranslatableComponent("alchemistry.container.current_recipe"), pMouseX, pMouseY);
            }
        }
    }
}
