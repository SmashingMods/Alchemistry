package com.smashingmods.alchemistry.client.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
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
public class CompactorRecipeCategory implements IRecipeCategory<CompactorRecipe> {

    private IGuiHelper guiHelper;

    public CompactorRecipeCategory() {}

    public CompactorRecipeCategory(IGuiHelper pGuiHelper) {
        this.guiHelper = pGuiHelper;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("alchemistry.jei.compactor");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/compactor_jei.png"), 0, 0, 150, 75);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.COMPACTOR.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends CompactorRecipe> getRecipeClass() {
        return CompactorRecipe.class;
    }

    @Override
    public RecipeType<CompactorRecipe> getRecipeType() {
        return RecipeTypes.COMPACTOR;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, CompactorRecipe pRecipe, IFocusGroup pFocusGroup) {
        pBuilder.addSlot(RecipeIngredientRole.INPUT, 14, 24).addItemStack(pRecipe.getInput());
        pBuilder.addSlot(RecipeIngredientRole.OUTPUT, 113, 24).addItemStack(pRecipe.getOutput());
    }
}
