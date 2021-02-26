package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.RecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DissolverRegistry {

    private static List<DissolverRecipe> recipes = null;
    private static HashMap<ItemStack, DissolverRecipe> cache = new HashMap<>();

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
        DissolverRecipe cachedRecipe = cache.getOrDefault(input, null);
        if (cachedRecipe != null) return cachedRecipe;

        for (DissolverRecipe recipe : getRecipes(world)) {
            if (recipe.inputIngredient != null) {
                for (ItemStack recipeStack : recipe.inputIngredient.ingredient.getMatchingStacks().clone()) {
                    if (ItemStack.areItemsEqual(recipeStack, input)) {
                        // && (input.itemDamage == recipeStack.itemDamage
                        //|| recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                        if (quantitySensitive && input.getCount() >= recipeStack.getCount() || !quantitySensitive) {
                            cachedRecipe = recipe.copy();
                            cache.put(input, cachedRecipe);
                            return cachedRecipe;
                        }
                    }
                }
            } else {
                //TODO handle borked recipes
            }
        }
        return null;
    }
}