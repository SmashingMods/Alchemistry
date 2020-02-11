package al132.alchemistry.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class AtomizerRecipe{//} extends AlchemistryRecipe {

    //public static final IRecipeSerializer<AtomizerRecipe> ATOMIZER_SERIALIZER = new SingleItemRecipe.Serializer<AtomizerRecipe>(AtomizerRecipe::new){};

    public FluidStack input;
    public ItemStack output;
    public boolean reversible;

    public AtomizerRecipe(FluidStack input, ItemStack output) {
        this(false, input, output);
    }

    public AtomizerRecipe(boolean reversible, FluidStack input, ItemStack output) {
        //super(null,null,null,null,null,null);
        this.reversible = reversible;
        this.input = input;
        this.output = output;
    }
/*
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }*/
}