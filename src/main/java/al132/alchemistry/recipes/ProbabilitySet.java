package al132.alchemistry.recipes;

import al132.alchemistry.utils.ListUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static al132.alchemistry.utils.StackUtils.areStacksEqualIgnoreQuantity;

public class ProbabilitySet {

    private List<ProbabilityGroup> set;
    public final boolean relativeProbability;
    public final int rolls;

    public ProbabilitySet(List<ProbabilityGroup> set) {
        this(set, true, 1);
    }

    public ProbabilitySet(List<ProbabilityGroup> set, boolean relativeProbability, int rolls) {
        this.set = set;
        this.relativeProbability = relativeProbability;
        this.rolls = rolls;
    }

    public List<ProbabilityGroup> getSet(){
        return set;
    }

    public List<ItemStack> toStackList() {
        ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
        set.forEach(it -> builder.add(ImmutableList.copyOf(it.getOutputs())));
        return builder.build().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public double probabilityAtIndex(int index) {
        double sum = getTotalProbability();
        if (relativeProbability) return set.get(index).getProbability() / sum;
        else return set.get(index).getProbability();
    }

    private double getTotalProbability() {
        return set.stream().mapToDouble(ProbabilityGroup::getProbability).sum();
    }

    public NonNullList<ItemStack> calculateOutput() {
        NonNullList<ItemStack> temp = NonNullList.create();
        Random rando = new Random();
        for (int i = 1; i <= rolls; i++) {
            if (relativeProbability) {
                double totalProbability = getTotalProbability();
                double targetProbability = rando.nextDouble();
                double trackingProbability = 0.0;

                for (ProbabilityGroup component : set) {
                    trackingProbability += (component.getProbability() / totalProbability);
                    if (trackingProbability >= targetProbability) {
                        component.getOutputs().stream().filter(x -> !x.isEmpty()).forEach(x -> {
                            ItemStack stack = x.copy();
                            int index = ListUtils.indexOfFirst(temp, it -> areStacksEqualIgnoreQuantity(stack, it));
                            if (index != -1) temp.get(index).grow(stack.getCount());//stack.count)
                            else temp.add(stack);
                        });
                        break;
                    }
                }
            } else { //absolute probability
                for (ProbabilityGroup component : set) {
                    if (component.getProbability() >= rando.nextInt(101)) {
                        component.getOutputs().stream().filter(x -> !x.isEmpty()).forEach(x -> {
                            ItemStack stack = x.copy();
                            int index = ListUtils.indexOfFirst(temp, it -> areStacksEqualIgnoreQuantity(stack, it));
                            if (index != -1) temp.get(index).grow(stack.getCount());
                            else temp.add(stack);
                        });
                    }
                }
            }
        }
        return temp;
    }

    public static class Builder {
        private List<ProbabilityGroup> groups = new ArrayList<>();

        public Builder() {
        }


        public Builder addGroup(ProbabilityGroup group) {
            groups.add(group);
            return this;
        }

        public Builder addGroup(double probability, ItemStack... stacks) {
            groups.add(new ProbabilityGroup(Lists.newArrayList(stacks), probability));
            return this;
        }

        public ProbabilitySet build() {
            return new ProbabilitySet(this.groups);
        }
    }
}
