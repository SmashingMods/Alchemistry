package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.RecipeCodecs;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ProbabilityGroup(List<ItemStack> output, double probability) {
    private static final Codec<ItemStack> ITEM_STACK_COMPAT_CODEC = Codec.either(
            RecipeCodecs.LEGACY_ITEM_STACK_CODEC,
            ItemStack.CODEC
    ).xmap(either -> either.map(stack -> stack, stack -> stack), Either::right);

    // Stream codec that filters out empty ItemStacks before encoding (1.20.1 recipes use minecraft:air for "no result")
    private static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> NON_EMPTY_ITEM_LIST_STREAM_CODEC =
            StreamCodec.of(
                    (buf, list) -> ItemStack.LIST_STREAM_CODEC.encode(buf, list.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList())),
                    ItemStack.LIST_STREAM_CODEC::decode
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ProbabilityGroup> STREAM_CODEC = StreamCodec.composite(
            NON_EMPTY_ITEM_LIST_STREAM_CODEC,
            ProbabilityGroup::output,

            ByteBufCodecs.DOUBLE,
            ProbabilityGroup::probability,
            
            ProbabilityGroup::new);
    public static final MapCodec<ProbabilityGroup> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                // Filter empty ItemStacks (minecraft:air used in 1.20.1 as "no result")
                ITEM_STACK_COMPAT_CODEC.listOf()
                    .xmap(
                        list -> list.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList()),
                        list -> list
                    )
                    .fieldOf("results").forGetter(ProbabilityGroup::output),
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
