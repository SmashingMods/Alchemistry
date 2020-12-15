package al132.alchemistry.blocks.fission;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.blocks.fission.FissionRecipe;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class FissionRegistry {

    private static List<FissionRecipe> recipes = null;

    public static List<FissionRecipe> getRecipes(World world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeTypes.FISSION)
                    .map(x -> (FissionRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}