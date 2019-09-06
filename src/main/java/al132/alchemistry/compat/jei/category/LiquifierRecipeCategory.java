package al132.alchemistry.compat.jei.category;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Ref;
import al132.alchemistry.compat.jei.JEIIntegration;
import al132.alchemistry.recipes.LiquifierRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class LiquifierRecipeCategory implements IRecipeCategory<LiquifierRecipe> {
    private IGuiHelper guiHelper;
    private final static int u = 40;
    private final static int v = 11;

    public LiquifierRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Alchemistry.data.MODID, JEIIntegration.LIQUIFIER_CATEGORY);
    }

    @Override
    public Class<? extends LiquifierRecipe> getRecipeClass() {
        return LiquifierRecipe.class;
    }

    @Override
    public String getTitle() {
        return I18n.format("alchemistry.jei.liquifier");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.data.MODID, "textures/gui/liquifier_gui.png"),
                u, v, 100, 125);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(new ItemStack(Ref.liquifier));
    }

    @Override
    public void setIngredients(LiquifierRecipe recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.input);
        ingredients.setOutput(VanillaTypes.FLUID, recipe.output);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, LiquifierRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();


        int x = 48 - u;
        int y = 57 - v;
        guiItemStacks.init(0, true, x, y);
        guiItemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        x = 122 - u;
        y = 40 - v;
        FluidStack outputFluidStack = ingredients.getOutputs(VanillaTypes.FLUID).get(0).get(0);
        guiFluidStacks.init(1, true, x, y, 16, 60, outputFluidStack.getAmount(), false, null);
        guiFluidStacks.set(1, outputFluidStack);
    }
}
