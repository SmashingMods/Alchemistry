package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DissolverRecipeSerializer<T extends DissolverRecipe> implements RecipeSerializer<T> {

    private final DissolverRecipeSerializer.IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public DissolverRecipeSerializer(DissolverRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "dissolver").forGetter(DissolverRecipe::getGroup),
                IngredientStack.CODEC.fieldOf("input").forGetter(DissolverRecipe::getInput),
                ProbabilitySet.CODEC.codec().fieldOf("output").forGetter(DissolverRecipe::getOutput)
        ).apply(instance, (group, input, output) -> this.factory.create(null, group, input, output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                DissolverRecipe::getGroup,
                IngredientStack.STREAM_CODEC,
                DissolverRecipe::getInput,
                ProbabilitySet.STREAM_CODEC,
                DissolverRecipe::getOutput,
                (group, input, output) -> this.factory.create(null, group, input, output)
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

    public interface IFactory<T extends Recipe<?>> {
        T create(ResourceLocation pId, String pGroup, IngredientStack pInput, ProbabilitySet pOutput);
    }
}