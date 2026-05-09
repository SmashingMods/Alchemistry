package com.smashingmods.alchemistry.common.recipe.fission;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FissionRecipeSerializer<T extends FissionRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public FissionRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "fission").forGetter(FissionRecipe::getGroup),
                ItemStack.CODEC.fieldOf("input").forGetter(FissionRecipe::getInput),
                ItemStack.CODEC.fieldOf("output1").forGetter(FissionRecipe::getOutput1),
                ItemStack.CODEC.fieldOf("output2").forGetter(FissionRecipe::getOutput2)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, FissionRecipe::getGroup,
                ItemStack.STREAM_CODEC, FissionRecipe::getInput,
                ItemStack.STREAM_CODEC, FissionRecipe::getOutput1,
                ItemStack.STREAM_CODEC, FissionRecipe::getOutput2,
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

    public interface IFactory<T extends FissionRecipe> {
        T create(String group, ItemStack input, ItemStack output1, ItemStack output2);
    }
}
