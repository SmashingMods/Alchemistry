package al132.alchemistry.recipes;

import al132.chemlib.chemistry.ElementRegistry;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class FissionRecipe {

    public final int input;
    public final int output1;
    public final int output2;

    public FissionRecipe(int input, int output1, int output2) {
        this.input = input;
        this.output1 = output1;
        this.output2 = output2;
    }

    public ItemStack getInput() {
        return new ItemStack(ElementRegistry.elements.get(input));
    }

    public List<ItemStack> getOutputs() {
        Item firstOutput = ElementRegistry.elements.get(output1);
        if (output2 == 0) {
            return Lists.newArrayList(new ItemStack(firstOutput, 2));
        } else {
            return Lists.newArrayList(new ItemStack(firstOutput), new ItemStack(ElementRegistry.elements.get(output2)));
        }
    }
}
