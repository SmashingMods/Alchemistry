package al132.alchemistry.blocks.combiner;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.Ref;
import al132.alchemistry.utils.IItemHandlerUtils;
import al132.alchemistry.utils.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.stream.Collectors;

public class CombinerRegistry {

    private static List<CombinerRecipe> recipes = null;

    public static List<CombinerRecipe> getRecipes(World world) {
        if (recipes == null) {
            recipes = world.getRecipeManager().getRecipes().stream()
                    .filter(x -> x.getType() == RecipeTypes.COMBINER)
                    .map(x -> (CombinerRecipe) x)
                    .collect(Collectors.toList());
        }
        return recipes;
    }

    public static CombinerRecipe matchOutput(World world, ItemStack stack) {
        return getRecipes(world).stream()
                .filter(it -> it.output.getItem() == stack.getItem())
                .filter(it -> ItemStack.areItemStacksEqual(it.output, stack))
                .findFirst()
                .orElse(null);
    }


    public static CombinerRecipe matchInputs(World world, IItemHandler handler) {
        return matchInputs(world, IItemHandlerUtils.toStackList(handler));//.toStackList())
    }

    private static CombinerRecipe matchInputs(World world, List<ItemStack> inputStacks) {
        outer:
        for (CombinerRecipe recipe : CombinerRegistry.getRecipes(world)) {
            int matchingStacks = 0;
            inner:
            for (int index = 0; index < recipe.inputs.size(); index++) {
                //inner: for ((index: Int, recipeStack: ItemStack) in recipe.inputs.withIndex()) {
                ItemStack recipeStack = recipe.inputs.get(index);
                ItemStack inputStack = inputStacks.get(index);
                if ((inputStack.getItem() == Ref.slotFiller || inputStack.isEmpty()) && recipeStack.isEmpty()) {
                    continue inner;
                } else if (!(StackUtils.areStacksEqualIgnoreQuantity(inputStack, recipeStack)
                        && inputStack.getCount() >= recipeStack.getCount())) {
                    // && (inputStack.get == recipeStack.itemDamage || recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE))) {
                    continue outer;
                } else if (inputStack.isEmpty() || recipeStack.isEmpty()) {
                    continue outer;
                }
            }
            return recipe;//.copy()
        }
        return null;
    }


}