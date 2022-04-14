package com.smashingmods.alchemistry.compat.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.block.fission.FissionRecipe;
import com.smashingmods.alchemistry.compat.jei.JEIIntegration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FissionRecipeCategory implements IRecipeCategory<FissionRecipe> {
    private IGuiHelper guiHelper;
    private final static int u = 45;
    private final static int v = 41;

    public FissionRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.FISSION_CATEGORY);
    }

    @Override
    public Class<? extends FissionRecipe> getRecipeClass() {
        return FissionRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TextComponent(I18n.get("alchemistry.jei.fission"));
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/fission_gui.png"),
                u, v, 120, 50);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(new ItemStack(Registry.FISSION_CONTROLLER_BLOCK.get()));
    }

    @Override
    public void setIngredients(FissionRecipe recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputs());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FissionRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int x = 48 - u;
        int y = 59 - v;
        guiItemStacks.init(0, true, x, y);
        guiItemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        x = 121 - u;
        List<ItemStack> output1 = ingredients.getOutputs(VanillaTypes.ITEM).get(0);
        guiItemStacks.init(1, false, x, y);
        guiItemStacks.set(1, output1);
        x += 18;
        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 1) {
            List<ItemStack> output2 = ingredients.getOutputs(VanillaTypes.ITEM).get(1);
            if (!output2.get(0).isEmpty()) {
                guiItemStacks.init(2, false, x, y);
                guiItemStacks.set(2, output2);
            }
        }
    }
}
