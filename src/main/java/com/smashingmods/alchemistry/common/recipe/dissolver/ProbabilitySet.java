package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

public class ProbabilitySet {

    private final List<ProbabilityGroup> probabilityGroups;
    private final boolean relativeProbability;
    private final int rolls;

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

    private double getTotalProbability() {
        return probabilityGroups.stream()
                .mapToDouble(ProbabilityGroup::getProbability)
                .sum();
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
                        group.getOutput().stream()
                            .filter(itemStack -> !itemStack.isEmpty())
                            .forEach(itemStack -> {

                                OptionalInt optionalIndex = IntStream.range(0, toReturn.size())
                                        .filter(index -> ItemStack.isSameItemSameTags(itemStack, toReturn.get(index)))
                                        .findFirst();

                                if (optionalIndex.isPresent()) {
                                    toReturn.get(optionalIndex.getAsInt()).grow(itemStack.getCount());
                                } else {
                                    toReturn.add(itemStack);
                                }
                            }
                        );
                        break;
                    }
                }
            } else {
                for (ProbabilityGroup group : probabilityGroups) {
                    if (group.getProbability() >= random.nextInt(101)) {
                        group.getOutput().stream()
                            .filter(itemStack -> !itemStack.isEmpty())
                            .forEach(itemStack -> {

                                OptionalInt optionalIndex = IntStream.range(0, toReturn.size())
                                        .filter(index -> ItemStack.isSameItemSameTags(itemStack, toReturn.get(index)))
                                        .findFirst();

                                if (optionalIndex.isPresent()) {
                                    toReturn.get(optionalIndex.getAsInt()).grow(itemStack.getCount());
                                } else {
                                    toReturn.add(itemStack);
                                }
                            }
                        );
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

        public static Builder createSet() {
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
