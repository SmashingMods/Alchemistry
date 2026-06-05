package com.smashingmods.alchemistry.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Shared {@link Codec}s for the recipe types that are reused across more than one serializer. As of
 * 1.20.2 recipe serializers are codec-based, so the wrapper types that AlchemyLib exposes without a
 * codec ({@link IngredientStack}) and the amount-keyed fluid shape Alchemistry stores on disk need
 * codecs defined here rather than re-derived in every serializer.
 */
public final class AlchemistryRecipeCodecs {

    private AlchemistryRecipeCodecs() {
    }

    /**
     * Mirrors {@link IngredientStack#toJson()} / {@link IngredientStack#fromJson(com.google.gson.JsonObject)}:
     * an {@code ingredient} object plus an optional {@code count} that defaults to 1.
     */
    public static final Codec<IngredientStack> INGREDIENT_STACK = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IngredientStack::getIngredient),
            ExtraCodecs.strictOptionalField(Codec.INT, "count", 1).forGetter(IngredientStack::getCount)
    ).apply(instance, IngredientStack::new));

    /**
     * Mirrors {@link com.smashingmods.alchemylib.datagen.DatagenHelpers#fluidStacktoJson}: the fluid's
     * registry id under {@code fluid} plus an optional {@code amount} that defaults to 1000. Resolving the
     * fluid through {@link BuiltInRegistries#FLUID} by-name codec correctly fails on an unknown fluid id.
     */
    public static final Codec<FluidStack> FLUID_STACK = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
            ExtraCodecs.strictOptionalField(Codec.INT, "amount", 1000).forGetter(FluidStack::getAmount)
    ).apply(instance, FluidStack::new));

    /**
     * Item stack codec for dissolver probability results. Unlike {@code CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC}
     * this permits {@code minecraft:air}, which weighted dissolver groups use to represent a "nothing" roll.
     * Reads {@code item} plus an optional {@code count} that defaults to 1.
     */
    public static final Codec<ItemStack> ITEM_STACK_RESULT = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
            ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));
}
