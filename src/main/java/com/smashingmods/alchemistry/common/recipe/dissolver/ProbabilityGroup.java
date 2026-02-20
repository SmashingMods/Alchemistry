package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ProbabilityGroup(List<ItemStack> output, double probability) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ProbabilityGroup> STREAM_CODEC = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC,
            ProbabilityGroup::output,

            ByteBufCodecs.DOUBLE,
            ProbabilityGroup::probability,
            
            ProbabilityGroup::new);
    public static final MapCodec<ProbabilityGroup> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStack.CODEC.listOf().fieldOf("results").forGetter(ProbabilityGroup::output), 
            Codec.DOUBLE.fieldOf("probability").forGetter(ProbabilityGroup::probability)).apply(inst, ProbabilityGroup::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ProbabilityGroup>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity));

    public ProbabilityGroup(List<ItemStack> pOutput) {
        this(pOutput, 100);
    }

    public static ProbabilityGroup createSafety(List<ItemStack> outputs, double probability) {
        List<ItemStack> output = new ArrayList<>();

        for (ItemStack itemStack : outputs) {

            int count = itemStack.getCount();

            while (count > 64) {
                output.add(new ItemStack(itemStack.getItem(), 64));
                count -= 64;
            }

            output.add(new ItemStack(itemStack.getItem(), Math.max(count, 1)));
        }
        
        return new ProbabilityGroup(output, probability);
    }
}
