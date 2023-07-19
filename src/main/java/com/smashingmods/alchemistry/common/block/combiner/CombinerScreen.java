package com.smashingmods.alchemistry.common.block.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.container.RecipeSelectorScreen;
import com.smashingmods.alchemistry.client.container.SideModeScreen;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.container.FakeItemRenderer;
import com.smashingmods.alchemylib.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.client.button.LockButton;
import com.smashingmods.alchemylib.client.button.PauseButton;
import com.smashingmods.alchemylib.client.button.RecipeSelectorButton;
import com.smashingmods.alchemylib.client.button.SideModeButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CombinerScreen extends AbstractProcessingScreen<CombinerMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();
    private final CombinerBlockEntity blockEntity;

    private final LockButton lockButton = new LockButton(this);
    private final PauseButton pauseButton = new PauseButton(this);
    private final SideModeButton sideModeButton;
    private final RecipeSelectorScreen<CombinerScreen, CombinerBlockEntity, CombinerRecipe> recipeSelectorScreen;
    private final RecipeSelectorButton recipeSelector;

    public CombinerScreen(CombinerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 184;
        this.imageHeight = 193;
        this.displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(), 87, 35, 60, 9, Direction2D.RIGHT));
        this.displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(), 12, 12, 16, 54));
        this.blockEntity = (CombinerBlockEntity) pMenu.getBlockEntity();

        SideModeScreen<CombinerScreen> sideModeScreen = new SideModeScreen<>(this);
        sideModeButton = new SideModeButton(this, sideModeScreen);

        recipeSelectorScreen = new RecipeSelectorScreen<>(this, (CombinerBlockEntity) getMenu().getBlockEntity(), RecipeRegistry.getCombinerRecipes(pMenu.getLevel()));
        recipeSelector = new RecipeSelectorButton(this, recipeSelectorScreen);
    }

    @Override
    protected void init() {
        recipeSelectorScreen.setTopPos((height - imageHeight) / 2);
        widgets.add(lockButton);
        widgets.add(pauseButton);
        widgets.add(recipeSelector);
        widgets.add(sideModeButton);
        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderDisplayData(displayData, pGuiGraphics, leftPos, topPos);
        renderCurrentRecipe(pGuiGraphics, pMouseX, pMouseY);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        renderDisplayTooltip(displayData, pGuiGraphics, leftPos, topPos, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_gui.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        Component title = MutableComponent.create(new TranslatableContents("alchemistry.container.combiner", null, TranslatableContents.NO_ARGS));
        pGuiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    private void renderCurrentRecipe(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        CombinerRecipe currentRecipe = menu.getBlockEntity().getRecipe();
        ProcessingSlotHandler handler = blockEntity.getInputHandler();

        if (currentRecipe != null) {
            ItemStack currentOutput = currentRecipe.getOutput();
            pGuiGraphics.renderItem(currentOutput, leftPos + 152, topPos + 15);

            if (pMouseX >= leftPos + 149 && pMouseX < leftPos + 173  && pMouseY >= topPos + 11 && pMouseY < topPos + 35) {
                renderItemTooltip(pGuiGraphics, currentOutput, MutableComponent.create(new TranslatableContents("alchemistry.container.current_recipe", "Current recipe:", TranslatableContents.NO_ARGS)), pMouseX, pMouseY);
            }

            int xOrigin = leftPos + 48;
            int yOrigin = topPos + 22;

            for (int row = 0; row < 2; row++) {
                for (int column = 0; column < 2; column++) {
                    int index = column + row * 2;
                    int x = xOrigin + column * 18;
                    int y = yOrigin + row * 18;

                    if (index < currentRecipe.getInput().size()) {

                        ItemStack itemStack = currentRecipe.getInput().get(index).getIngredient().getItems()[(int) (Math.random() * currentRecipe.getInput().get(index).getIngredient().getItems().length)];

                        boolean required = handler.getStacks().stream().noneMatch(handlerItem -> {
                            boolean sameItem = ItemStack.isSameItemSameTags(itemStack, handlerItem);
                            boolean minCount = handlerItem.getCount() >= itemStack.getCount();
                            return sameItem && minCount;
                        });

                        if (handler.getStackInSlot(index).isEmpty() && required) {
                            FakeItemRenderer.renderFakeItem(pGuiGraphics, itemStack, x, y, true);
                            if (pMouseX >= x - 2 && pMouseX < x + 16 && pMouseY >= y - 1 && pMouseY < y + 17) {
                                renderItemTooltip(pGuiGraphics, itemStack, MutableComponent.create(new TranslatableContents("alchemistry.container.required_input", "Required input item:", TranslatableContents.NO_ARGS)), pMouseX, pMouseY);
                            }
                        }
                    }
                }
            }
        }
    }
}
