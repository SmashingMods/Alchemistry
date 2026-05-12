package com.smashingmods.alchemistry.common.recipe.combiner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.RecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CombinerRecipeSerializer<T extends CombinerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;
    private static final StreamCodec<RegistryFriendlyByteBuf, List<IngredientStack>> INGREDIENT_LIST_STREAM_CODEC =
            IngredientStack.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new));

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "combiner").forGetter(CombinerRecipe::getGroup),
                IngredientStack.CODEC.listOf().fieldOf("input").forGetter(CombinerRecipe::getInput),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("result").forGetter(CombinerRecipe::getOutput)
        ).apply(instance, (group, input, output) -> this.factory.create(null, group, new LinkedHashSet<>(input), output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                CombinerRecipe::getGroup,
                INGREDIENT_LIST_STREAM_CODEC,
                CombinerRecipe::getInput,
                ItemStack.STREAM_CODEC,
                CombinerRecipe::getOutput,
                (group, input, output) -> this.factory.create(null, group, new LinkedHashSet<>(input), output)
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
        T create(ResourceLocation pId, String pGroup, Set<IngredientStack> pInput, ItemStack pOutput);
    }
}