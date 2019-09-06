package al132.alchemistry.recipes;

import al132.alchemistry.Ref;
import al132.alchemistry.utils.IItemHandlerUtils;
import al132.alchemistry.utils.StackUtils;
import al132.alib.tiles.CustomStackHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CombinerRecipe {
    public final ItemStack output;
    public final List<ItemStack> inputs = new ArrayList<>();
    public final Set<Integer> nonEmptyIndices = new HashSet<>();

    //String gamestage;

    public CombinerRecipe(ItemStack output, List<Object> inObjs) {
        this.output = output;
        for (int i = 0; i < 9; i++) {
            Object temp;
            if (inObjs.size() > i) temp = inObjs.get(i);
            else temp = null;
            if (temp instanceof ItemStack) {
                inputs.add(((ItemStack) temp).copy());
            } else if (temp instanceof Item) {
                inputs.add(new ItemStack((Item) temp));
            } else if (temp instanceof Block) {
                inputs.add(new ItemStack((Block) temp));
            } else {
                inputs.add(ItemStack.EMPTY);
            }
            if (!inputs.get(i).isEmpty()) nonEmptyIndices.add(i);
        }
        assert inputs.size() == 9;
    }

    public List<ItemStack> getEmptyStrippedInputs() {
        return inputs.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    }


    public static CombinerRecipe matchInputs(IItemHandler handler) {
        return matchInputs(IItemHandlerUtils.toStackList(handler));//.toStackList())
    }

    private static CombinerRecipe matchInputs(List<ItemStack> inputStacks) {
        outer:
        for (CombinerRecipe recipe : ModRecipes.combinerRecipes) {
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

    public boolean matchesHandlerStacks(CustomStackHandler handler) {
        int matchingStacks = 0;

        for (int index = 0; index < this.inputs.size(); index++) {
            ItemStack recipeStack = this.inputs.get(index);
            ItemStack handlerStack = handler.getStackInSlot(index);
            if ((handlerStack.getItem() == Ref.slotFiller || handlerStack.isEmpty()) && recipeStack.isEmpty()) {
                matchingStacks++;
            } else if (handlerStack.isEmpty() || recipeStack.isEmpty()) continue;
            else if (StackUtils.areStacksEqualIgnoreQuantity(handlerStack, recipeStack)
                    && handlerStack.getCount() >= recipeStack.getCount()) {
                //&& (handlerStack.itemDamage == recipeStack.itemDamage || recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)) {
                matchingStacks++;
            }
        }
        return matchingStacks == 9;
    }

    public static CombinerRecipe matchOutput(ItemStack stack) {
        return ModRecipes.combinerRecipes.stream()
                .filter(it -> it.output.getItem() == stack.getItem())
                .filter(it -> ItemStack.areItemStacksEqual(it.output, stack))
                .findFirst()
                .orElse(null);
    }
}
