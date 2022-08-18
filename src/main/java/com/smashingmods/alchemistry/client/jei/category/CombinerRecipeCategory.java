package com.smashingmods.alchemistry.client.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
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
public class CombinerRecipeCategory implements IRecipeCategory<CombinerRecipe> {

    private IGuiHelper guiHelper;

    public CombinerRecipeCategory() {}

    public CombinerRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("alchemistry.jei.combiner");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_jei.png"), 0, 0, 150, 75);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.COMBINER.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends CombinerRecipe> getRecipeClass() {
        return CombinerRecipe.class;
    }

    @Override
    public RecipeType<CombinerRecipe> getRecipeType() {
        return RecipeTypes.COMBINER;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, CombinerRecipe pRecipe, IFocusGroup pFocusGroup) {

        int xOrigin = 13;
        int yOrigin = 16;

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                int slotIndex = column + row * 2;
                int x = xOrigin + column * 19;
                int y = yOrigin + row * 19;

                if (pRecipe.getInput().size() > slotIndex) {
                    pBuilder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(pRecipe.getInput().get(slotIndex).toStacks());
                }
            }
        }

        pBuilder.addSlot(RecipeIngredientRole.OUTPUT, 113, 25).addItemStack(pRecipe.getOutput());
    }
}
