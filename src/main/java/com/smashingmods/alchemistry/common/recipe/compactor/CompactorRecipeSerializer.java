package com.smashingmods.alchemistry.common.recipe.compactor;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CompactorRecipeSerializer<T extends CompactorRecipe> implements RecipeSerializer<T> {

    private final CompactorRecipeSerializer.IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public CompactorRecipeSerializer(CompactorRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "compactor").forGetter(CompactorRecipe::getGroup),
                AlchemistryRecipeCodecs.INGREDIENT_STACK.fieldOf("input").forGetter(CompactorRecipe::getInput),
                AlchemistryRecipeCodecs.ITEM_STACK.fieldOf("result").forGetter(CompactorRecipe::getOutput)
        ).apply(instance, (group, input, output) -> pFactory.create(AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID, group, input, output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, CompactorRecipe::getGroup,
                AlchemistryRecipeCodecs.INGREDIENT_STACK_STREAM_CODEC, CompactorRecipe::getInput,
                ItemStack.OPTIONAL_STREAM_CODEC, CompactorRecipe::getOutput,
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
        T create(ResourceLocation pId, String pGroup, IngredientStack pInput, ItemStack pOutput);
    }
}
