package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.testsupport.BootstrappedTest;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 tests for the dissolver probability machinery in {@link ProbabilitySet}. The groups hold
 * {@link ItemStack}s, so this extends {@link BootstrappedTest} and builds groups from vanilla {@code Items.*}.
 */
class ProbabilitySetTest extends BootstrappedTest {

    private static final long SEED = 0xA1C4E315L;

    @Test
    void probSet_weightsSum() {
        ProbabilitySet set = ProbabilitySet.Builder.createSet()
                .weighted()
                .addGroup(25.0, new ItemStack(Items.IRON_INGOT))
                .addGroup(15.0, new ItemStack(Items.GOLD_INGOT))
                .addGroup(10.0, new ItemStack(Items.DIAMOND))
                .build();

        double total = set.getProbabilityGroups().stream()
                .mapToDouble(ProbabilityGroup::getProbability)
                .sum();

        assertEquals(50.0, total);
    }

    @Test
    void probSet_builderAddsNothingGroup() {
        // One explicit group added; non-weighted with total < 100, so build() must append exactly one more group:
        // an Items.AIR "nothing" group carrying the remaining 100 - total probability.
        int explicitGroups = 1;
        List<ProbabilityGroup> groups = ProbabilitySet.Builder.createSet()
                .addGroup(70.0, new ItemStack(Items.IRON_INGOT))
                .build()
                .getProbabilityGroups();

        assertEquals(explicitGroups + 1, groups.size());

        ProbabilityGroup nothing = groups.get(groups.size() - 1);
        assertEquals(30.0, nothing.getProbability());
        assertEquals(Items.AIR, nothing.getOutput().get(0).getItem());
    }

    @Test
    void probSet_seededDeterminism() {
        ProbabilitySet set = ProbabilitySet.Builder.createSet()
                .addGroup(60.0, new ItemStack(Items.IRON_INGOT))
                .addGroup(30.0, new ItemStack(Items.GOLD_INGOT))
                .build();

        List<ItemStack> first = set.calculateOutput(RandomSource.create(SEED));
        List<ItemStack> second = set.calculateOutput(RandomSource.create(SEED));

        assertEquals(first.size(), second.size());
        for (int i = 0; i < first.size(); i++) {
            assertEquals(first.get(i).getItem(), second.get(i).getItem());
            assertEquals(first.get(i).getCount(), second.get(i).getCount());
        }
    }

    @Test
    void probSet_everyRollInDeclaredSet() {
        ProbabilitySet set = ProbabilitySet.Builder.createSet()
                .addGroup(40.0, new ItemStack(Items.IRON_INGOT))
                .addGroup(35.0, new ItemStack(Items.GOLD_INGOT))
                .build();

        // Every item declared across the groups (build() also appends an AIR "nothing" group).
        Set<net.minecraft.world.item.Item> declared = new HashSet<>();
        for (ProbabilityGroup group : set.getProbabilityGroups()) {
            for (ItemStack stack : group.getOutput()) {
                declared.add(stack.getItem());
            }
        }

        // Over many seeded rolls, every produced stack must be a member of some declared group (membership, not an exact roll).
        for (long seed = 0; seed < 256; seed++) {
            for (ItemStack produced : set.calculateOutput(RandomSource.create(seed))) {
                assertTrue(declared.contains(produced.getItem()),
                        () -> "produced item not in any declared group: " + produced.getItem());
            }
        }
    }
}
