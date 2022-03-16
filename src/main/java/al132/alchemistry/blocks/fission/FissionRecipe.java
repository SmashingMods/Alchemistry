package al132.alchemistry.blocks.fission;

import al132.alchemistry.Registration;
import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.StackUtils;
import al132.chemlib.chemistry.ElementRegistry;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;


public class FissionRecipe extends ProcessingRecipe {

    public final int input;
    public final int output1;
    public final int output2;

    public FissionRecipe(ResourceLocation id, String group, int input, int output1, int output2) {
        super(Registration.FISSION_TYPE, id, group, Ingredient.of(StackUtils.atomicNumToStack(input)), StackUtils.atomicNumToStack(output1));
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
        temp.add(Ingredient.of(getInput()));
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
    public RecipeSerializer<?> getSerializer() {
        return Registration.FISSION_SERIALIZER.get();
    }
}
