package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProbabilitySet {

    public static final Codec<ProbabilitySet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ProbabilityGroup.CODEC.listOf().fieldOf("groups").forGetter(ProbabilitySet::getProbabilityGroups),
            Codec.BOOL.fieldOf("weighted").forGetter(ProbabilitySet::isWeighted),
            Codec.INT.fieldOf("rolls").forGetter(ProbabilitySet::getRolls)
    ).apply(instance, ProbabilitySet::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProbabilitySet> STREAM_CODEC = StreamCodec.composite(
            ProbabilityGroup.STREAM_CODEC.apply(ByteBufCodecs.list()), ProbabilitySet::getProbabilityGroups,
            ByteBufCodecs.BOOL, ProbabilitySet::isWeighted,
            ByteBufCodecs.VAR_INT, ProbabilitySet::getRolls,
            ProbabilitySet::new
    );

    private final List<ProbabilityGroup> probabilityGroups;
    private final boolean weighted;
    private final int rolls;

    @SuppressWarnings("unused")
    public ProbabilitySet(List<ProbabilityGroup> pProbabilityGroups) {
        this(pProbabilityGroups, true, 1);
    }

    public ProbabilitySet(List<ProbabilityGroup> pProbabilityGroups, boolean pWeighted, int pRolls) {
        this.probabilityGroups = pProbabilityGroups;
        this.weighted = pWeighted;
        this.rolls = pRolls;
    }

    public NonNullList<ItemStack> calculateOutput() {
        NonNullList<ItemStack> toReturn = NonNullList.create();
        Random random = new Random();

        for (int i = 1; i <= rolls; i++) {
            double totalProbability = getTotalProbability();
            double targetProbability = random.nextDouble();

            if (weighted) {
                double outputProbability = 0.0;
                for (ProbabilityGroup group : probabilityGroups) {
                    outputProbability += (group.getProbability() / totalProbability);
                    if (outputProbability >= targetProbability) {
                        toReturn.addAll(group.getOutput());
                        break;
                    }
                }
            } else {
                if ((totalProbability / 100) < targetProbability) return toReturn;
                for (ProbabilityGroup group : probabilityGroups) {
                    if (group.getProbability() >= random.nextInt(101)) {
                        toReturn.addAll(group.getOutput());
                    }
                }
            }
        }
        return toReturn;
    }

    public List<ProbabilityGroup> getProbabilityGroups() { return probabilityGroups; }
    public boolean isWeighted() { return weighted; }
    public int getRolls() { return rolls; }

    private double getTotalProbability() {
        return getTotalProbability(probabilityGroups);
    }

    private static double getTotalProbability(List<ProbabilityGroup> pGroups) {
        return pGroups.stream().mapToDouble(ProbabilityGroup::getProbability).sum();
    }

    public ProbabilitySet copy() {
        return new ProbabilitySet(List.copyOf(probabilityGroups), weighted, rolls);
    }

    public static class Builder {
        private final List<ProbabilityGroup> groups = new ArrayList<>();
        private boolean weighted = false;
        private int rolls = 1;

        public Builder() {}

        public static Builder createSet() {
            return new Builder();
        }

        @SuppressWarnings("unused")
        public Builder addGroup(ProbabilityGroup pGroup) {
            groups.add(pGroup);
            return this;
        }

        public Builder addGroup(List<ItemStack> itemStacks) {
            groups.add(new ProbabilityGroup(itemStacks));
            return this;
        }

        public Builder addGroup(List<ItemStack> itemStacks, double pProbability) {
            groups.add(new ProbabilityGroup(itemStacks, pProbability));
            return this;
        }

        public Builder addGroup(ItemStack... pItemStacks) {
            groups.add(new ProbabilityGroup(Arrays.asList(pItemStacks)));
            return this;
        }

        public Builder addGroup(double pProbability, ItemStack... pItemStacks) {
            if (pItemStacks.length == 0) {
                groups.add(new ProbabilityGroup(List.of(ItemStack.EMPTY), pProbability));
            } else {
                groups.add(new ProbabilityGroup(Arrays.asList(pItemStacks), pProbability));
            }
            return this;
        }

        public Builder rolls(int rolls) {
            this.rolls = rolls;
            return this;
        }

        public Builder weighted() {
            this.weighted = true;
            return this;
        }

        public ProbabilitySet build() {
            double totalProbability = getTotalProbability(groups);
            if (!weighted && totalProbability < 100) {
                double nothingProbability = 100 - totalProbability;
                this.addGroup(nothingProbability, new ItemStack(Items.AIR));
            }
            return new ProbabilitySet(groups, weighted, rolls);
        }
    }
}
