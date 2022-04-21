package com.smashingmods.alchemistry.client.jei.category;

//public class AtomizerRecipeCategory implements IRecipeCategory<AtomizerRecipe> {
//    private IGuiHelper guiHelper;
//    private final static int u = 40;
//    private final static int v = 11;
//
//    public AtomizerRecipeCategory(IGuiHelper guiHelper) {
//        this.guiHelper = guiHelper;
//    }
//
//    @Override
//    public ResourceLocation getUid() {
//        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.ATOMIZER_CATEGORY);
//    }
//
//    @Override
//    public Class<? extends AtomizerRecipe> getRecipeClass() {
//        return AtomizerRecipe.class;
//    }
//
//    @Override
//    public Component getTitle() {
//        return new TextComponent(I18n.get("alchemistry.jei.atomizer"));
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/atomizer_gui.png"),
//                u, v, 100, 125);
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return guiHelper.createDrawableIngredient(new ItemStack(BlockRegistry.ATOMIZER_BLOCK.get()));
//    }
//
//    @Override
//    public void setIngredients(AtomizerRecipe recipe, IIngredients ingredients) {
//        ingredients.setInput(VanillaTypes.FLUID, recipe.input);
//        ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, AtomizerRecipe recipe, IIngredients ingredients) {
//        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
//
//
//        int x = 121 - u;
//        int y = 51 - v;
//        guiItemStacks.init(0, false, x, y);
//        guiItemStacks.set(0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
//
//        x = 48 - u;
//        y = 69 - u;
//        FluidStack inputStack = ingredients.getInputs(VanillaTypes.FLUID).get(0).get(0);
//        guiFluidStacks.init(1, true, x, y, 16, 60, inputStack.getAmount(), false, null);
//        guiFluidStacks.set(1, inputStack);
//    }
//}
