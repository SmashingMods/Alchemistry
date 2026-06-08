package com.smashingmods.alchemistry.common.recipe.combiner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashSet;
import java.util.Set;

public class CombinerRecipeSerializer<T extends CombinerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("group").forGetter(CombinerRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.listOf().fieldOf("input").forGetter(CombinerRecipe::getInput),
                AlchemistryRecipeCodecs.ITEM_STACK.fieldOf("result").forGetter(CombinerRecipe::getOutput)
        ).apply(instance, (group, input, output) -> pFactory.create(AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID, group, new LinkedHashSet<>(input), output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, CombinerRecipe::getGroup,
                AlchemistryRecipeCodecs.INGREDIENT_STACK_STREAM_CODEC.apply(ByteBufCodecs.list()), CombinerRecipe::getInput,
                ItemStack.OPTIONAL_STREAM_CODEC, CombinerRecipe::getOutput,
                (group, input, output) -> pFactory.create(AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID, group, new LinkedHashSet<>(input), output)
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

    public interface IFactory<T extends Recipe<RecipeInput>> {
        T create(ResourceLocation pId, String pGroup, Set<IngredientStack> pInput, ItemStack pOutput);
    }
}
