package com.smashingmods.alchemistry.common.recipe.combiner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CombinerRecipeSerializer<T extends CombinerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CombinerRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "combiner").forGetter(CombinerRecipe::getGroup),
                IngredientStack.CODEC.listOf().fieldOf("input").forGetter(CombinerRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(CombinerRecipe::getOutput)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, CombinerRecipe::getGroup,
                IngredientStack.STREAM_CODEC.apply(ByteBufCodecs.list()), CombinerRecipe::getInput,
                ItemStack.STREAM_CODEC, CombinerRecipe::getOutput,
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

    public interface IFactory<T extends CombinerRecipe> {
        T create(String group, java.util.List<IngredientStack> input, ItemStack output);
    }
}
