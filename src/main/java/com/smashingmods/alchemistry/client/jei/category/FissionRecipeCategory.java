package com.smashingmods.alchemistry.client.jei.category;

//public class FissionRecipeCategory implements IRecipeCategory<FissionRecipe> {
//    private IGuiHelper guiHelper;
//    private final static int u = 45;
//    private final static int v = 41;
//
//    public FissionRecipeCategory(IGuiHelper guiHelper) {
//        this.guiHelper = guiHelper;
//    }
//
//    @Override
//    public ResourceLocation getUid() {
//        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.FISSION_CATEGORY);
//    }
//
//    @Override
//    public Class<? extends FissionRecipe> getRecipeClass() {
//        return FissionRecipe.class;
//    }
//
//    @Override
//    public Component getTitle() {
//        return new TextComponent(I18n.get("alchemistry.jei.fission"));
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/fission_gui.png"),
//                u, v, 120, 50);
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return guiHelper.createDrawableIngredient(new ItemStack(BlockRegistry.FISSION_CONTROLLER_BLOCK.get()));
//    }
//
//    @Override
//    public void setIngredients(FissionRecipe recipe, IIngredients ingredients) {
//        ingredients.setInput(VanillaTypes.ITEM, recipe.getInput());
//        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getOutputs());
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, FissionRecipe recipe, IIngredients ingredients) {
//        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//
//        int x = 48 - u;
//        int y = 59 - v;
//        guiItemStacks.init(0, true, x, y);
//        guiItemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
//
//        x = 121 - u;
//        List<ItemStack> output1 = ingredients.getOutputs(VanillaTypes.ITEM).get(0);
//        guiItemStacks.init(1, false, x, y);
//        guiItemStacks.set(1, output1);
//        x += 18;
//        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 1) {
//            List<ItemStack> output2 = ingredients.getOutputs(VanillaTypes.ITEM).get(1);
//            if (!output2.get(0).isEmpty()) {
//                guiItemStacks.init(2, false, x, y);
//                guiItemStacks.set(2, output2);
//            }
//        }
//    }
//}
