package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.blocks.liquifier.LiquifierRecipe;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class LiquifierRegistry {

    private static List<LiquifierRecipe> recipes = null;

    public static List<LiquifierRecipe> getRecipes(World world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeTypes.LIQUIFIER)
                    .map(x -> (LiquifierRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}