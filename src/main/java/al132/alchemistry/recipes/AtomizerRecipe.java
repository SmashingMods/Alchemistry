package al132.alchemistry.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

public class AtomizerRecipe {

    public FluidStack input;
    public ItemStack output;
    public boolean reversible;

    public AtomizerRecipe(FluidStack input, ItemStack output) {
        this(false, input, output);
    }

    public AtomizerRecipe(boolean reversible, FluidStack input, ItemStack output) {
        this.reversible = reversible;
        this.input = input;
        this.output = output;
    }
}