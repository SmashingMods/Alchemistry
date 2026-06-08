package com.smashingmods.alchemistry.datagen.recipe;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Objects;
import java.util.Optional;

public class RecipeUtils {

    public static ItemStack toStack(String pString) {
        return toStack(pString, 1);
    }

    public static ItemStack toStack(String pString, int pCount) {

        ResourceLocation resourceLocation = ResourceLocation.parse(pString);

        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(pString);
        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(pString.replace(" ", "_"));

        // BuiltInRegistries.{ITEM,BLOCK} are DefaultedRegistries: an unknown id resolves to AIR, never null.
        // Use getOptional so the null fall-through below still distinguishes "not registered" from a real entry.
        Item outputItem = BuiltInRegistries.ITEM.getOptional(resourceLocation).orElse(null);
        Block outputBlock = BuiltInRegistries.BLOCK.getOptional(resourceLocation).orElse(null);

        if (optionalElement.isPresent()) {
            return new ItemStack(optionalElement.get(), pCount);
        } else if (optionalCompound.isPresent()) {
            return new ItemStack(optionalCompound.get(), pCount);
        } else if (outputItem != null) {
            return new ItemStack(outputItem, pCount);
        } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
            return new ItemStack(outputBlock, pCount);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static ResourceLocation getLocation(ItemStack pItemStack, String pType) {
        return getLocation(pItemStack.getItem(), pType);
    }

    public static ResourceLocation getLocation(Item pItem, String pType) {
        return ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("%s/%s", pType, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItem)).getPath()));
    }

    public static ResourceLocation getLocation(FluidStack pFluidStack, String pType) {
        return ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("%s/%s", pType, Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(pFluidStack.getFluid())).getPath()));
    }
}
