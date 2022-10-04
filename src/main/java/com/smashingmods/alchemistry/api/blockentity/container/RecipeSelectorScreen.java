package com.smashingmods.alchemistry.api.blockentity.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;

import java.util.LinkedList;

public class RecipeSelectorScreen<B extends AbstractProcessingBlockEntity, R extends Recipe<Inventory>> extends Screen {

    private int imageWidth = 0;
    private int imageHeight = 0;

    private int leftPos = (width - imageWidth) / 2;
    private int topPos = (height - imageHeight) / 2;

    private final B blockEntity;
    private final LinkedList<R> recipes;
    private final LinkedList<R> displayedRecipes = new LinkedList<>();

    private final EditBox searchBox;

    private static final int DISPLAYED_SLOTS = 12;
    private static final int RECIPE_BOX_SIZE = 18;

    private float scrollOffset;
    private boolean scrolling;
    private int startIndex;

    public RecipeSelectorScreen(B pBlockEntity, LinkedList<R> pRecipes) {
        super(TextComponent.EMPTY);
        this.blockEntity = pBlockEntity;
        this.recipes = pRecipes;
        this.searchBox = new EditBox(Minecraft.getInstance().font, 0, 0, 72, 12, new TextComponent(""));
        if (!blockEntity.getSearchText().isEmpty()) {
            searchBox.setValue(blockEntity.getSearchText());
            searchRecipeList(blockEntity.getSearchText());
        }
    }

    @Override
    public void tick() {
        if (searchBox.getValue().isEmpty()) {
            blockEntity.setSearchText("");
            resetDisplayedRecipes();
            searchBox.setSuggestion("alchemistry.container.search");
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
        renderBackground(pPoseStack);
        renderBg(pPoseStack);
        renderLabels(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

//        renderRecipeBox(pPoseStack, pMouseX, pMouseY)

        renderWidget(searchBox, leftPos + 57, topPos + 7);
    }

    public void resetDisplayedRecipes() {
        this.displayedRecipes.clear();
        this.displayedRecipes.addAll(recipes);
    }

    public void searchRecipeList(String pKeyword) {
        displayedRecipes.clear();
        displayedRecipes.addAll(recipes.stream()
                .filter(recipe -> recipe.getResultItem()
                        .getItem()
                        .getDescription()
                        .getString()
                        .toLowerCase()
                        .contains(pKeyword.toLowerCase()))
                .toList());
    }

    private void renderBg(PoseStack pPoseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Alchemistry.MODID, ""));
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    private void renderLabels(PoseStack pPoseStack) {
        Component title = new TranslatableComponent("alchemistry.container.search_box");
        drawString(pPoseStack, font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
