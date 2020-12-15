package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.RecipeTypes;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class AtomizerRegistry {

    private static List<AtomizerRecipe> recipes = null;

    public static List<AtomizerRecipe> getRecipes(World world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeTypes.ATOMIZER)
                    .map(x -> (AtomizerRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}
