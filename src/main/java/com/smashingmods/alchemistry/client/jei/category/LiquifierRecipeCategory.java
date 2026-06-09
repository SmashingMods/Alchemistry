package com.smashingmods.alchemistry.client.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class LiquifierRecipeCategory implements IRecipeCategory<LiquifierRecipe> {

    private IGuiHelper guiHelper;
    private IDrawable background;

    public LiquifierRecipeCategory() {}

    public LiquifierRecipeCategory(IGuiHelper pGuiHelper) {
        this.guiHelper = pGuiHelper;
    }

    @Override
    public Component getTitle() {
        return MutableComponent.create(new TranslatableContents("alchemistry.jei.liquifier", null, TranslatableContents.NO_ARGS));
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 75;
    }

    @Override
    public void draw(LiquifierRecipe pRecipe, IRecipeSlotsView pRecipeSlotsView, GuiGraphics pGuiGraphics, double pMouseX, double pMouseY) {
        if (background == null) {
            background = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "textures/gui/liquifier_jei.png"), 0, 0, 150, 75);
        }
        background.draw(pGuiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.LIQUIFIER.get()));
    }

    @Override
    public IRecipeType<LiquifierRecipe> getRecipeType() {
        return RecipeTypes.LIQUIFIER;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, LiquifierRecipe pRecipe, IFocusGroup pFocusGroup) {
        pBuilder.addSlot(RecipeIngredientRole.INPUT, 14, 24).addItemStacks(pRecipe.getInput().toStacks());
        pBuilder.addSlot(RecipeIngredientRole.OUTPUT, 113, 24).addIngredient(NeoForgeTypes.FLUID_STACK, pRecipe.getOutput());
    }
}
