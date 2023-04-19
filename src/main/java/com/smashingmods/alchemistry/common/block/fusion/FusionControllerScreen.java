package com.smashingmods.alchemistry.common.block.fusion;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.container.button.AutoBalanceButton;
import com.smashingmods.alchemistry.client.container.button.ReactorAutoejectButton;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.container.FakeItemRenderer;
import com.smashingmods.alchemylib.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.client.button.LockButton;
import com.smashingmods.alchemylib.client.button.PauseButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FusionControllerScreen extends AbstractProcessingScreen<FusionControllerMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();
    private final FusionControllerBlockEntity blockEntity;
    private final LockButton lockButton = new LockButton(this);
    private final PauseButton pauseButton = new PauseButton(this);
    private final AutoBalanceButton autoBalanceButton = new AutoBalanceButton(this);
    private final ReactorAutoejectButton outputBehaviourButton = new ReactorAutoejectButton(this);

    public FusionControllerScreen(FusionControllerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 78, 35, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 12, 12, 16, 54));
        blockEntity = (FusionControllerBlockEntity) pMenu.getBlockEntity();
    }

    @Override
    protected void init() {
        widgets.add(lockButton);
        widgets.add(pauseButton);
        widgets.add(autoBalanceButton);
        widgets.add(outputBehaviourButton);
        super.init();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        renderCurrentRecipe(pPoseStack, pMouseX, pMouseY);
        renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/fusion_gui.png"));
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.fusion_controller");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    private void renderCurrentRecipe(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        FusionRecipe currentRecipe = blockEntity.getRecipe();
        ProcessingSlotHandler handler = blockEntity.getInputHandler();

        if (currentRecipe != null && blockEntity.isRecipeLocked()) {

            int x = leftPos + 48;
            int y = topPos + 18;

            List<ItemStack> inputs = List.of(currentRecipe.getInput1(), currentRecipe.getInput2());

            for (int i = 0; i < inputs.size(); i ++) {
                y = y + (i * 26);
                if (handler.getStackInSlot(i).isEmpty()) {
                    FakeItemRenderer.renderFakeItem(inputs.get(i), x, y, 0.35f);
                    if (pMouseX >= x - 1 && pMouseX <= x + 18 && pMouseY > y - 2 && pMouseY <= y + 18) {
                        renderItemTooltip(pPoseStack, inputs.get(i), new TranslatableComponent("alchemistry.container.current_recipe"), pMouseX, pMouseY);
                    }
                }
            }
        }
    }
}
