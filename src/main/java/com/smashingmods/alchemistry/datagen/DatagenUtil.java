package com.smashingmods.alchemistry.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.item.IngredientStack;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DatagenUtil {

    public static void itemStackToJson(JsonObject pJson, String pKey, ItemStack pItemStack) {
        if (!pItemStack.isEmpty()) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pItemStack.getItem())).toString());

            if (pItemStack.getCount() > 1) {
                jsonObject.addProperty("count", pItemStack.getCount());
            }
            pJson.add(pKey, jsonObject);
        }
    }

    public static void itemStackListToJson(JsonObject pJson, String pKey, List<ItemStack> pItemStackList) {
        if (!pItemStackList.isEmpty()) {

            JsonArray jsonArray = new JsonArray();

            for (ItemStack itemStack : pItemStackList) {

                JsonObject jsonObject = new JsonObject();
                if (itemStack != null) {
                    jsonObject.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemStack.getItem())).toString());
                    if (itemStack.getCount() > 1) {
                        jsonObject.addProperty("count", itemStack.getCount());
                    }
                } else {
                    jsonObject.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ItemStack.EMPTY.getItem())).toString());
                }
                jsonArray.add(jsonObject);
            }
            pJson.add(pKey, jsonArray);
        }
    }

    public static void ingredientStackListToJson(JsonObject pJson, String pKey, List<IngredientStack> pIngredientStackList) {
        if (!pIngredientStackList.isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for (IngredientStack ingredientStack : pIngredientStackList) {
                jsonArray.add(ingredientStack.toJson());
            }
            pJson.add(pKey, jsonArray);
        }
    }

    public static void fluidStacktoJson(JsonObject pJson, String pKey, FluidStack pFluidStack) {
        if (!pFluidStack.isEmpty()) {

            JsonObject jsonObject = new JsonObject();
            ResourceLocation fluidLocation = ForgeRegistries.FLUIDS.getKey(pFluidStack.getFluid());
            String amount = String.valueOf(pFluidStack.getAmount());

            jsonObject.addProperty("fluid", Objects.requireNonNull(fluidLocation).toString());
            jsonObject.addProperty("amount", amount);
            pJson.add(pKey, jsonObject);
        }
    }

    public static ItemStack toItemStack(String pString) {
        return toItemStack(pString, 1);
    }

    public static ItemStack toItemStack(String pString, int pCount) {
        return new ItemStack(getChemicalItem(pString), pCount);
    }

    public static IngredientStack toIngredientStack(String pString) {
        return toIngredientStack(pString, 1);
    }

    public static IngredientStack toIngredientStack(String pString, int pCount) {
        return new IngredientStack(getChemicalItem(pString), pCount);
    }

    private static ItemLike getChemicalItem(String pString) {

        ResourceLocation resourceLocation = new ResourceLocation(pString);

        Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(pString);
        Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(pString.replace(" ", "_"));

        Item outputItem = ForgeRegistries.ITEMS.getValue(resourceLocation);
        Block outputBlock = ForgeRegistries.BLOCKS.getValue(resourceLocation);

        if (optionalElement.isPresent()) {
            return optionalElement.get();
        } else if (optionalCompound.isPresent()) {
            return optionalCompound.get();
        } else if (outputItem != null) {
            return outputItem;
        } else if (outputBlock != null && outputBlock != Blocks.AIR && outputBlock != Blocks.WATER) {
            return outputBlock;
        } else {
            return Items.AIR;
        }
    }

    public static ResourceLocation getLocation(ItemStack pItemStack, String pType) {
        Objects.requireNonNull(pItemStack.getItem().getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("%s/%s", pType, pItemStack.getItem().getRegistryName().getPath()));
    }

    public static ResourceLocation getLocation(Item pItem, String pType) {
        Objects.requireNonNull(pItem.getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("%s/%s", pType, pItem.getRegistryName().getPath()));
    }

    public static ResourceLocation getLocation(FluidStack pFluidStack, String pType) {
        Objects.requireNonNull(pFluidStack.getFluid().getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("%s/%s", pType, pFluidStack.getFluid().getRegistryName().getPath()));
    }

    public static ModLoadedCondition modLoadedCondition(String pModId) {
        return new ModLoadedCondition(pModId);
    }

    public static NotCondition notCondition(ICondition pCondition) {
        return new NotCondition(pCondition);
    }

    public static AndCondition andCondition(ICondition pCondition1, ICondition pCondition2) {
        return new AndCondition(pCondition1, pCondition2);
    }

    public static OrCondition orCondition(ICondition pCondition1, ICondition pCondition2) {
        return new OrCondition(pCondition1, pCondition2);
    }

    public static TagEmptyCondition tagEmptyCondition(String pTag) {
        return new TagEmptyCondition(pTag);
    }

    public static NotCondition tagNotEmptyCondition(String pTag) {
        return notCondition(tagEmptyCondition(pTag));
    }
}
