package com.smashingmods.alchemistry.client.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.client.jei.RecipeTypes;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
public class DissolverRecipeCategory implements IRecipeCategory<DissolverRecipe> {

    private IGuiHelper guiHelper;

    public DissolverRecipeCategory() {}

    public DissolverRecipeCategory(IGuiHelper pGuiHelper) {
        this.guiHelper = pGuiHelper;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("alchemistry.jei.dissolver");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/dissolver_jei.png"), 0, 0, 150, 150);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.DISSOLVER.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @Override
    public Class<? extends DissolverRecipe> getRecipeClass() {
        return DissolverRecipe.class;
    }

    @Override
    public RecipeType<DissolverRecipe> getRecipeType() {
        return RecipeTypes.DISSOLVER;
    }

    @Override
    public void draw(DissolverRecipe pRecipe, IRecipeSlotsView pRecipeSlotsView, PoseStack pPoseStack, double pMouseX, double pMouseY) {

        Font font = Minecraft.getInstance().font;

        List<Double> probabilities = pRecipe.getOutput().getProbabilityGroups().stream().map(ProbabilityGroup::getProbability).collect(Collectors.toList());
        int groupCount = pRecipe.getOutput().getProbabilityGroups().size();
        double totalProbability = pRecipe.getOutput().getProbabilityGroups().stream().mapToDouble(ProbabilityGroup::getProbability).sum();
        boolean weighted = pRecipe.getOutput().isWeighted();
        int rolls = pRecipe.getOutput().getRolls();

        font.drawShadow(pPoseStack, String.format("Weighted: %s", weighted), 0, 0, 0xFFFFFFFF);
        font.drawShadow(pPoseStack, String.format("Rolls: %s", rolls), 0, 24, 0xFFFFFFFF);

        int xOrigin = 41;
        int yOrigin = 58;

        probabilities.forEach(aDouble -> {
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 2; column++) {
                    int index = column + row * 2;
                    int x = xOrigin + column * 52;
                    int y = yOrigin + row * 21;

                    if (groupCount > index) {

                        if (weighted) {
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            numberFormat.setMaximumFractionDigits(1);
                            double value = groupCount == 1 ? probabilities.get(0) : probabilities.get(index);
                            double percent = (value / totalProbability) * 100;
                            font.drawShadow(pPoseStack, numberFormat.format(percent) + "%", x, y, 0xFFFFFFFF);
                        } else {
                            double value = groupCount == 1 ? probabilities.get(0) : probabilities.get(index);
                            font.drawShadow(pPoseStack, value+ "%", x, y, 0xFFFFFFFF);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, DissolverRecipe pRecipe, IFocusGroup pFocusGroup) {
        pBuilder.addSlot(RecipeIngredientRole.INPUT, 78, 17).addIngredients(pRecipe.getInput());

        int xOrigin = 23;
        int yOrigin = 54;

        List<ItemStack> items = new ArrayList<>();

        pRecipe.getOutput().getProbabilityGroups().stream().forEach(group -> items.addAll(group.getOutput()));

        items.forEach(itemStack -> {
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 2; column++) {
                    int itemIndex = column + row * 2;
                    int x = xOrigin + column * 52;
                    int y = yOrigin + row * 21;

                    if (items.size() > itemIndex) {
                        pBuilder.addSlot(RecipeIngredientRole.OUTPUT, x, y).addItemStack(items.get(itemIndex));
                    }
                }
            }
        });
    }
}
