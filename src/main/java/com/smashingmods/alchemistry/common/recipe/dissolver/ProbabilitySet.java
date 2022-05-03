package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        JsonObject toReturn = new JsonObject();
        JsonArray jsonArray = new JsonArray();

        toReturn.add("rolls", new JsonPrimitive(rolls));
        toReturn.add("relativeProbability", new JsonPrimitive(relativeProbability));

        for (ProbabilityGroup group : probabilityGroups) {
            jsonArray.add(group.serialize());
        }
        toReturn.add("groups", jsonArray);

        return toReturn;
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(probabilityGroups.size());
        pBuffer.writeInt(rolls);
        pBuffer.writeBoolean(relativeProbability);
        for (ProbabilityGroup group : probabilityGroups) {
            group.write(pBuffer);
        }
    }

    public static ProbabilitySet read(FriendlyByteBuf pbuffer) {
        List<ProbabilityGroup> groupArrayList = new ArrayList<>();
        int size = pbuffer.readInt();
        int rolls = pbuffer.readInt();
        boolean relative = pbuffer.readBoolean();

        for (int index = 0; index < size; index++) {
            groupArrayList.add(ProbabilityGroup.read(pbuffer));
        }
        return new ProbabilitySet(groupArrayList, relative, rolls);
    }

    public List<ProbabilityGroup> getProbabilityGroups() {
        return probabilityGroups;
    }

    public List<ItemStack> getAsList() {
        ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
        probabilityGroups.forEach(group -> builder.add(ImmutableList.copyOf(group.getOutput())));
        return builder.build().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<ItemStack> filterEmpty() {
        return getAsList().stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
    }

    public double getProbability(int pIndex) {
        if (relativeProbability) {
            double sum = getTotalProbability();
            return probabilityGroups.get(pIndex).getProbability() / sum;
        } else {
            return probabilityGroups.get(pIndex).getProbability();
        }
    }

    private double getTotalProbability() {
        return probabilityGroups.stream().mapToDouble(ProbabilityGroup::getProbability).sum();
    }

    public NonNullList<ItemStack> calculateOutput() {
        NonNullList<ItemStack> toReturn = NonNullList.create();
        Random random = new Random();

        for (int i = 1; i <= rolls; i++) {
            if (relativeProbability) {

                double totalProbability = getTotalProbability();
                double targetProbability = random.nextDouble();
                double trackingProbability = 0.0;

                for (ProbabilityGroup group : probabilityGroups) {
                    trackingProbability += (group.getProbability() / totalProbability);

                    if (trackingProbability >= targetProbability) {

                        group.getOutput().stream().filter(itemStack -> !itemStack.isEmpty()).forEach(itemStack -> {

                            int stackIndex = IntStream.range(0, toReturn.size())
                                    .filter(index -> ItemStack.isSameItemSameTags(itemStack, toReturn.get(index)))
                                    .findFirst().orElse(-1);

                            if (stackIndex != -1) {
                                toReturn.get(stackIndex).grow(itemStack.getCount());
                            } else {
                                toReturn.add(itemStack);
                            }
                        });
                        break;
                    }
                }
            } else {
                for (ProbabilityGroup component : probabilityGroups) {
                    if (component.getProbability() >= random.nextInt(101)) {

                        component.getOutput().stream().filter(itemStack -> !itemStack.isEmpty()).forEach(itemStack -> {

                            int index = IntStream.range(0, toReturn.size())
                                    .filter(it -> ItemStack.isSameItemSameTags(itemStack, toReturn.get(it)))
                                    .findFirst().orElse(-1);

                            if (index != -1) {
                                toReturn.get(index).grow(itemStack.getCount());
                            } else {
                                toReturn.add(itemStack);
                            }
                        });
                    }
                }
            }
        }
        return toReturn;
    }

    public static class Builder {
        private final List<ProbabilityGroup> groups = new ArrayList<>();
        private boolean relativeProbability = true;
        private int rolls = 1;

        public Builder() {}

        public static Builder set() {
            return new Builder();
        }

        public Builder addGroup(ProbabilityGroup pGroup) {
            groups.add(pGroup);
            return this;
        }

        public Builder addGroup(ItemStack... pItemStacks) {
            groups.add(new ProbabilityGroup(Arrays.asList(pItemStacks), 1));
            return this;
        }

        public Builder addGroup(double pProbability, ItemStack... pItemStacks) {
            groups.add(new ProbabilityGroup(Arrays.asList(pItemStacks), pProbability));
            return this;
        }

        public Builder rolls(int rolls) {
            this.rolls = rolls;
            return this;
        }

        public Builder relative(boolean pRelativeProbability) {
            this.relativeProbability = pRelativeProbability;
            return this;
        }

        public ProbabilitySet build() {
            return new ProbabilitySet(groups, relativeProbability, rolls);
        }
    }
}
