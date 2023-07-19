package com.smashingmods.alchemistry.common.block.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.container.RecipeSelectorScreen;
import com.smashingmods.alchemistry.client.container.SideModeScreen;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemylib.client.button.LockButton;
import com.smashingmods.alchemylib.client.button.PauseButton;
import com.smashingmods.alchemylib.client.button.RecipeSelectorButton;
import com.smashingmods.alchemylib.client.button.SideModeButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class CompactorScreen extends AbstractProcessingScreen<CompactorMenu> {

    protected final List<AbstractDisplayData> displayData = new ArrayList<>();

    private final LockButton lockButton = new LockButton(this);
    private final PauseButton pauseButton = new PauseButton(this);
    private final SideModeButton sideModeButton;

    private final RecipeSelectorScreen<CompactorScreen, CompactorBlockEntity, CompactorRecipe> recipeSelectorScreen;
    private final RecipeSelectorButton recipeSelector;

    public CompactorScreen(CompactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = 184;
        imageHeight = 163;

        displayData.add(new ProgressDisplayData(pMenu.getBlockEntity(),78, 54, 60, 9, Direction2D.RIGHT));
        displayData.add(new EnergyDisplayData(pMenu.getBlockEntity(),12, 12, 16, 54));

        SideModeScreen<CompactorScreen> sideModeScreen = new SideModeScreen<>(this);
        sideModeButton = new SideModeButton(this, sideModeScreen);

        recipeSelectorScreen = new RecipeSelectorScreen<>(this, (CompactorBlockEntity) getMenu().getBlockEntity(), RecipeRegistry.getCompactorRecipes(pMenu.getLevel()));
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
        renderDisplayTooltip(displayData, pGuiGraphics, leftPos, topPos, pMouseX, pMouseY);

        renderTarget(pGuiGraphics, pMouseX, pMouseY);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(new ResourceLocation(Alchemistry.MODID, "textures/gui/compactor_gui.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        Component title = MutableComponent.create(new TranslatableContents("alchemistry.container.compactor", null, TranslatableContents.NO_ARGS));
        pGuiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, -10, 0xFFFFFFFF);
    }

    private void renderTarget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {

        if (menu.getBlockEntity().getRecipe() instanceof CompactorRecipe compactorRecipe) {
            ItemStack target = compactorRecipe.getOutput();

            int xStart = leftPos + 83;
            int xEnd = xStart + 18;
            int yStart = topPos + 15;
            int yEnd = yStart + 18;

            if (!target.isEmpty()) {
                pGuiGraphics.renderItem(target, xStart, yStart);
                if (pMouseX >= xStart && pMouseX < xEnd && pMouseY >= yStart && pMouseY < yEnd) {
                    List<Component> components = new ArrayList<>();
                    components.add(0, MutableComponent.create(new TranslatableContents("alchemistry.container.target", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                    components.addAll(target.getTooltipLines(getMinecraft().player, TooltipFlag.Default.NORMAL));
                    pGuiGraphics.renderTooltip(font, components, target.getTooltipImage(), pMouseX, pMouseY);
                }
            }
        }
    }
}
