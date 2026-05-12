package com.smashingmods.alchemistry.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class RecipeCodecs {

    /**
     * Decodes an ItemStack from the legacy Alchemistry recipe format:
     * {"item": "modid:item_name", "count": N}
     */
    public static final Codec<ItemStack> LEGACY_ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemStack::getItem),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, ItemStack::new));

    /**
     * Decodes a FluidStack from the legacy Alchemistry recipe format:
     * {"fluid": "modid:fluid_name", "amount": N}
     */
    public static final Codec<FluidStack> LEGACY_FLUID_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
            Codec.INT.optionalFieldOf("amount", 1000).forGetter(FluidStack::getAmount)
    ).apply(instance, FluidStack::new));

    private RecipeCodecs() {
    }
}