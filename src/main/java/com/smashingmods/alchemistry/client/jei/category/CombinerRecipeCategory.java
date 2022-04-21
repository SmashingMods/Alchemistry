package com.smashingmods.alchemistry.client.jei.category;

//public class CombinerRecipeCategory implements IRecipeCategory<CombinerRecipe> {
//    private IGuiHelper guiHelper;
//    private final static int u = 35;
//    private final static int v = 10;
//    private final static int OUTPUT_SLOT = 9;
//    private final static int INPUT_SIZE = 9;
//
//    public CombinerRecipeCategory(IGuiHelper guiHelper) {
//        this.guiHelper = guiHelper;
//    }
//
//    @Override
//    public ResourceLocation getUid() {
//        return new ResourceLocation(Alchemistry.MODID, JEIIntegration.COMBINER_CATEGORY);
//    }
//
//    @Override
//    public Class<? extends CombinerRecipe> getRecipeClass() {
//        return CombinerRecipe.class;
//    }
//
//    @Override
//    public Component getTitle() {
//        return new TextComponent(I18n.get("alchemistry.jei.combiner"));
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return guiHelper.createDrawable(new ResourceLocation(Alchemistry.MODID, "textures/gui/combiner_gui.png"),
//                u, v, 159 - u, 85 - v);
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return guiHelper.createDrawableIngredient(new ItemStack(BlockRegistry.COMBINER_BLOCK.get()));
//    }
//
//    @Override
//    public void setIngredients(CombinerRecipe recipe, IIngredients ingredients) {
//        ingredients.setInputs(VanillaTypes.ITEM, recipe.getEmptyStrippedInputs());
//        ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout recipeLayout, CombinerRecipe recipe, IIngredients ingredients) {
//        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//        int startX = 38 - u;
//        int x = startX;
//        int y = 13 - v;
//        int index = 0;
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                guiItemStacks.init(index, true, x, y);
//                index++;
//                x += 18;
//            }
//            x = startX;
//            y += 18;
//        }
//
//
//        for (int i = 0; i < 9; i++) {
//            ItemStack temp = recipe.inputs.get(i);
//            if (!temp.isEmpty()) {
//                guiItemStacks.set(i, temp);//ingredients.getInputs(VanillaTypes.ITEM).get(i));
//            }
//        }
//
//        x = 139 - u;
//        y = 32 - v;
//
//        guiItemStacks.init(OUTPUT_SLOT, false, x, y);
//        //guiItemStacks.set(OUTPUT_SLOT, ingredients.getOutputs(ItemStack::class.java)[0])
//        guiItemStacks.set(OUTPUT_SLOT, recipe.output);
//    }
//}
