package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.Alchemistry;
import al132.alchemistry.RecipeTypes;
import al132.alchemistry.Ref;
import al132.alchemistry.misc.ModRecipes;
import al132.alchemistry.misc.ProbabilitySet;
import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.IngredientStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagRegistry;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;


public class DissolverRecipe extends ProcessingRecipe {

    public IngredientStack inputIngredient;
    public boolean reversible = false;
    public ProbabilitySet outputs;

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public String toString(){
        return "input=" + inputIngredient.toString() + "\treversible=" + reversible + "\toutputs=" + outputs.toString();
    }
    //private NonNullList<ItemStack> inputs = null;
/*
    public Ingredient getInput() {
        if (inputIngredient == null) {
            //System.out.println(inputTagName);
            inputIngredient = Ingredient.fromTag(TagUtils.tag(inputTagName));//Ingredient.fromTag(ItemTags.createOptional(new ResourceLocation(inputTagName)));
        }
        return inputIngredient;
    }

 */
/*
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
*/

    public DissolverRecipe(ResourceLocation id, String group, IngredientStack input, ProbabilitySet outputs) {
        super(RecipeTypes.DISSOLVER, id, group, input.ingredient, ItemStack.EMPTY);
        this.inputIngredient = input;
        this.outputs = outputs;
    }

    public DissolverRecipe copy() {
        return new DissolverRecipe(this.id, this.group, this.inputIngredient, this.outputs);
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


    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Ref.DISSOLVER_SERIALIZER;
    }

    /*
    public static class Builder {
        private Ingredient inputIngredient;
        private ProbabilitySet outputs;
        private boolean reversible = false;

        public Builder outputs(ProbabilitySet set) {
            this.outputs = set;
            return this;
        }

        public Builder setReversible(boolean value) {
            this.reversible = value;
            return this;
        }

        public Builder input(String input) {

            this.inputIngredient = Ingredient.fromTag(ItemTags.createOptional(new ResourceLocation(input)));
            return this;
        }

        public Builder input(ItemStack input) {
            this.inputIngredient = Ingredient.fromStacks(input);
            return this;
        }

        public Builder input(Item input) {
            this.inputIngredient = Ingredient.fromItems(input);
            return this;
        }

        public Builder input(Ingredient input) {
            this.inputIngredient = input;
            return this;
        }

        public DissolverRecipe build() {
            String ins = inputIngredient == null ? "null" : inputIngredient.toString();
            String outs = outputs == null ? "null" : outputs.toString();
            if (this.inputIngredient != null && this.outputs != null) {
                return new DissolverRecipe(this);//DissolverRegistry.dissolverRecipes.add(new DissolverRecipe(this));
            } else Alchemistry.LOGGER.warn("Invalid dissolver recipe - input[" + ins + "], outputs[" + outs + "]");
            throw new RuntimeException("Invalid recipe");
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
    }*/
}