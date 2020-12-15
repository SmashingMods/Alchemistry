package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.RecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class DissolverRegistry {

    private static List<DissolverRecipe> recipes = null;

    public static List<DissolverRecipe> getRecipes(World world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeTypes.DISSOLVER)
                    .map(x -> (DissolverRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }

    @Nullable
    public static DissolverRecipe match(World world,ItemStack input, boolean quantitySensitive) {
        for (DissolverRecipe recipe : getRecipes(world)) {
            if (recipe.inputIngredient != null) {
                for (ItemStack recipeStack : recipe.inputIngredient.ingredient.getMatchingStacks().clone()) {
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
}