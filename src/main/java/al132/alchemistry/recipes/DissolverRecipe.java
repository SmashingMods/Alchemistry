package al132.alchemistry.recipes;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.utils.TagUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;


public class DissolverRecipe {

    private String inputTagName;
    private Ingredient inputIngredient;
    public boolean reversible = false;
    public ProbabilitySet outputs;
    //private NonNullList<ItemStack> inputs = null;

    public Ingredient getInput() {
        if (inputIngredient == null) {
            //System.out.println(inputTagName);
            inputIngredient = Ingredient.fromTag(TagUtils.tag(inputTagName));//Ingredient.fromTag(ItemTags.createOptional(new ResourceLocation(inputTagName)));
        }
        return inputIngredient;
    }

    public DissolverRecipe(BuilderFromIngredient builder) {
        this(builder.inputIngredient, builder.outputs);
        this.reversible = builder.reversible;
    }

    public DissolverRecipe(BuilderFromTag builder) {
        this(builder.inputTagName, builder.outputs);
        this.reversible = builder.reversible;
    }

    public DissolverRecipe(String inputTagName, ProbabilitySet outputs) {
        this.inputTagName = inputTagName;
        this.outputs = outputs;
    }

    public DissolverRecipe(Ingredient input, ProbabilitySet outputs) {
        this.inputIngredient = input;
        this.outputs = outputs;
    }

    public DissolverRecipe copy() {
        return new DissolverRecipe(this.inputIngredient, this.outputs);
    }
/*
    private void initInputs() {
        inputs = NonNullList.create();
        if (input != null) inputs.addAll(Lists.newArrayList(input.getMatchingStacks().clone()));
    }

    public NonNullList<ItemStack> getInputs() {
        if(this.inputs == null) initInputs();
        return this.inputs;
    }*/

    @Nullable
    public static DissolverRecipe match(ItemStack input, boolean quantitySensitive) {
        for (DissolverRecipe recipe : ModRecipes.dissolverRecipes) {
            if (recipe.inputIngredient != null) {
                for (ItemStack recipeStack : recipe.inputIngredient.getMatchingStacks().clone()) {
                    if (ItemStack.areItemsEqual(recipeStack, input)) {
                        // && (input.itemDamage == recipeStack.itemDamage
                        //|| recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                        if (quantitySensitive && input.getCount() >= recipeStack.getCount()) return recipe.copy();
                        else if (!quantitySensitive) return recipe.copy();
                    }
                }
            } else {
                //TODO handle borked recipes
            }
        }
        return null;
    }

    public static class BuilderFromIngredient {
        private Ingredient inputIngredient;
        private ProbabilitySet outputs;
        private boolean reversible = false;

        public BuilderFromIngredient outputs(ProbabilitySet set) {
            this.outputs = set;
            return this;
        }

        public BuilderFromIngredient setReversible(boolean value) {
            this.reversible = value;
            return this;
        }

        public BuilderFromIngredient input(ItemStack input) {
            this.inputIngredient = Ingredient.fromStacks(input);
            return this;
        }

        public BuilderFromIngredient input(Item input) {
            this.inputIngredient = Ingredient.fromItems(input);
            return this;
        }

        public BuilderFromIngredient input(Ingredient input) {
            this.inputIngredient = input;
            return this;
        }

        public void build() {
            String ins = inputIngredient == null ? "null" : inputIngredient.toString();
            String outs = outputs == null ? "null" : outputs.toString();
            if (this.inputIngredient != null && this.outputs != null) {
                ModRecipes.dissolverRecipes.add(new DissolverRecipe(this));
            } else Alchemistry.LOGGER.warn("Invalid dissolver recipe - input[" + ins + "], outputs[" + outs + "]");
        }
    }

    public static class BuilderFromTag {
        private String inputTagName;
        private ProbabilitySet outputs;
        private boolean reversible = false;

        public BuilderFromTag() {
        }

        public BuilderFromTag outputs(ProbabilitySet set) {
            this.outputs = set;
            return this;
        }

        public BuilderFromTag setReversible(boolean value) {
            this.reversible = value;
            return this;
        }

        public BuilderFromTag input(String tagLocation) {
            this.inputTagName = tagLocation;
            //this.input = Ingredient.fromTag(ItemTags.getCollection().getOrCreate(new ResourceLocation(tagLocation)));
            //this.input = Ingredient.fromTag(new ItemTags.Wrapper(new ResourceLocation(tagLocation)));
            return this;
        }

        public void build() {
            String ins = inputTagName == null ? "null" : inputTagName.toString();
            String outs = outputs == null ? "null" : outputs.toString();
            if (this.inputTagName != null && this.outputs != null) {
                ModRecipes.dissolverRecipes.add(new DissolverRecipe(this));
            } else Alchemistry.LOGGER.warn("Invalid dissolver recipe - input[" + ins + "], outputs[" + outs + "]");
        }
    }
}