package al132.alchemistry.compat.jei.category;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Ref;
import al132.alchemistry.compat.jei.JEIIntegration;
import al132.alchemistry.recipes.DissolverRecipe;
import al132.alchemistry.recipes.ProbabilityGroup;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
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
        return new ResourceLocation(Alchemistry.data.MODID, JEIIntegration.DISSOLVER_CATEGORY);
    }

    @Override
    public Class<? extends DissolverRecipe> getRecipeClass() {
        return DissolverRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("alchemistry.jei.dissolver");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.data.MODID, "textures/gui/dissolver_jei.png"),
                u, v, 180, 256);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(new ItemStack(Ref.dissolver));
    }

    @Override
    public void setIngredients(DissolverRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.inputs);
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs.toStackList());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DissolverRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        List<ItemStack> inputStack = recipe.inputs;//ingredients.getInputs(VanillaTypes.ITEM)[0]
        List<ProbabilityGroup> outputSet = recipe.outputs.getSet();
        int x = 95 - u;
        int y = 7 - v;
        guiItemStacks.init(INPUT_ONE, true, x, y);
        guiItemStacks.set(INPUT_ONE, inputStack);
        x = 50 - u;
        y = 50 - v;

        int outputSlotIndex = OUTPUT_STARTING_INDEX;
        for (ProbabilityGroup component : outputSet) {
            for (ItemStack stack : component.getOutputs()) {
                guiItemStacks.init(outputSlotIndex, false, x, y);
                guiItemStacks.set(outputSlotIndex, stack);
                x += 18;
                outputSlotIndex++;
            }
            x = 50 - u;
            y += 18;
        }
    }

    @Override
    public void draw(DissolverRecipe recipe, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int y = 50;
        for (int index = 0; index < recipe.outputs.getSet().size(); index++) {
            String text = formatProbability(recipe, recipe.outputs.probabilityAtIndex(index));
            minecraft.fontRenderer.drawString(text, 0/*-5*/, y, Color.BLACK.getRGB());
            y += 18;
        }

        String probabilityType = "";
        if (recipe.outputs.relativeProbability) probabilityType = I18n.format("alchemistry.jei.dissolver.relative");
        else probabilityType = I18n.format("alchemistry.jei.dissolver.absolute");
        String typeLabel = I18n.format("alchemistry.jei.dissolver.type");
        String rollsLabel = I18n.format("alchemistry.jei.dissolver.rolls");
        int rolls = recipe.outputs.rolls;
        minecraft.fontRenderer.drawString(typeLabel + ": " + probabilityType, 5, 4, Color.BLACK.getRGB());
        minecraft.fontRenderer.drawString(rollsLabel + ": " + rolls, 5, 16, Color.BLACK.getRGB());
    }

    public String formatProbability(DissolverRecipe recipe, double probability) {//probability: Double): String {
        if (recipe.outputs.relativeProbability) return Ref.DECIMAL_FORMAT.format(probability * 100) + "%";
        else return Ref.DECIMAL_FORMAT.format(probability) + "%";
    }
}
