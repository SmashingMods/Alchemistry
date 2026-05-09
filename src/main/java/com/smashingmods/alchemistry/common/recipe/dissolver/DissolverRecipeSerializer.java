package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DissolverRecipeSerializer<T extends DissolverRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public DissolverRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "dissolver").forGetter(DissolverRecipe::getGroup),
                IngredientStack.CODEC.fieldOf("input").forGetter(DissolverRecipe::getInput),
                ProbabilitySet.CODEC.fieldOf("output").forGetter(DissolverRecipe::getOutput)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, DissolverRecipe::getGroup,
                IngredientStack.STREAM_CODEC, DissolverRecipe::getInput,
                ProbabilitySet.STREAM_CODEC, DissolverRecipe::getOutput,
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

    public interface IFactory<T extends DissolverRecipe> {
        T create(String group, IngredientStack input, ProbabilitySet output);
    }
}
