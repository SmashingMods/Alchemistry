package al132.alchemistry.recipes;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;


public class LiquifierRecipe {

    public ItemStack input;
    public FluidStack output;

    public LiquifierRecipe(ItemStack input, FluidStack output) {
        this.input = input;
        this.output = output;
    }

    public LiquifierRecipe(ItemStack input, Fluid fluid, int fluidQuantity) {
        this(input, new FluidStack(fluid, fluidQuantity));
    }
}