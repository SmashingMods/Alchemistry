package com.smashingmods.alchemistry.client.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("removal")
public class AtomizerRecipeCategory implements IRecipeCategory<AtomizerRecipe> {

    private IGuiHelper guiHelper;
    private static final int u = 40;
    private static final int v = 11;

    public AtomizerRecipeCategory() {}

    public AtomizerRecipeCategory(IGuiHelper pGuiHelper) {
        this.guiHelper = pGuiHelper;
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return new TranslatableComponent("alchemistry.jei.atomizer");
    }

    @Override
    @Nonnull
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png"), u, v, 100, 125);
    }

    @Override
    @Nonnull
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.ATOMIZER.get()));
    }

    @Override
    @Nonnull
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    @Nonnull
    public Class<? extends AtomizerRecipe> getRecipeClass() {
        return AtomizerRecipe.class;
    }

    @Override
    @Nonnull
    public RecipeType<AtomizerRecipe> getRecipeType() {
        return RecipeTypes.ATOMIZER;
    }
}
