package al132.alchemistry.blocks.fission;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.Ref;
import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.StackUtils;
import al132.chemlib.chemistry.ElementRegistry;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class FissionRecipe extends ProcessingRecipe {

    public final int input;
    public final int output1;
    public final int output2;

    public FissionRecipe(ResourceLocation id, String group, int input, int output1, int output2) {
        super(RecipeTypes.FISSION, id, group, Ingredient.fromStacks(StackUtils.atomicNumToStack(input)), StackUtils.atomicNumToStack(output1));
        this.input = input;
        this.output1 = output1;
        this.output2 = output2;
    }

    public ItemStack getInput() {
        return StackUtils.atomicNumToStack(input);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList temp = NonNullList.create();
        temp.add(Ingredient.fromStacks(getInput()));
        return temp;
    }

    public List<ItemStack> getOutputs() {
        Item firstOutput = ElementRegistry.elements.get(output1);
        if (output2 == 0) {
            return Lists.newArrayList(new ItemStack(firstOutput, 2));
        } else {
            return Lists.newArrayList(new ItemStack(firstOutput), new ItemStack(ElementRegistry.elements.get(output2)));
        }
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Ref.FISSION_SERIALIZER;
    }
}
