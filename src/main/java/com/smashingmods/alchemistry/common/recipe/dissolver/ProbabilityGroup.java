package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ProbabilityGroup {

    /**
     * Codec for the on-disk group shape: an array of item stacks under {@code results} (which may include
     * {@code minecraft:air} for weighted "nothing" rolls) plus the group's {@code probability}.
     */
    public static final Codec<ProbabilityGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AlchemistryRecipeCodecs.ITEM_STACK_RESULT.listOf().fieldOf("results").forGetter(ProbabilityGroup::getOutput),
            Codec.DOUBLE.fieldOf("probability").forGetter(ProbabilityGroup::getProbability)
    ).apply(instance, ProbabilityGroup::new));

    /**
     * Network codec mirroring the on-disk shape: the {@code results} stacks (which may be empty, as the
     * weighted "nothing" roll is) followed by the {@code probability}. {@link ItemStack#OPTIONAL_STREAM_CODEC}
     * accepts empty stacks, matching the buffer encoding this replaces.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, ProbabilityGroup> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), ProbabilityGroup::getOutput,
            ByteBufCodecs.DOUBLE, ProbabilityGroup::getProbability,
            ProbabilityGroup::new
    );

    private final List<ItemStack> output;
    private final double probability;

    public ProbabilityGroup(List<ItemStack> pOutput, double pProbability) {
        this.output = pOutput;
        this.probability = pProbability;
    }

    public ProbabilityGroup(List<ItemStack> pOutput) {
        this.output = pOutput;
        this.probability = 100;
    }

    public List<ItemStack> getOutput() {
        return this.output;
    }

    public double getProbability() {
        return this.probability;
    }
}
