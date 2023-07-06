package com.smashingmods.alchemistry.client.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FusionRecipeCategory implements IRecipeCategory<FusionRecipe> {

    private IGuiHelper guiHelper;

    public FusionRecipeCategory() {}

    public FusionRecipeCategory(IGuiHelper pGuiHelper) {
        this.guiHelper = pGuiHelper;
    }

    @Override
    public Component getTitle() {
        return MutableComponent.create(new TranslatableContents("alchemistry.jei.fusion_controller", null, TranslatableContents.NO_ARGS));
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/fusion_jei.png"), 0, 0, 150, 75);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.FUSION_CONTROLLER.get()));
    }

    @Override
    public RecipeType<FusionRecipe> getRecipeType() {
        return RecipeTypes.FUSION;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, FusionRecipe pRecipe, IFocusGroup pFocusGroup) {
        pBuilder.addSlot(RecipeIngredientRole.INPUT, 14, 8).addItemStack(pRecipe.getInput1());
        pBuilder.addSlot(RecipeIngredientRole.INPUT, 14, 42).addItemStack(pRecipe.getInput2());

        pBuilder.addSlot(RecipeIngredientRole.OUTPUT, 113, 24).addItemStack(pRecipe.getOutput());
    }
}
