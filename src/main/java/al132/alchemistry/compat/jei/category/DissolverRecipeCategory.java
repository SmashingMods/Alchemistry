package al132.alchemistry.compat.jei.category;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Registration;
import al132.alchemistry.blocks.dissolver.DissolverRecipe;
import al132.alchemistry.compat.jei.JEIIntegration;
import al132.alchemistry.misc.ProbabilityGroup;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class DissolverRecipeCategory implements IRecipeCategory<DissolverRecipe> {
    private IGuiHelper guiHelper;
    private final static int u = 5;
    private final static int v = 5;
    private final static int INPUT_ONE = 2;
    private final static int OUTPUT_STARTING_INDEX = 3;


    public DissolverRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.DISSOLVER_CATEGORY);
    }

    @Override
    public Class<? extends DissolverRecipe> getRecipeClass() {
        return DissolverRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TextComponent(I18n.get("alchemistry.jei.dissolver"));
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/dissolver_jei.png"),
                u, v, 180, 256);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(new ItemStack(Registration.DISSOLVER_BLOCK.get()));
    }

    @Override
    public void setIngredients(DissolverRecipe recipe, IIngredients ingredients) {
        List<Ingredient> inputs = Lists.newArrayList(recipe.inputIngredient.ingredient);
        ingredients.setInputIngredients(inputs);
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs.filterNonEmpty());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DissolverRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        //List<ItemStack> inputStack = recipe.getInputs();//ingredients.getInputs(VanillaTypes.ITEM)[0]
        List<ProbabilityGroup> outputSet = recipe.outputs.getSet();
        int x = 95 - u;
        int y = 7 - v;
        guiItemStacks.init(INPUT_ONE, true, x, y);
        guiItemStacks.set(INPUT_ONE, Arrays.asList(recipe.inputIngredient.ingredient.getItems()));
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
    public void draw(DissolverRecipe recipe, PoseStack ms, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int y = 50;
        for (int index = 0; index < recipe.outputs.getSet().size(); index++) {
            String text = formatProbability(recipe, recipe.outputs.probabilityAtIndex(index));
            minecraft.font.draw(ms, text, 0/*-5*/, y, Color.BLACK.getRGB());
            y += 18;
        }

        String probabilityType = "";
        if (recipe.outputs.relativeProbability) probabilityType = I18n.get("alchemistry.jei.dissolver.relative");
        else probabilityType = I18n.get("alchemistry.jei.dissolver.absolute");
        String typeLabel = I18n.get("alchemistry.jei.dissolver.type");
        String rollsLabel = I18n.get("alchemistry.jei.dissolver.rolls");
        int rolls = recipe.outputs.rolls;
        minecraft.font.draw(ms, typeLabel + ": " + probabilityType, 5, 4, Color.BLACK.getRGB());
        minecraft.font.draw(ms, rollsLabel + ": " + rolls, 5, 16, Color.BLACK.getRGB());
    }

    public String formatProbability(DissolverRecipe recipe, double probability) {//probability: Double): String {
        if (recipe.outputs.relativeProbability) return Alchemistry.DECIMAL_FORMAT.format(probability * 100) + "%";
        else return Alchemistry.DECIMAL_FORMAT.format(probability) + "%";
    }
}
