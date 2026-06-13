package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.RecipeCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe> implements RecipeSerializer<T> {

    private static int parseCount = 0;

    private final IFactory<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public AtomizerRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "atomizer").forGetter(AtomizerRecipe::getGroup),
                RecipeCodecs.LEGACY_FLUID_STACK_CODEC.fieldOf("input").forGetter(AtomizerRecipe::getInput),
                RecipeCodecs.LEGACY_ITEM_STACK_CODEC.fieldOf("result").forGetter(AtomizerRecipe::getOutput)
        ).apply(instance, (group, input, output) -> {
            int n = ++parseCount;
            Alchemistry.LOGGER.info("[Alchemistry] AtomizerSerializer parsed recipe #{}: group={} input={} output={}", n, group, input, output);
            return this.factory.create(null, group, input, output);
        }));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                AtomizerRecipe::getGroup,
                FluidStack.STREAM_CODEC,
                AtomizerRecipe::getInput,
                ItemStack.STREAM_CODEC,
                AtomizerRecipe::getOutput,
                (group, input, output) -> {
                    Alchemistry.LOGGER.info("[Alchemistry] AtomizerSerializer streamCodec decoded: group={} input={} output={}", group, input, output);
                    return this.factory.create(null, group, input, output);
                }
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
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}
