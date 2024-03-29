package com.smashingmods.alchemistry.common.block.fission;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.container.button.ReactorAutoejectButton;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.container.FakeItemRenderer;
import com.smashingmods.alchemylib.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.client.button.PauseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FissionControllerScreen extends AbstractProcessingScreen<FissionControllerMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();
    private final FissionControllerBlockEntity blockEntity;
    private final PauseButton pauseButton = new PauseButton(this);
    private final ReactorAutoejectButton outputBehaviourButton = new ReactorAutoejectButton(this);

    public FissionControllerScreen(FissionControllerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 74, 35, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 12, 12, 16, 54));

        this.blockEntity = (FissionControllerBlockEntity) pMenu.getBlockEntity();
    }

    @Override
    protected void init() {
        widgets.add(pauseButton);
        widgets.add(outputBehaviourButton);
        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        renderDisplayData(displayData, pGuiGraphics, leftPos, topPos);
        renderCurrentRecipe(pGuiGraphics, pMouseX, pMouseY);
        renderDisplayTooltip(displayData, pGuiGraphics, leftPos, topPos, pMouseX, pMouseY);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(new ResourceLocation(Alchemistry.MODID, "textures/gui/fission_gui.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        MutableComponent title = MutableComponent.create(new TranslatableContents("alchemistry.container.fission_controller", null, TranslatableContents.NO_ARGS));
        pGuiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    private void renderCurrentRecipe(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

        FissionRecipe currentRecipe = blockEntity.getRecipe();
        ProcessingSlotHandler handler = blockEntity.getInputHandler();

        if (currentRecipe != null && handler.getStackInSlot(0).isEmpty() && blockEntity.isRecipeLocked()) {
            ItemStack currentInput = currentRecipe.getInput();

            int x = leftPos + 44;
            int y = topPos + 35;

            FakeItemRenderer.renderFakeItem(pGuiGraphics, currentInput, x, y);
            if (pMouseX >= x - 1 && pMouseX <= x + 18 && pMouseY > y - 2 && pMouseY <= y + 18) {
                renderItemTooltip(pGuiGraphics, currentInput, MutableComponent.create(new TranslatableContents("alchemistry.container.current_recipe", null, TranslatableContents.NO_ARGS)), pMouseX, pMouseY);
            }
        }
    }
}
