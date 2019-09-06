package al132.alchemistry.recipes;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatorRecipe {

    public FluidStack input;
    public ItemStack output;

    public EvaporatorRecipe(FluidStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public EvaporatorRecipe(Fluid fluid, int fluidQuantity, ItemStack output) {
        this(new FluidStack(fluid, fluidQuantity), output);
    }
}