package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ProbabilityGroup {

    public static final Codec<ProbabilityGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("results").forGetter(ProbabilityGroup::getOutput),
            Codec.DOUBLE.fieldOf("probability").forGetter(ProbabilityGroup::getProbability)
    ).apply(instance, ProbabilityGroup::new));

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
        this(pOutput, 100);
    }

    public List<ItemStack> getOutput() {
        return this.output;
    }

    public double getProbability() {
        return this.probability;
    }
}
