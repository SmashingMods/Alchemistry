package com.smashingmods.alchemistry.common.block.combiner;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.container.*;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.chemlib.items.CompoundItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

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
        this.imageHeight = 193;
        this.editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 72, 12, new TextComponent(""));
        this.displayData.add(new ProgressDisplayData(pMenu.getContainerData(), 65, 84, 60, 9, Direction.RIGHT));
        this.displayData.add(new EnergyDisplayData(pMenu.getContainerData(), 156, 23, 16, 54));
    }

    @Override
    protected void containerTick() {
        if (this.editBox.getValue().isEmpty()) {
            this.menu.resetDisplayedRecipes();
            this.editBox.setSuggestion(I18n.get("alchemistry.container.search"));
        } else {
            this.mouseScrolled(0, 0, 0);
            this.menu.searchRecipeList(this.editBox.getValue());
            this.editBox.setSuggestion("");
        }
        super.containerTick();
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if (!this.renderables.contains(editBox)) {
            this.editBox.x = this.leftPos + 57;
            this.editBox.y = this.topPos + 7;
            this.addRenderableWidget(editBox);
        }

        this.renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        this.renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);

        int recipeBoxLeftPos = this.leftPos + 57;
        int recipeBoxTopPos = this.topPos + 21;
        int lastVisibleElementIndex = this.startIndex + this.DISPLAYED_SLOTS;

        this.renderRecipeTooltips(pPoseStack, pMouseX, pMouseY, recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_gui.png"));
        this.blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        this.renderRecipeBox(pPoseStack, pMouseX, pMouseY); // won't work unless it goes here
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.combiner");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, Color.WHITE.getRGB());
    }

    protected void renderRecipeBox(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        int scrollPosition = (int)(39.0F * this.scrollOffset);
        this.blit(pPoseStack, this.leftPos + 132, this.topPos + 23 + scrollPosition, 18 + (isScrollBarActive() ? 0 : 12), this.imageHeight, 12, 15);

        int recipeBoxLeftPos = this.leftPos + 57;
        int recipeBoxTopPos = this.topPos + 21;
        int lastVisibleElementIndex = this.startIndex + this.DISPLAYED_SLOTS;

        this.renderButtons(pPoseStack, pMouseX, pMouseY, recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
        this.renderRecipes(recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
    }

    private void renderButtons(PoseStack pPoseStack, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex) {

        for (int index = this.startIndex; index < pLastVisibleElementIndex && index < this.menu.getDisplayedRecipes().size(); index++) {
            int firstVisibleElementIndex = index - this.startIndex;
            int col = pX + firstVisibleElementIndex % 4 * this.RECIPE_BOX_SIZE;
            int rowt = firstVisibleElementIndex / 4;
            int row = pY + rowt * this.RECIPE_BOX_SIZE + 2;
            int vOffset = this.imageHeight;

            if (index == this.menu.getSelectedRecipeIndex()) {
                vOffset += this.RECIPE_BOX_SIZE;
            } else if (pMouseX >= col && pMouseY >= row && pMouseX < col + this.RECIPE_BOX_SIZE && pMouseY < row + this.RECIPE_BOX_SIZE) {
                vOffset += this.RECIPE_BOX_SIZE * 2;
            }
            this.blit(pPoseStack, col, row, 0, vOffset, this.RECIPE_BOX_SIZE, this.RECIPE_BOX_SIZE);
        }
    }

    private void renderRecipes(int pLeftPos, int pTopPos, int pRecipeIndexOffsetMax) {

        Objects.requireNonNull(this.minecraft);

        List<CombinerRecipe> list = this.menu.getDisplayedRecipes();

        for (int index = this.startIndex; index < pRecipeIndexOffsetMax && index < this.menu.getDisplayedRecipes().size(); index++) {

            ItemStack output = list.get(index).output;

            int firstVisibleIndex = index - this.startIndex;
            int recipeBoxLeftPos = pLeftPos + firstVisibleIndex % 4 * this.RECIPE_BOX_SIZE + 1;
            int l = firstVisibleIndex / 4;
            int recipeBoxTopPos = pTopPos + l * this.RECIPE_BOX_SIZE + 3;
            this.minecraft.getItemRenderer().renderGuiItem(output, recipeBoxLeftPos, recipeBoxTopPos);
        }
    }

    private void renderRecipeTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int pLeftPos, int pTopPos, int pRecipeIndexOffsetMax) {
        Objects.requireNonNull(this.minecraft);

        List<CombinerRecipe> list = this.menu.getDisplayedRecipes();

        for (int index = this.startIndex; index < pRecipeIndexOffsetMax && index < this.menu.getDisplayedRecipes().size(); index++) {

            ItemStack output = list.get(index).output;

            int firstVisibleIndex = index - this.startIndex;
            int recipeBoxLeftPos = pLeftPos + firstVisibleIndex % 4 * this.RECIPE_BOX_SIZE + 1;
            int l = firstVisibleIndex / 4;
            int recipeBoxTopPos = pTopPos + l * this.RECIPE_BOX_SIZE + 3;

            if (pMouseX >= recipeBoxLeftPos - 1 && pMouseX <= recipeBoxLeftPos + this.RECIPE_BOX_SIZE - 2 && pMouseY >= recipeBoxTopPos - 1 && pMouseY <= recipeBoxTopPos + this.RECIPE_BOX_SIZE - 2) {
                List<Component> components = new ArrayList<>();
                Objects.requireNonNull(output.getItem().getRegistryName());
                String namespace = StringUtils.capitalize(output.getItem().getRegistryName().getNamespace());

                components.add(output.getItem().getDescription());
                if (output.getItem() instanceof CompoundItem) {
                    String chemicalName = ((CompoundItem) output.getItem()).getAbbreviation();
                    components.add(new TextComponent(chemicalName));
                }
                components.add(new TextComponent(namespace).withStyle(ChatFormatting.BLUE));

                this.renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {

        if (pKeyCode == InputConstants.KEY_E && this.editBox.isFocused()) {
            return false;
        } else if (pKeyCode == InputConstants.KEY_TAB && !this.editBox.isFocused()) {
            this.editBox.setFocus(true);
            this.editBox.setEditable(true);
            this.editBox.active = true;
        } else if (pKeyCode == InputConstants.KEY_ESCAPE && this.editBox.isFocused()) {
            this.editBox.setFocus(false);
            this.editBox.setEditable(false);
            this.editBox.active = false;
            return false;
        }  else if (pKeyCode == InputConstants.KEY_R && ModList.get().isLoaded("jei")) {
            Alchemistry.LOGGER.info("SHOW RECIPE");
        } else if (pKeyCode == InputConstants.KEY_U && ModList.get().isLoaded("jei")) {
            Alchemistry.LOGGER.info("SHOW USAGE");
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        Objects.requireNonNull(minecraft);
        Objects.requireNonNull(minecraft.player);
        Objects.requireNonNull(minecraft.gameMode);

        int editBoxMinX = leftPos + 57;
        int editBoxMaxX = editBoxMinX + 72;
        int editBoxMinY = topPos + 7;
        int editBoxMaxY = editBoxMinY + 12;

        if (pMouseX >= editBoxMinX && pMouseX < editBoxMaxX && pMouseY >= editBoxMinY && pMouseY < editBoxMaxY) {
            this.editBox.onClick(pMouseX, pMouseY);
        } else {
            this.editBox.active = false;
        }

        this.scrolling = false;

        int recipeBoxLeftPos = leftPos + 57;
        int recipeBoxTopPos = topPos + 23;
        int k = startIndex + DISPLAYED_SLOTS;

        for (int index = this.startIndex; index < k; index++) {
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
            this.startIndex = (int)((double)(this.scrollOffset * (float) this.getOffscreenRows()) + 0.5D) * 4;
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
        return this.menu.getDisplayedRecipes().size() > 12;
    }

    protected int getOffscreenRows() {
        return (this.menu.getDisplayedRecipes().size() + 4 - 1) / 4 - 3;
    }
}
