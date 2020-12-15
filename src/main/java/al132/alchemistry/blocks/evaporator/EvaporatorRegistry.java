package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.blocks.evaporator.EvaporatorRecipe;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class EvaporatorRegistry {

    private static List<EvaporatorRecipe> recipes = null;

    public static List<EvaporatorRecipe> getRecipes(World world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeTypes.EVAPORATOR)
                    .map(x -> (EvaporatorRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }
}