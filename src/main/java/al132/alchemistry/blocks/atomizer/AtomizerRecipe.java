package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.Ref;
import al132.alchemistry.misc.ProcessingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class AtomizerRecipe extends ProcessingRecipe {

    public FluidStack input;
    public ItemStack output;

    public AtomizerRecipe(ResourceLocation id, String group, FluidStack input, ItemStack output) {
        super(RecipeTypes.ATOMIZER, id, group, Ingredient.EMPTY, output);
        this.input = input;
        this.output = output;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Ref.ATOMIZER_SERIALIZER;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }
}