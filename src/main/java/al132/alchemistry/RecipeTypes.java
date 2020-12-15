package al132.alchemistry;

import al132.alchemistry.blocks.atomizer.AtomizerRecipe;
import al132.alchemistry.blocks.combiner.CombinerRecipe;
import al132.alchemistry.blocks.dissolver.DissolverRecipe;
import al132.alchemistry.blocks.evaporator.EvaporatorRecipe;
import al132.alchemistry.blocks.fission.FissionRecipe;
import al132.alchemistry.blocks.liquifier.LiquifierRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import static al132.alchemistry.Alchemistry.MODID;


public class RecipeTypes {
    public static IRecipeType<AtomizerRecipe> ATOMIZER = register(MODID + ":atomizer");
    public static IRecipeType<CombinerRecipe> COMBINER = register(MODID + ":combiner");
    public static IRecipeType<DissolverRecipe> DISSOLVER = register(MODID + ":dissolver");
    public static IRecipeType<EvaporatorRecipe> EVAPORATOR = register(MODID + ":evaporator");
    public static IRecipeType<FissionRecipe> FISSION = register(MODID + ":fission");
    public static IRecipeType<LiquifierRecipe> LIQUIFIER = register(MODID + ":liquifier");


    static <T extends IRecipe<?>> IRecipeType<T> register(final String key) {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(key), new IRecipeType<T>() {
            public String toString() {
                return key;
            }
        });
    }
}