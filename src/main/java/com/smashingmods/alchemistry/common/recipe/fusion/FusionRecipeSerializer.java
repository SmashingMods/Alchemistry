package com.smashingmods.alchemistry.common.recipe.fusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FusionRecipeSerializer<T extends FusionRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public FusionRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "fusion").forGetter(FusionRecipe::getGroup),
                ItemStack.CODEC.fieldOf("input1").forGetter(FusionRecipe::getInput1),
                ItemStack.CODEC.fieldOf("input2").forGetter(FusionRecipe::getInput2),
                ItemStack.CODEC.fieldOf("output").forGetter(FusionRecipe::getOutput)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, FusionRecipe::getGroup,
                ItemStack.STREAM_CODEC, FusionRecipe::getInput1,
                ItemStack.STREAM_CODEC, FusionRecipe::getInput2,
                ItemStack.STREAM_CODEC, FusionRecipe::getOutput,
                factory::create
        );
    }

    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    public interface IFactory<T extends FusionRecipe> {
        T create(String group, ItemStack input1, ItemStack input2, ItemStack output);
    }
}
