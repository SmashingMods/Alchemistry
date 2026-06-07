package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import net.minecraft.network.FriendlyByteBuf;
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

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(output.size());
        for (ItemStack stack : output) {
            buf.writeItem(stack);
        }
        buf.writeDouble(probability);
    }

    public static ProbabilityGroup fromNetwork(FriendlyByteBuf buf) {
        List<ItemStack> stacks = Lists.newArrayList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            stacks.add(buf.readItem());
        }
        double probability = buf.readDouble();
        return new ProbabilityGroup(stacks, probability);
    }
}
