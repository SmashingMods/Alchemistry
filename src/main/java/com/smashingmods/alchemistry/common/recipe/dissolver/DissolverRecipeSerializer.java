package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class DissolverRecipeSerializer<T extends DissolverRecipe> implements RecipeSerializer<T> {

    private final DissolverRecipeSerializer.IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public DissolverRecipeSerializer(DissolverRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("group").forGetter(DissolverRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.fieldOf("input").forGetter(DissolverRecipe::getInput),
                ProbabilitySet.CODEC.fieldOf("output").forGetter(DissolverRecipe::getOutput)
        ).apply(instance, (group, input, output) -> pFactory.create(AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID, group, input, output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, DissolverRecipe::getGroup,
                AlchemistryRecipeCodecs.INGREDIENT_STACK_STREAM_CODEC, DissolverRecipe::getInput,
                ProbabilitySet.STREAM_CODEC, DissolverRecipe::getOutput,
                (group, input, output) -> pFactory.create(AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID, group, input, output)
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

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, IngredientStack pInput, ProbabilitySet pOutput);
    }
}
