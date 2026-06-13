package com.smashingmods.alchemistry.common.recipe.fusion;

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

public class FusionRecipeSerializer<T extends FusionRecipe> implements RecipeSerializer<T> {

    private final FusionRecipeSerializer.IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public FusionRecipeSerializer(FusionRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "fusion").forGetter(FusionRecipe::getGroup),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("input1").forGetter(FusionRecipe::getInput1),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("input2").forGetter(FusionRecipe::getInput2),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("output").forGetter(FusionRecipe::getOutput)
        ).apply(instance, (group, input1, input2, output) -> factory.create(null, group, input1, input2, output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                FusionRecipe::getGroup,
                ItemStack.STREAM_CODEC,
                FusionRecipe::getInput1,
                ItemStack.STREAM_CODEC,
                FusionRecipe::getInput2,
                ItemStack.STREAM_CODEC,
                FusionRecipe::getOutput,
                (group, input1, input2, output) -> factory.create(null, group, input1, input2, output)
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
        T create(ResourceLocation pId, String pGroup, ItemStack pInput1, ItemStack pInput2, ItemStack pOutput);
    }
}
