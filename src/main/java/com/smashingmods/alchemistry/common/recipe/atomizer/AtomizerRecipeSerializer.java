package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.common.recipe.AlchemistryRecipeCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public AtomizerRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "atomizer").forGetter(AtomizerRecipe::getGroup),
                AlchemistryRecipeCodecs.FLUID_STACK.fieldOf("input").forGetter(AtomizerRecipe::getInput),
                AlchemistryRecipeCodecs.ITEM_STACK.fieldOf("result").forGetter(AtomizerRecipe::getOutput)
        ).apply(instance, (group, input, output) -> pFactory.create(AlchemistryRecipeCodecs.UNKEYED_RECIPE_ID, group, input, output)));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, AtomizerRecipe::getGroup,
                FluidStack.OPTIONAL_STREAM_CODEC, AtomizerRecipe::getInput,
                ItemStack.OPTIONAL_STREAM_CODEC, AtomizerRecipe::getOutput,
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
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}
