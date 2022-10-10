package com.smashingmods.alchemistry.api.blockentity.container;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemistry.api.recipe.ProcessingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecipeSelectorScreen<P extends AbstractProcessingScreen<?>, B extends AbstractProcessingBlockEntity, R extends ProcessingRecipe> extends Screen implements RecipeScreen<B, R> {

    private final int imageWidth = 184;
    private final int imageHeight = 92;

    private int leftPos;
    private int topPos;

    private int recipeBoxLeftPos;
    private int recipeBoxTopPos;

    private final P parentScreen;
    private final B blockEntity;
    private final LinkedList<R> recipes;
    private final LinkedList<ProcessingRecipe> displayedRecipes = new LinkedList<>();

    private final EditBox searchBox;

    private static final int DISPLAYED_SLOTS = 12;
    private static final int RECIPE_BOX_SIZE = 18;

    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;

    public RecipeSelectorScreen(P pParentScreen, B pBlockEntity, LinkedList<R> pRecipes) {
        super(TextComponent.EMPTY);
        this.parentScreen = pParentScreen;
        this.blockEntity = pBlockEntity;
        this.recipes = pRecipes;
        this.searchBox = new EditBox(Minecraft.getInstance().font, 0, 0, 72, 12, new TextComponent(""));
        if (!blockEntity.getSearchText().isEmpty()) {
            searchBox.setValue(blockEntity.getSearchText());
            searchRecipeList(blockEntity.getSearchText());
        }
    }

    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.recipeBoxLeftPos = leftPos + 56;
        this.recipeBoxTopPos = topPos + 24;
        super.init();
    }

    public void setTopPos(int topPos) {
        this.topPos = topPos;
    }

    @Override
    public void tick() {
        if (searchBox.getValue().isEmpty()) {
            blockEntity.setSearchText("");
            resetDisplayedRecipes();
            searchBox.setSuggestion(I18n.get("alchemistry.container.search"));
        } else {
            mouseScrolled(0, 0, 0);
            blockEntity.setSearchText(searchBox.getValue());
            searchRecipeList(searchBox.getValue());
            if (displayedRecipes.size() <= 12) {
                startIndex = 0;
                scrollOffset = 0;
            }
            searchBox.setSuggestion("");
        }
        super.tick();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBg(pPoseStack);
        renderLabels(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderRecipeBox(pPoseStack, pMouseX, pMouseY);
        renderWidget(searchBox, leftPos + 56, topPos + 12);
    }

    private void renderBg(PoseStack pPoseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/recipe_select_gui.png"));
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private void renderLabels(PoseStack pPoseStack) {
        Component title = new TranslatableComponent("alchemistry.container.search");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    private void renderRecipeBox(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/recipe_select_gui.png"));

        renderScrollbar(pPoseStack);
        renderRecipeButtons(pPoseStack, pMouseX, pMouseY);
        renderRecipeButtonItems(pPoseStack, pMouseX, pMouseY);
        renderCurrentRecipe(pPoseStack, pMouseX, pMouseY);
    }

    private void renderScrollbar(PoseStack pPoseStack) {
        int scrollPosition = (int) (39.0f * scrollOffset);
        blit(pPoseStack, leftPos + 132, topPos + 26 + scrollPosition, 18 + (isScrollBarActive() ? 0 : 12), imageHeight, 12, 15);
    }

    private void renderRecipeButtons(PoseStack pPoseStack, int pMouseX, int pMouseY) {

        int lastDisplayedIndex = startIndex + DISPLAYED_SLOTS;

        for (int index = startIndex; index < lastDisplayedIndex && index < getDisplayedRecipes().size(); index++) {
            int firstDisplayedIndex = index - startIndex;
            int col = recipeBoxLeftPos + firstDisplayedIndex % 4 * RECIPE_BOX_SIZE;
            int row = recipeBoxTopPos + (firstDisplayedIndex / 4) * RECIPE_BOX_SIZE + 2;
            int vOffset = imageHeight;
            int currentRecipeIndex = getDisplayedRecipes().indexOf(blockEntity.getRecipe());

            if (index == currentRecipeIndex) {
                vOffset += RECIPE_BOX_SIZE;
            } else if (pMouseX >= col && pMouseX < col + RECIPE_BOX_SIZE && pMouseY >= row && pMouseY < row + RECIPE_BOX_SIZE) {
                vOffset += RECIPE_BOX_SIZE * 2;
            }
            blit(pPoseStack, col, row, 0, vOffset, RECIPE_BOX_SIZE, RECIPE_BOX_SIZE);
        }
    }

    private void renderRecipeButtonItems(PoseStack pPoseStack, int pMouseX, int pMouseY) {

        int lastDisplayedIndex = startIndex + DISPLAYED_SLOTS;


        for (int index = startIndex; index < lastDisplayedIndex && index < getDisplayedRecipes().size(); index++) {
            if (index >= 0 && index < getDisplayedRecipes().size()) {
                ItemStack target = RecipeDisplayUtil.getTarget(getDisplayedRecipes().get(index));

                int firstDisplayedIndex = index - startIndex;
                int row = recipeBoxLeftPos + firstDisplayedIndex % 4 * RECIPE_BOX_SIZE + 1;
                int col = recipeBoxTopPos + (firstDisplayedIndex / 4) * RECIPE_BOX_SIZE + 3;

                renderFloatingItem(target, row, col);

                if (pMouseX >= row && pMouseX <= row + 17 && pMouseY >= col && pMouseY <= col + 17) {
                    List<Component> components = RecipeDisplayUtil.getItemTooltipComponent(target, new TranslatableComponent("alchemistry.container.select_recipe"));
                    renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
                }
            }
        }
    }

    private void renderFloatingItem(ItemStack pItemStack, int pX, int pY) {
        RenderSystem.applyModelViewMatrix();
        setBlitOffset(2000);
        itemRenderer.blitOffset = 2000.0f;

        itemRenderer.renderAndDecorateItem(pItemStack, pX, pY);
        itemRenderer.renderGuiItemDecorations(font, pItemStack, pX, pY, null);

        setBlitOffset(0);
        itemRenderer.blitOffset = 0.0f;
    }

    private void renderCurrentRecipe(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        ProcessingRecipe recipe = blockEntity.getRecipe();
        if (recipe != null && blockEntity instanceof AbstractInventoryBlockEntity inventoryBlockEntity) {
            ItemStack target = RecipeDisplayUtil.getTarget(recipe);
            renderFloatingItem(target, leftPos + 21, topPos + 14);

            int xOrigin = leftPos + 12;
            int yOrigin = topPos + 42;
            int inputSize = RecipeDisplayUtil.getInputSize(recipe);
            int totalRows = inputSize / 2;
            int totalCols = (inputSize / 2) + (inputSize % 2);

            for (int row = 0; row < totalRows; row++) {
                for (int col = 0; col < totalCols; col++) {
                    int index = col + row * 2;
                    int x = xOrigin + col * 18;
                    int y = yOrigin + row * 18;

                    if (index < inputSize && inventoryBlockEntity.getInputHandler().getStackInSlot(index).isEmpty()) {
                        ItemStack itemStack = RecipeDisplayUtil.getRecipeInputByIndex(recipe, index);
                        drawSlot(pPoseStack, x, y);
                        renderFloatingItem(itemStack, x + 1, y + 1);
                    }
                }
            }

            if (pMouseX >= leftPos + 17 && pMouseX < leftPos + 16 && pMouseY >= topPos + 10 && pMouseY <= topPos + 16) {
                List<Component> components = RecipeDisplayUtil.getItemTooltipComponent(target, new TranslatableComponent("alchemistry.container.current_recipe"));
                renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
            }

            for (int row = 0; row < totalRows; row++) {
                for (int col = 0; col < totalCols; col++) {
                    int index = col + row * 2;
                    int x = xOrigin + col * 18;
                    int y = yOrigin + row * 18;

                    if (index < inputSize && inventoryBlockEntity.getInputHandler().getStackInSlot(index).isEmpty()) {
                        ItemStack itemStack = RecipeDisplayUtil.getRecipeInputByIndex(recipe, index);

                        if (pMouseX >= x - 1 && pMouseX < x + 17 && pMouseY >= y - 1 && pMouseY < y + 17 && !itemStack.isEmpty()) {
                            List<Component> components = RecipeDisplayUtil.getItemTooltipComponent(itemStack, new TranslatableComponent("alchemistry.container.required_input"));
                            renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
                        }
                    }
                }
            }
        }
    }

    private void drawSlot(PoseStack pPoseStack, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/recipe_select_gui.png"));
        blit(pPoseStack, pX, pY, 0, 146, 18, 18);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == InputConstants.KEY_E && searchBox.isFocused()) {
            return false;
        } else if (pKeyCode == InputConstants.KEY_TAB && !searchBox.isFocused()) {
            searchBox.setFocus(true);
            searchBox.setEditable(true);
            searchBox.active = true;
        } else if (pKeyCode == InputConstants.KEY_ESCAPE && searchBox.isFocused()) {
            searchBox.setFocus(false);
            searchBox.setEditable(false);
            searchBox.active = false;
            return false;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        Objects.requireNonNull(Minecraft.getInstance().player);
        Objects.requireNonNull(Minecraft.getInstance().gameMode);

        int searchBoxMinX = leftPos + 56;
        int searchBoxMaxX = searchBoxMinX + 72;
        int searchBoxMinY = topPos + 12;
        int searchBoxMaxY = searchBoxMinY + 12;

        if (pMouseX >= searchBoxMinX && pMouseX < searchBoxMaxX && pMouseY >= searchBoxMinY && pMouseY < searchBoxMaxY) {
            searchBox.onClick(pMouseX, pMouseY);
        } else {
            searchBox.active = false;
        }
        scrolling = false;

        int lastDisplayedIndex = startIndex + DISPLAYED_SLOTS;

        for (int index = startIndex; index < lastDisplayedIndex; index++) {
            int currentIndex = index - startIndex;
            double boxX = pMouseX - (double)(recipeBoxLeftPos + currentIndex % 4 * RECIPE_BOX_SIZE);
            double boxY = pMouseY - (double)(recipeBoxTopPos + currentIndex / 4 * RECIPE_BOX_SIZE);

            if (boxX >= 0 && boxX < RECIPE_BOX_SIZE && boxY >= 0 && boxY < RECIPE_BOX_SIZE && !blockEntity.isRecipeLocked() && isValidRecipeIndex(index)) {
                RecipeDisplayUtil.setRecipe(blockEntity, getDisplayedRecipes().get(index));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                return true;
            }

            int scrollMinX = leftPos + 132;
            int scrollMinY = topPos + 26;
            int scrollMaxX = scrollMinX + 12;
            int scrollMaxY = scrollMinY + 52;

            if (pMouseX >= scrollMinX && pMouseX < scrollMaxX && pMouseY >= scrollMinY && pMouseY < scrollMaxY) {
                scrolling = true;
            }
        }

        int parentXStart = parentScreen.getLeftPos();
        int parentXEnd = parentXStart - parentScreen.getImageWidth();
        int parentYStart = parentScreen.getTopPos();
        int parentYEnd = parentYStart - parentScreen.getImageHeight();

//        if (pMouseX < parentXStart && pMouseX > parentXEnd && pMouseY < parentYStart && pMouseY > parentYEnd) {
            return parentScreen.mouseClicked(pMouseX, pMouseY, pButton);
//        }
//        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling && isScrollBarActive()) {
            int i = topPos + 14;
            int j = i + 54;
            scrollOffset = ((float) pMouseY - (float) i - 7.5f) / ((float) (j - i) - 15.0f);
            scrollOffset = Mth.clamp(scrollOffset, 0.0f, 1.0f);
            startIndex = (int) ((double) (scrollOffset * (float) getOffscreenRows()) + 0.5d) * 4;
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (pMouseX >= leftPos && pMouseX < leftPos + imageWidth && pMouseY >= topPos && pMouseY < topPos + imageHeight && isScrollBarActive()) {
            int offscreenRows = getOffscreenRows();
            float scrollToBottomOffset = (float) pDelta / (float) offscreenRows;
            scrollOffset = Mth.clamp(scrollOffset - scrollToBottomOffset, 0.0f, 1.0f);
            startIndex = (int) ((double) (scrollToBottomOffset * (float) offscreenRows) + 0.5d) * 4;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return displayedRecipes.size() > 12;
    }

    private int getOffscreenRows() {
        return (displayedRecipes.size() + 4 - 1) / 4 - 3;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void setupRecipeList() {
        if (displayedRecipes.isEmpty()) {
            resetDisplayedRecipes();
        }
    }

    public void resetDisplayedRecipes() {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(recipes);
    }

    @Override
    public LinkedList<ProcessingRecipe> getDisplayedRecipes() {
        return displayedRecipes;
    }

    @Override
    public B getBlockEntity() {
        return blockEntity;
    }

    public <W extends GuiEventListener & Widget & NarratableEntry> void renderWidget(W pWidget, int pX, int pY) {
        if (!renderables.contains(pWidget)) {
            if (pWidget instanceof AbstractWidget widget) {
                widget.x = pX;
                widget.y = pY;
            }
            addRenderableWidget(pWidget);
        }
    }
}
