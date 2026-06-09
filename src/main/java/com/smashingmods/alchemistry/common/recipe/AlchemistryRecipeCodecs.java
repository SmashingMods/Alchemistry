package com.smashingmods.alchemistry.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Shared {@link Codec}s and {@link StreamCodec}s for the recipe types that are reused across more than
 * one serializer. Recipe serializers are codec-based, so the wrapper types that AlchemyLib exposes
 * without a codec ({@link IngredientStack}) and the amount-keyed fluid shape Alchemistry stores on disk
 * need codecs defined here rather than re-derived in every serializer.
 */
public final class AlchemistryRecipeCodecs {

    /**
     * The identifier a serializer hands to a freshly decoded recipe. A recipe's real identity is its
     * {@link net.minecraft.world.item.crafting.RecipeHolder} key, which is not part of the serialized
     * payload; {@link AbstractProcessingRecipe} still carries a {@link AbstractProcessingRecipe#getId() getId()}
     * the recipe registry and block entities key on, so decoded recipes hold this placeholder until that
     * keying is sourced from the holder.
     */
    public static final ResourceLocation UNKEYED_RECIPE_ID = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, "unkeyed");

    private AlchemistryRecipeCodecs() {
    }

    /**
     * Mirrors {@link IngredientStack#toJson()} / {@link IngredientStack#fromJson(com.google.gson.JsonObject)}:
     * an {@code ingredient} object plus an optional {@code count} that defaults to 1.
     */
    public static final Codec<IngredientStack> INGREDIENT_STACK = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientStack::getIngredient),
            Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientStack::getCount)
    ).apply(instance, IngredientStack::new));

    /**
     * Network codec for {@link IngredientStack}, delegating to its
     * {@link IngredientStack#toNetwork(RegistryFriendlyByteBuf) toNetwork} /
     * {@link IngredientStack#fromNetwork(RegistryFriendlyByteBuf) fromNetwork} pair (which encode the
     * {@link Ingredient} over {@link Ingredient#CONTENTS_STREAM_CODEC} plus the count). AlchemyLib exposes
     * those buffer methods but no {@code StreamCodec}, so the serializers compose this one instead.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> INGREDIENT_STACK_STREAM_CODEC =
            StreamCodec.of((buffer, ingredientStack) -> ingredientStack.toNetwork(buffer), IngredientStack::fromNetwork);

    /**
     * Item stack disk codec for recipe inputs and outputs: an {@code item} registry id plus an optional
     * {@code count} that defaults to 1. Preserves the {@code {"item":..,"count":..}} shape the recipes have
     * always stored, rather than vanilla {@link ItemStack#CODEC}'s {@code id}/{@code components} shape.
     */
    public static final Codec<ItemStack> ITEM_STACK = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));

    /**
     * Mirrors {@link com.smashingmods.alchemylib.datagen.DatagenHelpers#fluidStacktoJson}: the fluid's
     * registry id under {@code fluid} plus an optional {@code amount} that defaults to 1000. Resolving the
     * fluid through {@link BuiltInRegistries#FLUID} by-name codec correctly fails on an unknown fluid id.
     */
    public static final Codec<FluidStack> FLUID_STACK = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
            Codec.INT.optionalFieldOf("amount", 1000).forGetter(FluidStack::getAmount)
    ).apply(instance, FluidStack::new));

    /**
     * Item stack codec for dissolver probability results. Unlike {@link #ITEM_STACK} this permits
     * {@code minecraft:air}, which weighted dissolver groups use to represent a "nothing" roll.
     * Reads {@code item} plus an optional {@code count} that defaults to 1. The count is
     * {@linkplain ExtraCodecs#NON_NEGATIVE_INT non-negative} rather than positive so the {@code minecraft:air}
     * "nothing" stack, whose count is 0, round-trips through datagen instead of failing to encode.
     */
    public static final Codec<ItemStack> ITEM_STACK_RESULT = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));
}
