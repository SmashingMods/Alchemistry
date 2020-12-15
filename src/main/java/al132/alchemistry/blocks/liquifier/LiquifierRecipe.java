package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.Ref;
import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.IngredientStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;


public class LiquifierRecipe extends ProcessingRecipe {

    public IngredientStack input;
    public FluidStack output;

    public LiquifierRecipe(ResourceLocation id, String group, IngredientStack input, FluidStack output) {
        super(RecipeTypes.LIQUIFIER, id, group, input.ingredient, ItemStack.EMPTY);
        this.input = input;
        this.output = output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Ref.LIQUIFIER_SERIALIZER;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList temp = NonNullList.create();
        temp.add(input.ingredient);
        return temp;
    }
}