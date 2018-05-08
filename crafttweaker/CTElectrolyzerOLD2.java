package al132.alchemistry.compat.crafttweaker;

/**
 * Created by al132 on 5/26/2017.
 */

import al132.alchemistry.compat.crafttweaker.utils.InputHelper;
import al132.alchemistry.compat.jei.AlchemistryPlugin;
import al132.alchemistry.compat.jei.AlchemistryRecipeUID;
import al132.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeWrapper;
import al132.alchemistry.recipes.ElectrolyzerRecipe;
import al132.alchemistry.recipes.ModRecipes;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al132 on 4/28/2017.
 */

@ZenClass("mods.alchemistry.Electrolyzer")
public class CTElectrolyzerOLD2 {
    final String name = "Alchemistry Electrolyzer";
    static String UID = AlchemistryRecipeUID.INSTANCE.getELECTROLYZER();

    @ZenMethod
    public static void addRecipe(ILiquidStack input, IItemStack stack1, IItemStack stack2, int consumptionProbability,
                                 IItemStack electrolyte) {
       /* MineTweakerAPI.apply(new Add(new ElectrolyzerRecipe(
                InputHelper.toFluid(input),
                Lists.newArrayList(InputHelper.toStack(electrolyte),
                        consumptionProbability,
                        InputHelper.toStack(stack1),
                        InputHelper.toStack(stack2)))));*/
    }

    @ZenMethod
    public static void remove(ILiquidStack input) {
        MineTweakerAPI.apply(new Remove(InputHelper.toFluid(input)));
    }

    private class Add implements IUndoableAction {

        private ElectrolyzerRecipe recipe;

        public Add(ElectrolyzerRecipe recipe) {
            this.recipe = recipe;

        }

        @Override
        public void apply() {
            ModRecipes.INSTANCE.getElectrolyzerRecipes().add(recipe);
            // MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe)
            AlchemistryPlugin.recipeRegistry.addRecipe(new ElectrolyzerRecipeWrapper(recipe), UID);

        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            AlchemistryPlugin.recipeRegistry.removeRecipe(new ElectrolyzerRecipeWrapper(recipe), UID);
            // MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe)
            ModRecipes.INSTANCE.getElectrolyzerRecipes().remove(recipe);
        }

        @Override
        public String describe() {
            return "Adding";
        }

        @Override
        public String describeUndo() {
            return "Undoing Adding"; //TODO
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    private static class Remove implements IUndoableAction {

        private FluidStack input;
        private List<ElectrolyzerRecipe> removedRecipes = new ArrayList<ElectrolyzerRecipe>();

        public Remove(FluidStack input) {
            this.input = input;
            System.out.println(input.amount + input.getFluid().getName());
        }


        @Override
        public void apply() {
            ModRecipes.INSTANCE.getElectrolyzerRecipes().stream()
                    .filter(it -> it.getInput().isFluidEqual(input))
                    .forEach(recipe -> {
                                System.out.println("Removing");
                                System.out.println(UID);
                                System.out.println(AlchemistryPlugin.recipeRegistry.getRecipeWrapper(recipe, UID));
                                MineTweakerAPI.ijeiRecipeRegistry.removeRecipe(AlchemistryPlugin.recipeRegistry.getRecipeWrapper(recipe, UID));
                                removedRecipes.add(recipe);
                                ModRecipes.INSTANCE.getElectrolyzerRecipes().remove(recipe);
                            }
                    );
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            removedRecipes.forEach(recipe -> {
                        System.out.println("UNDO REMOVAL");
                        ModRecipes.INSTANCE.getElectrolyzerRecipes().add(recipe);
                        AlchemistryPlugin.recipeRegistry.addRecipe(new ElectrolyzerRecipeWrapper(recipe), UID);
                    }
            );
        }

        @Override
        public String describe() {
            return "Removing";
        }

        @Override
        public String describeUndo() {
            return "Undoing removal"; //TODO
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}