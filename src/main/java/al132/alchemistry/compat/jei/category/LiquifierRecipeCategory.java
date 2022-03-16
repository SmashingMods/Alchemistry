package al132.alchemistry.compat.jei.category;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.Registration;
import al132.alchemistry.blocks.liquifier.LiquifierRecipe;
import al132.alchemistry.compat.jei.JEIIntegration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.LIQUIFIER_CATEGORY);
    }

    @Override
    public Class<? extends LiquifierRecipe> getRecipeClass() {
        return LiquifierRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TextComponent(I18n.get("alchemistry.jei.liquifier"));
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/liquifier_gui.png"),
                u, v, 100, 125);
    }

    @Override
    public IDrawable getIcon() {
        return guiHelper.createDrawableIngredient(new ItemStack(Registration.LIQUIFIER_BLOCK.get()));//.liquifier));
    }

    @Override
    public void setIngredients(LiquifierRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.input.toStacks());
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
