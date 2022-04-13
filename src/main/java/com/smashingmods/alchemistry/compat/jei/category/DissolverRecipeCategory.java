package com.smashingmods.alchemistry.compat.jei.category;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.blocks.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.compat.jei.JEIIntegration;
import com.smashingmods.alchemistry.misc.ProbabilityGroup;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class DissolverRecipeCategory implements IRecipeCategory<DissolverRecipe> {
    private final IGuiHelper guiHelper;
    private final static int u = 5;
    private final static int v = 5;
    private final static int INPUT_ONE = 2;
    private final static int OUTPUT_STARTING_INDEX = 3;
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public DissolverRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    @Nonnull
    public RecipeType<DissolverRecipe> getRecipeType() {
        return new RecipeType<>(new ResourceLocation(Alchemistry.MODID, JEIIntegration.DISSOLVER_CATEGORY), DissolverRecipe.class);
    }

    @Override
    @Nonnull
    public Component getTitle() {
        return new TranslatableComponent("alchemistry.jei.dissolver");
    }

    @Override
    @Nonnull
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/dissolver_jei.png"),
                u, v, 180, 256);
    }

    @Override
    @Nonnull
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Registry.DISSOLVER_BLOCK.get()));
    }

    @Override
    public void setRecipe(IRecipeLayout pRecipeLayout, DissolverRecipe pRecipe, @Nonnull IIngredients pIngredients) {
        IGuiItemStackGroup guiItemStacks = pRecipeLayout.getItemStacks();
        //List<ItemStack> inputStack = recipe.getInputs();//ingredients.getInputs(VanillaTypes.ITEM)[0]
        List<ProbabilityGroup> outputSet = pRecipe.outputs.getSet();
        int x = 95 - u;
        int y = 7 - v;
        guiItemStacks.init(INPUT_ONE, true, x, y);
        guiItemStacks.set(INPUT_ONE, Arrays.asList(pRecipe.inputIngredient.ingredient.getItems()));
        x = 50 - u;
        y = 50 - v;

        int outputSlotIndex = OUTPUT_STARTING_INDEX;
        for (ProbabilityGroup component : outputSet) {
            for (ItemStack stack : component.getOutputs()) {
                guiItemStacks.init(outputSlotIndex, false, x, y);
                if (!stack.isEmpty()) {
                    guiItemStacks.set(outputSlotIndex, stack);
                }
                x += 18;
                outputSlotIndex++;
            }
            x = 50 - u;
            y += 18;
        }
    }

    @Override
    public void draw(DissolverRecipe pRecipe, @Nonnull IRecipeSlotsView pRecipeSlotsView, @Nonnull PoseStack pPoseStack, double pMouseX, double pMouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int y = 50;
        for (int index = 0; index < pRecipe.outputs.getSet().size(); index++) {
            String text = formatProbability(pRecipe, pRecipe.outputs.probabilityAtIndex(index));
            minecraft.font.draw(pPoseStack, text, 0/*-5*/, y, Color.BLACK.getRGB());
            y += 18;
        }

        String probabilityType = "";
        if (pRecipe.outputs.relativeProbability) probabilityType = I18n.get("alchemistry.jei.dissolver.relative");
        else probabilityType = I18n.get("alchemistry.jei.dissolver.absolute");
        String typeLabel = I18n.get("alchemistry.jei.dissolver.type");
        String rollsLabel = I18n.get("alchemistry.jei.dissolver.rolls");
        int rolls = pRecipe.outputs.rolls;
        minecraft.font.draw(pPoseStack, typeLabel + ": " + probabilityType, 5, 4, Color.BLACK.getRGB());
        minecraft.font.draw(pPoseStack, rollsLabel + ": " + rolls, 5, 16, Color.BLACK.getRGB());
    }

    @Override
    public ResourceLocation getUid() {
        return null;
    }

    @Override
    public Class<? extends DissolverRecipe> getRecipeClass() {
        return null;
    }

    public String formatProbability(DissolverRecipe recipe, double probability) {//probability: Double): String {
        if (recipe.outputs.relativeProbability) return DECIMAL_FORMAT.format(probability * 100) + "%";
        else return DECIMAL_FORMAT.format(probability) + "%";
    }
}
