package com.smashingmods.alchemistry.common.recipe.fission;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.RecipeCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FissionRecipeSerializer<T extends FissionRecipe> implements RecipeSerializer<T> {

    private final FissionRecipeSerializer.IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public FissionRecipeSerializer(FissionRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "fission").forGetter(FissionRecipe::getGroup),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("input").forGetter(FissionRecipe::getInput),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("output1").forGetter(FissionRecipe::getOutput1),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("output2").forGetter(FissionRecipe::getOutput2)
        ).apply(instance, (group, input, output1, output2) -> factory.create(null, group, input, output1, output2)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                FissionRecipe::getGroup,
                ItemStack.STREAM_CODEC,
                FissionRecipe::getInput,
                ItemStack.STREAM_CODEC,
                FissionRecipe::getOutput1,
                ItemStack.STREAM_CODEC,
                FissionRecipe::getOutput2,
                (group, input, output1, output2) -> factory.create(null, group, input, output1, output2)
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
        T create(ResourceLocation pId, String pGroup, ItemStack pInput, ItemStack pOutput1, ItemStack pOutput2);
    }
}