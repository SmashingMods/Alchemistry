package com.smashingmods.alchemistry.common.recipe.liquifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class LiquifierRecipeSerializer<T extends LiquifierRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public LiquifierRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "liquifier").forGetter(LiquifierRecipe::getGroup),
                IngredientStack.CODEC.fieldOf("input").forGetter(LiquifierRecipe::getInput),
                FluidStack.CODEC.fieldOf("result").forGetter(LiquifierRecipe::getOutput)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, LiquifierRecipe::getGroup,
                IngredientStack.STREAM_CODEC, LiquifierRecipe::getInput,
                FluidStack.STREAM_CODEC, LiquifierRecipe::getOutput,
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

    public interface IFactory<T extends LiquifierRecipe> {
        T create(String group, IngredientStack input, FluidStack output);
    }
}
