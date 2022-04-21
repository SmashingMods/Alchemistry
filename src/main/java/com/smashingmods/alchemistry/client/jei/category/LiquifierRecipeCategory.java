package com.smashingmods.alchemistry.client.jei.category;

//public class LiquifierRecipeCategory implements IRecipeCategory<LiquifierRecipe> {
//    private IGuiHelper guiHelper;
//    private final static int u = 40;
//    private final static int v = 11;
//
//    public LiquifierRecipeCategory(IGuiHelper guiHelper) {
//        this.guiHelper = guiHelper;
//    }
//
//    @Override
//    public ResourceLocation getUid() {
//        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.LIQUIFIER_CATEGORY);
//    }
//
//    @Override
//    public Class<? extends LiquifierRecipe> getRecipeClass() {
//        return LiquifierRecipe.class;
//    }
//
//    @Override
//    public Component getTitle() {
//        return new TextComponent(I18n.get("alchemistry.jei.liquifier"));
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/liquifier_gui.png"),
//                u, v, 100, 125);
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return guiHelper.createDrawableIngredient(new ItemStack(BlockRegistry.LIQUIFIER_BLOCK.get()));//.liquifier));
//    }
//
//    @Override
//    public void setIngredients(LiquifierRecipe recipe, IIngredients ingredients) {
//        ingredients.setInputs(VanillaTypes.ITEM, recipe.input.toStacks());
//        ingredients.setOutput(VanillaTypes.FLUID, recipe.output);
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, LiquifierRecipe recipe, IIngredients ingredients) {
//        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
//
//
//        int x = 48 - u;
//        int y = 57 - v;
//        guiItemStacks.init(0, true, x, y);
//        guiItemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
//
//        x = 122 - u;
//        y = 40 - v;
//        FluidStack outputFluidStack = ingredients.getOutputs(VanillaTypes.FLUID).get(0).get(0);
//        guiFluidStacks.init(1, true, x, y, 16, 60, outputFluidStack.getAmount(), false, null);
//        guiFluidStacks.set(1, outputFluidStack);
//    }
//}
