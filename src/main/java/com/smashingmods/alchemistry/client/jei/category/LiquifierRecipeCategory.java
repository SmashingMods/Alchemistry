package com.smashingmods.alchemistry.client.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("removal")
public class LiquifierRecipeCategory implements IRecipeCategory<LiquifierRecipe> {

    private IGuiHelper guiHelper;

    public LiquifierRecipeCategory() {}

    public LiquifierRecipeCategory(IGuiHelper pGuiHelper) {
        this.guiHelper = pGuiHelper;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("alchemistry.jei.liquifier");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/liquifier_jei.png"), 0, 0, 150, 75);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.LIQUIFIER.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends LiquifierRecipe> getRecipeClass() {
        return LiquifierRecipe.class;
    }

    @Override
    public RecipeType<LiquifierRecipe> getRecipeType() {
        return RecipeTypes.LIQUIFIER;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, LiquifierRecipe pRecipe, IFocusGroup pFocusGroup) {
        pBuilder.addSlot(RecipeIngredientRole.INPUT, 18, 29).addItemStack(pRecipe.getInput());
        pBuilder.addSlot(RecipeIngredientRole.OUTPUT, 118, 29).addIngredient(VanillaTypes.FLUID, pRecipe.getOutput());
    }
}
