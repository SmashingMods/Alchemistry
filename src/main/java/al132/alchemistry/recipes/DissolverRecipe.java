package al132.alchemistry.recipes;

import al132.alchemistry.Alchemistry;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;


public class DissolverRecipe {

    public Ingredient input;
    public boolean reversible = false;
    public ProbabilitySet outputs;
    public NonNullList<ItemStack> inputs;

    public DissolverRecipe(Ingredient input, ProbabilitySet outputs) {
        this.input = input;
        this.outputs = outputs;
        inputs = NonNullList.create();
        if (input != null) inputs.addAll(Lists.newArrayList(input.getMatchingStacks().clone()));
    }

    public void setReversible() {
        this.reversible = true;
    }

    public DissolverRecipe copy() {
        return new DissolverRecipe(this.input, this.outputs);
    }


    @Nullable
    public static DissolverRecipe match(ItemStack input, boolean quantitySensitive) {
        for (DissolverRecipe recipe : ModRecipes.dissolverRecipes) {
            for (ItemStack recipeStack : recipe.inputs) {
                if (ItemStack.areItemsEqual(recipeStack, input)) {
                    // && (input.itemDamage == recipeStack.itemDamage
                    //|| recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                    if (quantitySensitive && input.getCount() >= recipeStack.getCount()) return recipe.copy();
                    else if (!quantitySensitive) return recipe.copy();
                }
            }
        }
        return null;
    }

    public static class Builder {
        private Ingredient input;
        private ProbabilitySet outputs;


        public Builder() {
        }

        public Builder outputs(ProbabilitySet set) {
            this.outputs = set;
            return this;
        }

        public Builder input(ItemStack input) {
            this.input = Ingredient.fromStacks(input);
            return this;
        }

        public Builder input(Item input) {
            this.input = Ingredient.fromItems(input);
            return this;
        }

        public void build() {
            if (this.input != null && this.outputs != null) {
                ModRecipes.dissolverRecipes.add(new DissolverRecipe(this.input, this.outputs));
            } else Alchemistry.LOGGER.warn("Invalid dissolver recipe");
        }
    }
}