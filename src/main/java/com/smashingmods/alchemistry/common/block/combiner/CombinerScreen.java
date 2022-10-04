package com.smashingmods.alchemistry.common.block.combiner;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemistry.api.blockentity.container.Direction2D;
import com.smashingmods.alchemistry.api.blockentity.container.FakeItemRenderer;
import com.smashingmods.alchemistry.api.blockentity.container.RecipeSelectorScreen;
import com.smashingmods.alchemistry.api.blockentity.container.button.RecipeSelectorButton;
import com.smashingmods.alchemistry.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemistry.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemistry.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CombinerScreen extends AbstractProcessingScreen<CombinerMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();
    private final CombinerBlockEntity blockEntity;
    protected final EditBox editBox;

    private final RecipeSelectorButton recipeSelector;

    private final int DISPLAYED_SLOTS = 12;
    private final int RECIPE_BOX_SIZE = 18;
    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;

    public CombinerScreen(CombinerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, Alchemistry.MODID);
        this.imageWidth = 184;
        this.imageHeight = 193;
        this.displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 65, 84, 60, 9, Direction2D.RIGHT));
        this.displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 156, 23, 16, 54));
        this.blockEntity = (CombinerBlockEntity) pMenu.getBlockEntity();

        editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 72, 12, new TextComponent(""));
        if (!blockEntity.getEditBoxText().isEmpty()) {
            editBox.setValue(blockEntity.getEditBoxText());
            menu.searchRecipeList(blockEntity.getEditBoxText());
        }

        RecipeSelectorScreen<CombinerBlockEntity, CombinerRecipe> recipeSelectorScreen = new RecipeSelectorScreen<>((CombinerBlockEntity) getMenu().getBlockEntity(), RecipeRegistry.getCombinerRecipes(pMenu.getLevel()));
        recipeSelector = new RecipeSelectorButton(0, 0, this, recipeSelectorScreen, new TranslatableComponent("alchemistry.container.select_recipe"), 45, 60);
    }

    @Override
    protected void containerTick() {
        if (editBox.getValue().isEmpty()) {
            blockEntity.setEditBoxText("");
            menu.resetDisplayedRecipes();
            editBox.setSuggestion(I18n.get("alchemistry.container.search"));
        } else {
            mouseScrolled(0, 0, 0);
            blockEntity.setEditBoxText(editBox.getValue());
            menu.searchRecipeList(editBox.getValue());
            if (menu.getDisplayedRecipes().size() <= 12) {
                startIndex = 0;
                scrollOffset = 0;
            }
            editBox.setSuggestion("");
        }
        super.containerTick();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderRecipeBox(pPoseStack, pMouseX, pMouseY);
        renderDisplayData(displayData, pPoseStack, leftPos, topPos);
        renderCurrentRecipe(pPoseStack, pMouseX, pMouseY);

        renderTooltip(pPoseStack, pMouseX, pMouseY);
        renderRecipeTooltips(pPoseStack, pMouseX, pMouseY);
        renderDisplayTooltip(displayData, pPoseStack, leftPos, topPos, pMouseX, pMouseY);

        renderWidget(editBox, leftPos + 57, topPos + 7);
        renderWidget(recipeSelector, leftPos - 24, topPos + 72);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_gui.png"));
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        Component title = new TranslatableComponent("alchemistry.container.combiner");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    protected void renderRecipeBox(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_gui.png"));

        int scrollPosition = (int)(39.0F * scrollOffset);
        blit(pPoseStack, leftPos + 132, topPos + 23 + scrollPosition, 18 + (isScrollBarActive() ? 0 : 12), imageHeight, 12, 15);

        int recipeBoxLeftPos = leftPos + 57;
        int recipeBoxTopPos = topPos + 21;
        int lastVisibleElementIndex = startIndex + DISPLAYED_SLOTS;

        renderRecipeButtons(pPoseStack, pMouseX, pMouseY, recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
        renderRecipes(recipeBoxLeftPos, recipeBoxTopPos, lastVisibleElementIndex);
    }

    private void renderRecipeButtons(PoseStack pPoseStack, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex) {

        for (int index = startIndex; index < pLastVisibleElementIndex && index < menu.getDisplayedRecipes().size(); index++) {
            int firstVisibleElementIndex = index - startIndex;
            int col = pX + firstVisibleElementIndex % 4 * RECIPE_BOX_SIZE;
            int rowt = firstVisibleElementIndex / 4;
            int row = pY + rowt * RECIPE_BOX_SIZE + 2;
            int vOffset = imageHeight;

            int currentRecipeIndex = menu.getDisplayedRecipes().indexOf(((CombinerBlockEntity) menu.getBlockEntity()).getRecipe());

            if (index == currentRecipeIndex) {
                vOffset += RECIPE_BOX_SIZE;
            } else if (pMouseX >= col && pMouseY >= row && pMouseX < col + RECIPE_BOX_SIZE && pMouseY < row + RECIPE_BOX_SIZE) {
                vOffset += RECIPE_BOX_SIZE * 2;
            }
            blit(pPoseStack, col, row, 0, vOffset, RECIPE_BOX_SIZE, RECIPE_BOX_SIZE);
        }
    }

    private void renderRecipes(int pLeftPos, int pTopPos, int pRecipeIndexOffsetMax) {
        LinkedList<CombinerRecipe> list = menu.getDisplayedRecipes();

        for (int index = startIndex; index < pRecipeIndexOffsetMax && index < menu.getDisplayedRecipes().size(); index++) {

            ItemStack output = list.get(index).getOutput();

            int firstVisibleIndex = index - startIndex;
            int recipeBoxLeftPos = pLeftPos + firstVisibleIndex % 4 * RECIPE_BOX_SIZE + 1;
            int l = firstVisibleIndex / 4;
            int recipeBoxTopPos = pTopPos + l * RECIPE_BOX_SIZE + 3;
            Minecraft.getInstance().getItemRenderer().renderGuiItem(output, recipeBoxLeftPos, recipeBoxTopPos);
        }
    }

    private void renderCurrentRecipe(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        CombinerRecipe currentRecipe = (CombinerRecipe) menu.getBlockEntity().getRecipe();
        ProcessingSlotHandler handler = blockEntity.getInputHandler();

        // Intellij thinks this is never null. Remove this and watch it crash.
        //noinspection ConstantConditions
        if (currentRecipe != null) {
            ItemStack currentOutput = currentRecipe.getOutput();
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(currentOutput, leftPos + 21, topPos + 15);

            if (pMouseX >= leftPos + 20 && pMouseX < leftPos + 36 && pMouseY > topPos + 14 && pMouseY < topPos + 30) {
                renderItemTooltip(pPoseStack, currentOutput, new TranslatableComponent("alchemistry.container.current_recipe"), pMouseX, pMouseY);
            }

            int xOrigin = leftPos + 12;
            int yOrigin = topPos + 63;

            for (int row = 0; row < 2; row++) {
                for (int column = 0; column < 2; column++) {
                    int index = column + row * 2;
                    int x = xOrigin + column * 18;
                    int y = yOrigin + row * 18;

                    if (index < currentRecipe.getInput().size()) {

                        ItemStack itemStack = currentRecipe.getInput().get(index).getIngredient().getItems()[(int) (Math.random() * currentRecipe.getInput().get(index).getIngredient().getItems().length)];

                        if (handler.getStackInSlot(index).isEmpty()) {
                            FakeItemRenderer.renderFakeItem(itemStack, x, y, 0.35F);

                            if (pMouseX >= x - 1 && pMouseX < x + 17 && pMouseY > y - 2 && pMouseY < y + 17) {
                                renderItemTooltip(pPoseStack, itemStack, new TranslatableComponent("alchemistry.container.required_input"), pMouseX, pMouseY);
                            }
                        }
                    }
                }
            }
        }
    }

    private void renderRecipeTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY) {

        int originX = leftPos + 57;
        int originY = topPos + 23;
        List<CombinerRecipe> displayedRecipes = menu.getDisplayedRecipes();

        for (int index = startIndex; index < startIndex + DISPLAYED_SLOTS && index < displayedRecipes.size(); index++) {

            ItemStack output = displayedRecipes.get(index).getOutput();

            int firstVisibleIndex = index - startIndex;
            int recipeBoxLeftPos = originX + firstVisibleIndex % 4 * RECIPE_BOX_SIZE;
            int col = firstVisibleIndex / 4;
            int recipeBoxTopPos = originY + col * RECIPE_BOX_SIZE;

            if (pMouseX >= recipeBoxLeftPos && pMouseX <= recipeBoxLeftPos + 17 && pMouseY >= recipeBoxTopPos && pMouseY <= recipeBoxTopPos + 17) {
                renderItemTooltip(pPoseStack, output, new TranslatableComponent("alchemistry.container.select_recipe"), pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == InputConstants.KEY_E && editBox.isFocused()) {
            return false;
        } else if (pKeyCode == InputConstants.KEY_TAB && !editBox.isFocused()) {
            editBox.setFocus(true);
            editBox.setEditable(true);
            editBox.active = true;
        } else if (pKeyCode == InputConstants.KEY_ESCAPE && editBox.isFocused()) {
            editBox.setFocus(false);
            editBox.setEditable(false);
            editBox.active = false;
            return false;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        Objects.requireNonNull(Minecraft.getInstance().player);
        Objects.requireNonNull(Minecraft.getInstance().gameMode);

        int editBoxMinX = leftPos + 57;
        int editBoxMaxX = editBoxMinX + 72;
        int editBoxMinY = topPos + 7;
        int editBoxMaxY = editBoxMinY + 12;

        if (pMouseX >= editBoxMinX && pMouseX < editBoxMaxX && pMouseY >= editBoxMinY && pMouseY < editBoxMaxY) {
            editBox.onClick(pMouseX, pMouseY);
        } else {
            editBox.active = false;
        }
        scrolling = false;

        int recipeBoxLeftPos = leftPos + 57;
        int recipeBoxTopPos = topPos + 23;
        int k = startIndex + DISPLAYED_SLOTS;

        for (int index = startIndex; index < k; index++) {
            int currentIndex = index - startIndex;
            double boxX = pMouseX - (double)(recipeBoxLeftPos + currentIndex % 4 * RECIPE_BOX_SIZE);
            double boxY = pMouseY - (double)(recipeBoxTopPos + currentIndex / 4 * RECIPE_BOX_SIZE);

            if (boxX >= 0 && boxY >= 0 && boxX < RECIPE_BOX_SIZE && boxY < RECIPE_BOX_SIZE && menu.clickMenuButton(Minecraft.getInstance().player, index)) {

                int finalIndex = index;
                RecipeRegistry.getCombinerRecipe(recipe -> ItemStack.isSameItemSameTags(recipe.getOutput(), getMenu().getDisplayedRecipes().get(finalIndex).getOutput()), getMenu().getLevel()).ifPresent(blockEntity::setRecipe);

                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                Minecraft.getInstance().gameMode.handleInventoryButtonClick((menu).containerId, index);
                return true;
            }

            int scrollMinX = recipeBoxLeftPos + 75;
            int scrollMaxX = recipeBoxLeftPos + 87;
            int scrollMaxY = recipeBoxTopPos + 54;

            if (pMouseX >= scrollMinX
                    && pMouseX < scrollMaxX
                    && pMouseY >= recipeBoxTopPos
                    && pMouseY < scrollMaxY) {
                scrolling = true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling && isScrollBarActive()) {
            int i = topPos + 14;
            int j = i + 54;
            scrollOffset = ((float)pMouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
            startIndex = (int)((double)(scrollOffset * (float) getOffscreenRows()) + 0.5D) * 4;
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (isScrollBarActive()) {
            int offscreenRows = getOffscreenRows();
            float f = (float) pDelta / (float) offscreenRows;
            scrollOffset = Mth.clamp(scrollOffset - f, 0.0F, 1.0F);
            startIndex = (int)((double)(scrollOffset * (float) offscreenRows) + 0.5D) * 4;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return menu.getDisplayedRecipes().size() > 12;
    }

    private int getOffscreenRows() {
        return (menu.getDisplayedRecipes().size() + 4 - 1) / 4 - 3;
    }
}
