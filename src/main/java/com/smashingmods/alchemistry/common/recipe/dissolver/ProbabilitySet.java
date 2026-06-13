package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public record ProbabilitySet(List<ProbabilityGroup> probabilityGroups, boolean weighted, int rolls) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ProbabilitySet> STREAM_CODEC = StreamCodec.composite(
            ProbabilityGroup.LIST_STREAM_CODEC,
            ProbabilitySet::probabilityGroups,

            ByteBufCodecs.BOOL,
            ProbabilitySet::weighted,
            
            ByteBufCodecs.INT,
            ProbabilitySet::rolls,
            
            ProbabilitySet::new
    );
    // Accepts both a single ProbabilityGroup object and an array of ProbabilityGroups for the "groups" field
    private static final Codec<List<ProbabilityGroup>> GROUPS_CODEC = Codec.either(
            ProbabilityGroup.CODEC.codec(),
            ProbabilityGroup.CODEC.codec().listOf()
    ).xmap(
            e -> e.map(List::of, list -> list),
            list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list)
    );
    public static final MapCodec<ProbabilitySet> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            GROUPS_CODEC.fieldOf("groups").forGetter(ProbabilitySet::probabilityGroups),
            Codec.BOOL.fieldOf("weighted").forGetter(ProbabilitySet::weighted),
            Codec.INT.fieldOf("rolls").forGetter(ProbabilitySet::rolls)).apply(inst, ProbabilitySet::new)
    );


    @SuppressWarnings("unused")
    public ProbabilitySet(List<ProbabilityGroup> pProbabilityGroups) {
        this(pProbabilityGroups, true, 1);
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
                    outputProbability += (group.probability() / totalProbability);

                    if (outputProbability >= targetProbability) {
                        toReturn.addAll(group.output());
                        break;
                    }
                }
            } else {
                if ((totalProbability / 100) < targetProbability) return toReturn;

                for (ProbabilityGroup group : probabilityGroups) {
                    if (group.probability() >= random.nextInt(101)) {
                        toReturn.addAll(group.output());
                    }
                }
            }
        }
        return toReturn;
    }

    private double getTotalProbability() {
        return getTotalProbability(probabilityGroups);
    }

    private static double getTotalProbability(List<ProbabilityGroup> pGroups) {
        return pGroups.stream()
                .mapToDouble(ProbabilityGroup::probability)
                .sum();
    }

    public ProbabilitySet copy() {
        return new ProbabilitySet(List.copyOf(probabilityGroups), weighted, rolls);
    }

    public static class Builder {
        private final List<ProbabilityGroup> groups = new ArrayList<>();
        private boolean weighted = false;
        private int rolls = 1;

        public Builder() {
        }

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
