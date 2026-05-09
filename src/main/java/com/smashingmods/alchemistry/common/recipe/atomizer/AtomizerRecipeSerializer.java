package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public AtomizerRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.STRING.optionalFieldOf("group", "atomizer").forGetter(AtomizerRecipe::getGroup),
                FluidStack.CODEC.fieldOf("input").forGetter(AtomizerRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(AtomizerRecipe::getOutput)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, AtomizerRecipe::getGroup,
                FluidStack.STREAM_CODEC, AtomizerRecipe::getInput,
                ItemStack.STREAM_CODEC, AtomizerRecipe::getOutput,
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

    public interface IFactory<T extends AtomizerRecipe> {
        T create(String group, FluidStack input, ItemStack output);
    }
}
