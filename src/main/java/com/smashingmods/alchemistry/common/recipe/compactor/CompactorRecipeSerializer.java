package com.smashingmods.alchemistry.common.recipe.compactor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CompactorRecipeSerializer<T extends CompactorRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CompactorRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "compactor").forGetter(CompactorRecipe::getGroup),
                IngredientStack.CODEC.fieldOf("input").forGetter(CompactorRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(CompactorRecipe::getOutput)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, CompactorRecipe::getGroup,
                IngredientStack.STREAM_CODEC, CompactorRecipe::getInput,
                ItemStack.STREAM_CODEC, CompactorRecipe::getOutput,
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

    public interface IFactory<T extends CompactorRecipe> {
        T create(String group, IngredientStack input, ItemStack output);
    }
}
