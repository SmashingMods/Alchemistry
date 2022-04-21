package com.smashingmods.alchemistry.common.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class ProbabilitySet {

    private final List<ProbabilityGroup> probabilityGroups;
    public final boolean relativeProbability;
    public final int rolls;

    public ProbabilitySet(List<ProbabilityGroup> pProbabilityGroups) {
        this(pProbabilityGroups, true, 1);
    }

    public ProbabilitySet(List<ProbabilityGroup> pProbabilityGroups, boolean pRelativeProbability, int pRolls) {
        this.probabilityGroups = pProbabilityGroups;
        this.relativeProbability = pRelativeProbability;
        this.rolls = pRolls;
    }

    public JsonElement serialize() {
        JsonObject temp = new JsonObject();
        temp.add("rolls", new JsonPrimitive(rolls));
        temp.add("relativeProbability", new JsonPrimitive(relativeProbability));
        JsonArray setGroups = new JsonArray();
        for (ProbabilityGroup group : probabilityGroups) {
            setGroups.add(group.serialize());
        }
        temp.add("groups", setGroups);
        return temp;
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(probabilityGroups.size());
        for (ProbabilityGroup group : probabilityGroups) {
            group.write(pBuffer);
        }
        pBuffer.writeBoolean(relativeProbability);
        pBuffer.writeInt(rolls);
    }

    public static ProbabilitySet read(FriendlyByteBuf pbuffer) {
        List<ProbabilityGroup> set = Lists.newArrayList();
        int size = pbuffer.readInt();
        for (int i = 0; i < size; i++) {
            set.add(ProbabilityGroup.read(pbuffer));
        }
        boolean relativeProbability = pbuffer.readBoolean();
        int rolls = pbuffer.readInt();
        return new ProbabilitySet(set, relativeProbability, rolls);
    }

    public List<ProbabilityGroup> getProbabilityGroups() {
        return probabilityGroups;
    }

    public List<ItemStack> toStackList() {
        ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
        probabilityGroups.forEach(it -> builder.add(ImmutableList.copyOf(it.getOutputs())));//.stream().map(x->x.orElse(null)).collect(Collectors.toList()))));
        return builder.build().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<ItemStack> filterNonEmpty() {
        return toStackList().stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    }

    public double probabilityAtIndex(int pIndex) {
        double sum = getTotalProbability();
        if (relativeProbability) return probabilityGroups.get(pIndex).getProbability() / sum;
        else return probabilityGroups.get(pIndex).getProbability();
    }

    private double getTotalProbability() {
        return probabilityGroups.stream().mapToDouble(ProbabilityGroup::getProbability).sum();
    }

    public NonNullList<ItemStack> calculateOutput() {
        NonNullList<ItemStack> temp = NonNullList.create();
        Random rando = new Random();
        for (int i = 1; i <= rolls; i++) {
            if (relativeProbability) {
                double totalProbability = getTotalProbability();
                double targetProbability = rando.nextDouble();
                double trackingProbability = 0.0;

                for (ProbabilityGroup component : probabilityGroups) {
                    trackingProbability += (component.getProbability() / totalProbability);
                    if (trackingProbability >= targetProbability) {
                        component.getOutputs().stream().filter(x -> !x.isEmpty()).forEach(x -> {
                            ItemStack stack = x.copy();

                            int index = IntStream.range(0, temp.size())
                                    .filter(it -> ItemStack.isSameItemSameTags(stack, temp.get(it)))
                                    .findFirst().orElse(-1);

                            if (index != -1) temp.get(index).grow(stack.getCount());
                            else temp.add(stack);
                        });
                        break;
                    }
                }
            } else { //absolute probability
                for (ProbabilityGroup component : probabilityGroups) {
                    if (component.getProbability() >= rando.nextInt(101)) {
                        component.getOutputs().stream().filter(x -> !x.isEmpty()).forEach(x -> {
                            ItemStack stack = x.copy();

                            int index = IntStream.range(0, temp.size())
                                    .filter(it -> ItemStack.isSameItemSameTags(stack, temp.get(it)))
                                    .findFirst().orElse(-1);

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
        private final List<ProbabilityGroup> groups = new ArrayList<>();
        private boolean relativeProbability = true;
        private int rolls = 1;

        public Builder() {}

        public Builder addGroup(ProbabilityGroup group) {
            groups.add(group);
            return this;
        }

        public Builder rolls(int rolls) {
            this.rolls = rolls;
            return this;
        }

        public Builder relative(boolean relativeProbability) {
            this.relativeProbability = relativeProbability;
            return this;
        }

        public Builder addGroup(double probability, ItemStack... stacks) {
            groups.add(new ProbabilityGroup(Lists.newArrayList(stacks), probability));
            return this;
        }

        /*
        public Builder addGroup(double probability, LazyOptional<Item>... items) {
            return addGroup(probability, Arrays.stream(items).map(x->x.orElse(null)).map(ItemStack::new).toArray(ItemStack[]::new));
        }
*/
        public ProbabilitySet build() {
            return new ProbabilitySet(this.groups, relativeProbability, rolls);
        }
    }
}
