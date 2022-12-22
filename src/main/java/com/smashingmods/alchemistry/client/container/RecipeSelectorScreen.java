package com.smashingmods.alchemistry.client.container;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.network.SetRecipePacket;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemylib.api.recipe.ProcessingRecipe;
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
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeSelectorScreen<P extends AbstractProcessingScreen<?>, B extends AbstractSearchableBlockEntity, R extends AbstractProcessingRecipe> extends Screen {

    private final int imageWidth = 184;
    private final int imageHeight = 162;

    private int leftPos;
    private int topPos;

    private int recipeBoxLeftPos;
    private int recipeBoxTopPos;

    private final P parentScreen;
    private final B blockEntity;
    private final LinkedList<R> recipes;
    private final LinkedList<AbstractProcessingRecipe> displayedRecipes = new LinkedList<>();

    private final EditBox searchBox;

    private static final int MAX_DISPLAYED_RECIPES = 30;
    private static final int COLUMNS = 5;
    private static final int RECIPE_BOX_SIZE = 18;

    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;

    public RecipeSelectorScreen(P pParentScreen, B pBlockEntity, LinkedList<R> pRecipes) {
        super(TextComponent.EMPTY);
        this.parentScreen = pParentScreen;
        this.blockEntity = pBlockEntity;
        this.recipes = pRecipes;
        this.searchBox = new EditBox(Minecraft.getInstance().font, 0, 0, 92, 12, new TextComponent(""));
        if (!blockEntity.getSearchText().isEmpty()) {
            searchBox.setValue(blockEntity.getSearchText());
            searchRecipeList(blockEntity.getSearchText());
        }
    }

    // Lifecycle methods

    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.recipeBoxLeftPos = leftPos + 58;
        this.recipeBoxTopPos = topPos + 26;
        super.init();
    }

    @Override
    public void tick() {
        if (searchBox.getValue().isEmpty()) {
            blockEntity.setSearchText("");
            resetDisplayedRecipes();
            searchBox.setSuggestion(I18n.get("alchemistry.container.search"));
        } else {
            if (displayedRecipes.size() < MAX_DISPLAYED_RECIPES) {
                mouseScrolled(0, 0, 0);
                scrollOffset = 0.0f;
            }
            blockEntity.setSearchText(searchBox.getValue());
            searchRecipeList(searchBox.getValue());
            if (displayedRecipes.size() <= MAX_DISPLAYED_RECIPES) {
                startIndex = 0;
                scrollOffset = 0;
            }
            searchBox.setSuggestion("");
        }
        super.tick();
    }

    @Override
    public void onClose() {
        blockEntity.setRecipeSelectorOpen(false);
        super.onClose();
    }

    // Render methods

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBg(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        renderRecipeBox(pPoseStack, pMouseX, pMouseY);
        renderWidget(searchBox, leftPos + 58, topPos + 11);
        renderParentTooltips(pPoseStack, pMouseX, pMouseY);
    }

    private void renderBg(PoseStack pPoseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/recipe_select_gui.png"));
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private void renderRecipeBox(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/recipe_select_gui.png"));

        int lastDisplayedIndex = startIndex + MAX_DISPLAYED_RECIPES;

        renderScrollbar(pPoseStack);
        renderRecipeButtons(pPoseStack, pMouseX, pMouseY, lastDisplayedIndex);
        renderRecipeButtonItems(pPoseStack, pMouseX, pMouseY, lastDisplayedIndex);
        renderCurrentRecipe(pPoseStack, pMouseX, pMouseY);
    }

    private void renderScrollbar(PoseStack pPoseStack) {
        int scrollPosition = (int) (93.0f * scrollOffset);
        blit(pPoseStack, leftPos + 154, topPos + 28 + scrollPosition, 18 + (isScrollBarActive() ? 0 : 12), imageHeight, 12, 15);
    }

    private void renderRecipeButtons(PoseStack pPoseStack, int pMouseX, int pMouseY, int pLastDisplayedIndex) {
        for (int index = startIndex; index < pLastDisplayedIndex && index < getDisplayedRecipes().size(); index++) {
            int firstDisplayedIndex = index - startIndex;
            int xStart = recipeBoxLeftPos + firstDisplayedIndex % COLUMNS * RECIPE_BOX_SIZE;
            int yStart = recipeBoxTopPos + (firstDisplayedIndex / COLUMNS) * RECIPE_BOX_SIZE + 2;
            int vOffset = imageHeight;
            int currentRecipeIndex = getDisplayedRecipes().indexOf(blockEntity.getRecipe());

            if (index == currentRecipeIndex) {
                vOffset += RECIPE_BOX_SIZE;
            } else if (pMouseX >= xStart && pMouseX < xStart + RECIPE_BOX_SIZE && pMouseY >= yStart && pMouseY < yStart + RECIPE_BOX_SIZE) {
                vOffset += RECIPE_BOX_SIZE * 2;
            }
            blit(pPoseStack, xStart, yStart, 0, vOffset, RECIPE_BOX_SIZE, RECIPE_BOX_SIZE);
        }
    }

    private void renderRecipeButtonItems(PoseStack pPoseStack, int pMouseX, int pMouseY, int pLastDisplayedIndex) {
        LinkedList<AbstractProcessingRecipe> displayedRecipes = getDisplayedRecipes();
        for (int index = startIndex; index >= 0 && index < pLastDisplayedIndex && index < displayedRecipes.size(); index++) {

            int firstDisplayedIndex = index - startIndex;
            ItemStack target = RecipeDisplayUtil.getTarget(getDisplayedRecipes().get(index));
            int xStart = recipeBoxLeftPos + firstDisplayedIndex % COLUMNS * RECIPE_BOX_SIZE + 1;
            int yStart = recipeBoxTopPos + (firstDisplayedIndex / COLUMNS) * RECIPE_BOX_SIZE + 3;

            renderFloatingItem(target, xStart, yStart);

            if (pMouseX >= xStart - 1 && pMouseX <= xStart + 16 && pMouseY >= yStart - 1 && pMouseY <= yStart + 16) {
                List<Component> components = RecipeDisplayUtil.getItemTooltipComponent(target, new TranslatableComponent("alchemistry.container.select_recipe"));
                renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
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
        if (recipe != null) {

            // handle rendering the input slots and required input items
            recipeLooper((pIndex, pInputSize, pX, pY) -> {
                if (pIndex < pInputSize && blockEntity.getInputHandler().getStackInSlot(pIndex).isEmpty()) {
                    ItemStack itemStack = RecipeDisplayUtil.getRecipeInputByIndex(recipe, pIndex);
                    renderSlot(pPoseStack, pX, pY);
                    renderFloatingItem(itemStack, pX + 1, pY + 1);
                }
            });

            // handle rendering tooltips for recipe inputs, can't be done in previous loop because of rendering order
            recipeLooper((pIndex, pInputSize, pX, pY) -> {
                if (pIndex < pInputSize && blockEntity.getInputHandler().getStackInSlot(pIndex).isEmpty()) {
                    ItemStack itemStack = RecipeDisplayUtil.getRecipeInputByIndex(recipe, pIndex);

                    if (pMouseX >= pX - 1 && pMouseX < pX + 17 && pMouseY >= pY - 1 && pMouseY < pY + 17 && !itemStack.isEmpty()) {
                        List<Component> components = RecipeDisplayUtil.getItemTooltipComponent(itemStack, new TranslatableComponent("alchemistry.container.required_input"));
                        renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
                    }
                }
            });

            // Render the target item
            ItemStack target = RecipeDisplayUtil.getTarget(recipe);
            renderFloatingItem(target, leftPos + 21, topPos + 30);
            if (pMouseX >= leftPos + 17 && pMouseX < leftPos + 41 && pMouseY >= topPos + 27 && pMouseY <= topPos + 50) {
                List<Component> components = RecipeDisplayUtil.getItemTooltipComponent(target, new TranslatableComponent("alchemistry.container.current_recipe"));
                renderTooltip(pPoseStack, components, Optional.empty(), pMouseX, pMouseY);
            }
        } else {
            // if the recipe is empty, we still need to render the slots
            recipeLooper((pIndex, pInputSize, pX, pY) -> renderSlot(pPoseStack, pX, pY));
        }
    }

    @FunctionalInterface
    interface LoopConsumer {
        void accept(int pIndex, int pInputSize, int pX, int pY);
    }

    private void recipeLooper(LoopConsumer pConsumer) {
        int inputSize = RecipeDisplayUtil.getInputSize(blockEntity);
        int totalRows = (inputSize / 2) + (inputSize % 2);
        int totalCols = (inputSize / 2) + (inputSize % 2);
        int xOrigin = totalRows == 1 ? leftPos + 20 : leftPos + 11;
        int yOrigin = topPos + 59;

        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                int index = col + row * 2;
                int x = xOrigin + col * 18;
                int y = yOrigin + row * 18;

                pConsumer.accept(index, inputSize, x, y);
            }
        }
    }

    private void renderSlot(PoseStack pPoseStack, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, "textures/gui/recipe_select_gui.png"));
        blit(pPoseStack, pX, pY, 0, imageHeight + RECIPE_BOX_SIZE * 3, RECIPE_BOX_SIZE, RECIPE_BOX_SIZE);
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

    public void renderParentTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        for (Widget renderable : parentScreen.renderables) {
            if (renderable instanceof AbstractWidget widget) {
                int xStart = widget.x;
                int xEnd = xStart + widget.getWidth();
                int yStart = widget.y;
                int yEnd = yStart + widget.getHeight();

                if (pMouseX > xStart && pMouseX < xEnd && pMouseY > yStart && pMouseY < yEnd) {
                    renderTooltip(pPoseStack, widget.getMessage(), pMouseX, pMouseY);
                }
            }
        }
    }

    // Input methods

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
            searchBox.setFocus(true);
            searchBox.setEditable(true);
            searchBox.active = true;
        } else {
            if (searchBox.isFocused() || searchBox.isActive()) {
                searchBox.setFocus(false);
                searchBox.active = false;
            }
        }
        scrolling = false;

        int lastDisplayedIndex = startIndex + MAX_DISPLAYED_RECIPES;

        for (int index = startIndex; index < lastDisplayedIndex; index++) {
            int currentIndex = index - startIndex;
            double boxX = pMouseX - (double)(recipeBoxLeftPos + currentIndex % COLUMNS * RECIPE_BOX_SIZE);
            double boxY = pMouseY - (double)(recipeBoxTopPos + currentIndex / COLUMNS * RECIPE_BOX_SIZE);

            if (boxX > 0 && boxX <= RECIPE_BOX_SIZE + 1 && boxY > 0 && boxY <= RECIPE_BOX_SIZE + 1 && !blockEntity.isRecipeLocked() && isValidRecipeIndex(index)) {
                AbstractProcessingRecipe recipe = getDisplayedRecipes().get(index);
                Alchemistry.PACKET_HANDLER.sendToServer(new SetRecipePacket(blockEntity.getBlockPos(), recipe.getId(), recipe.getGroup()));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                return true;
            }

            int scrollMinX = leftPos + 154;
            int scrollMinY = topPos + 28;
            int scrollMaxX = scrollMinX + 12;
            int scrollMaxY = scrollMinY + 108;

            if (pMouseX >= scrollMinX && pMouseX < scrollMaxX && pMouseY >= scrollMinY && pMouseY < scrollMaxY) {
                scrolling = true;
            }
        }

        for (Widget renderable : parentScreen.renderables) {
            if (renderable instanceof AbstractWidget widget) {
                int xStart = widget.x;
                int xEnd = xStart + widget.getWidth();
                int yStart = widget.y;
                int yEnd = yStart + widget.getHeight();

                if (pMouseX > xStart && pMouseX < xEnd && pMouseY > yStart && pMouseY < yEnd) {
                    return parentScreen.mouseClicked(pMouseX, pMouseY, pButton);
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (scrolling && isScrollBarActive()) {
            int scrollbarTopPos = topPos + 28;
            int scrollbarBottomPos = scrollbarTopPos + 108;
            scrollOffset = ((float) pMouseY - (float) scrollbarTopPos - 7.5f) / ((float) (scrollbarBottomPos - scrollbarTopPos) - 15.0f);
            scrollOffset = Mth.clamp(scrollOffset, 0.0f, 1.0f);
            startIndex = (int) ((double) (scrollOffset * (float) getOffscreenRows()) + 0.5d) * COLUMNS;
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (pMouseX >= leftPos && pMouseX < leftPos + imageWidth && pMouseY >= topPos && pMouseY < topPos + imageHeight && isScrollBarActive()) {
            scrollOffset = Mth.clamp(scrollOffset - (float) pDelta / (float) getOffscreenRows(), 0.0f, 1.0f);
            startIndex = (int) ((double) (scrollOffset * (float) getOffscreenRows()) + 0.5d) * COLUMNS;
        }
        return true;
    }

    // getters and setters

    private int getOffscreenRows() {
        return (displayedRecipes.size() + 6 - 1) / 6 - 3;
    }

    public void setTopPos(int topPos) {
        this.topPos = topPos;
    }

    public LinkedList<AbstractProcessingRecipe> getDisplayedRecipes() {
        return displayedRecipes;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isScrollBarActive() {
        return displayedRecipes.size() > MAX_DISPLAYED_RECIPES;
    }

    private boolean isValidRecipeIndex(int pSlot) {
        return pSlot >= 0 && pSlot < getDisplayedRecipes().size();
    }

    // Utility methods

    public void resetDisplayedRecipes() {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(recipes);
        this.displayedRecipes.sort((r1, r2) -> r1.getId().compareNamespaced(r2.getId()));
    }

    private void searchRecipeList(String pKeyword) {
        getDisplayedRecipes().clear();

        LinkedList<AbstractProcessingRecipe> recipes = blockEntity.getAllRecipes().stream()
                .filter(recipe -> {
                    Pair<ResourceLocation, String> searchablePair = RecipeDisplayUtil.getSearchablePair(recipe);
                    ResourceLocation registryName = searchablePair.getLeft();
                    String description = searchablePair.getRight();
                    String keyword = pKeyword.toLowerCase();

                    if (keyword.charAt(0) == '@') {
                        if (keyword.contains(" ")) {
                            if (keyword.split(" ").length > 1) {
                                String[] splitKeyword = keyword.split(" ");
                                return registryName.getNamespace().contains(splitKeyword[0].substring(1)) && registryName.getPath().contains(splitKeyword[1]);
                            }
                            return registryName.getNamespace().contains(keyword.substring(1, keyword.length() - 1));
                        }
                        return registryName.getNamespace().contains(keyword.substring(1));
                    }
                    return description.toLowerCase().contains(keyword);
                })
                .collect(Collectors.toCollection(LinkedList::new));

        getDisplayedRecipes().addAll(recipes);
    }
}
