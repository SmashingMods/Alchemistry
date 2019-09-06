package al132.alchemistry.recipes;

import net.minecraft.item.ItemStack;

import java.util.List;

public class ProbabilityGroup {

    private List<ItemStack> outputs;
    private double probability;

    public ProbabilityGroup(List<ItemStack> outputs, double probability) {
        this.outputs = outputs;
        this.probability = probability;
    }

    public List<ItemStack> getOutputs() {
        return this.outputs;
    }

    public double getProbability() {
        return this.probability;
    }

}
