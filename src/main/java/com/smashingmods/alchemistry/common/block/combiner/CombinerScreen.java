package com.smashingmods.alchemistry.common.block.combiner;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.AbstractAlchemistryScreen;
import com.smashingmods.alchemistry.api.container.DisplayData;
import com.smashingmods.alchemistry.api.container.EnergyDisplayData;
import com.smashingmods.alchemistry.api.container.ProgressDisplayData;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CombinerScreen extends AbstractAlchemistryScreen<CombinerMenu> {

    protected final List<DisplayData> displayData = new ArrayList<>();
    protected final EditBox editBox;

    private final int DISPLAYED_SLOTS = 12;
    private final int RECIPE_BOX_SIZE = 18;
    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;

    public CombinerScreen(CombinerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 184;
        this.imageHeight = 181;
        this.editBox = new EditBox(Minecraft.getInstance().font, this.leftPos + 57, this.topPos, 70, 12, new TextComponent("Search ..."));
        this.displayData.add(new ProgressDisplayData(pMenu.getContainerData(), 65, 72, 60, 9));
        this.displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 156, 11, 16, 54));
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        this.renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void containerTick() {
        if (this.editBox.getValue().isEmpty()) {
            this.menu.resetDisplayedRecipes();
        } else {
            this.menu.searchRecipeList(this.editBox.getValue());
        }
        super.containerTick();
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/big_combiner_gui.png"));
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        this.renderRecipeBox(pPoseStack, pMouseX, pMouseY);

        this.editBox.x = this.leftPos + 57;
        this.editBox.y = this.topPos;

        this.addRenderableWidget(editBox);
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.combiner");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, Color.WHITE.getRGB());
    }

    protected void renderRecipeBox(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int scrollPosition = (int)(39.0F * this.scrollOffset);
        this.blit(pPoseStack, this.leftPos + 132, this.topPos + 11 + scrollPosition, 18 + (isScrollBarActive() ? 0 : 12), 181, 12, 15);

        int recipeBoxLeftPos = this.leftPos + 57;
        int recipeBoxTopPos = this.topPos + 9;
        int lastVisibleElementIndex = this.startIndex + this.DISPLAYED_SLOTS;

        this.renderButtons(pPoseStack, pMouseX, pMouseY, recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
        this.renderRecipes(recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
    }

    private void renderButtons(PoseStack pPoseStack, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex) {

        for (int index = this.startIndex; index < pLastVisibleElementIndex && index < this.menu.displayedRecipes.size(); index++) {
            int firstVisibleElementIndex = index - this.startIndex;
            int col = pX + firstVisibleElementIndex % 4 * this.RECIPE_BOX_SIZE;
            int row = firstVisibleElementIndex / 4;
            int uOffset = pY + row * this.RECIPE_BOX_SIZE + 2;
            int vOffset = this.imageHeight;

            if (index == this.menu.getSelectedRecipeIndex()) {
                vOffset += this.RECIPE_BOX_SIZE;
            } else if (pMouseX >= col && pMouseY >= uOffset && pMouseX < col + this.RECIPE_BOX_SIZE && pMouseY < uOffset + this.RECIPE_BOX_SIZE) {
                vOffset += this.RECIPE_BOX_SIZE * 2;
            }

            this.blit(pPoseStack, col, uOffset, 0, vOffset, this.RECIPE_BOX_SIZE, this.RECIPE_BOX_SIZE);
        }
    }

    private void renderRecipes(int pLeftPos, int pTopPos, int pRecipeIndexOffsetMax) {

        Objects.requireNonNull(this.minecraft);

        List<CombinerRecipe> list = this.menu.displayedRecipes;

        for(int index = this.startIndex; index < pRecipeIndexOffsetMax && index < this.menu.displayedRecipes.size(); index++) {

            ItemStack output = list.get(index).output;

            int firstVisibleIndex = index - this.startIndex;
            int recipeBoxLeftPos = pLeftPos + firstVisibleIndex % 4 * this.RECIPE_BOX_SIZE + 1;
            int l = firstVisibleIndex / 4;
            int recipeBoxTopPos = pTopPos + l * this.RECIPE_BOX_SIZE + 3;
            this.minecraft.getItemRenderer().renderAndDecorateItem(output, recipeBoxLeftPos, recipeBoxTopPos);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        Objects.requireNonNull(minecraft);
        Objects.requireNonNull(minecraft.player);
        Objects.requireNonNull(minecraft.gameMode);

        this.scrolling = false;

        int recipeBoxLeftPos = leftPos + 57;
        int recipeBoxTopPos = topPos + 11;
        int k = startIndex + DISPLAYED_SLOTS;

        for(int index = this.startIndex; index < k; index++) {
            int currentIndex = index - this.startIndex;
            double boxX = pMouseX - (double)(recipeBoxLeftPos + currentIndex % 4 * this.RECIPE_BOX_SIZE);
            double boxY = pMouseY - (double)(recipeBoxTopPos + currentIndex / 4 * this.RECIPE_BOX_SIZE);

            if (boxX >= 0 && boxY >= 0 && boxX < this.RECIPE_BOX_SIZE && boxY < this.RECIPE_BOX_SIZE && this.menu.clickMenuButton(this.minecraft.player, index)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, index);
                return true;
            }

            int scrollMinX = recipeBoxLeftPos + 75;
            int scrollMaxX = recipeBoxLeftPos + 87;
            int scrollMaxY = recipeBoxTopPos + 54;

            if (pMouseX >= scrollMinX
                    && pMouseX < scrollMaxX
                    && pMouseY >= recipeBoxTopPos
                    && pMouseY < scrollMaxY) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling && this.isScrollBarActive()) {
            int i = this.topPos + 14;
            int j = i + 54;
            this.scrollOffset = ((float)pMouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollOffset = Mth.clamp(this.scrollOffset, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffset * (float)this.getOffscreenRows()) + 0.5D) * 4;
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isScrollBarActive()) {
            int offscreenRows = getOffscreenRows();
            float f = (float) pDelta / (float) offscreenRows;
            this.scrollOffset = Mth.clamp(this.scrollOffset - f, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffset * (float) offscreenRows) + 0.5D) * 4;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return this.menu.displayedRecipes.size() > 12;
    }

    protected int getOffscreenRows() {
        return (this.menu.displayedRecipes.size() + 4 - 1) / 4 - 3;
    }
}
