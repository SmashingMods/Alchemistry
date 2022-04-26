package com.smashingmods.alchemistry.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

public class DatagenUtil {

    public static void itemStackToJson(JsonObject pJson, String pKey, ItemStack pItemStack) {
        if (!pItemStack.isEmpty()) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Objects.requireNonNull(pItemStack.getItem().getRegistryName()).toString());

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
                    Objects.requireNonNull(itemStack.getItem().getRegistryName());
                    jsonObject.addProperty("item", itemStack.getItem().getRegistryName().toString());
                    if (itemStack.getCount() > 1) {
                        jsonObject.addProperty("count", itemStack.getCount());
                    }
                } else {
                    jsonObject.addProperty("item", ItemStack.EMPTY.getItem().getRegistryName().toString());
                }
                jsonArray.add(jsonObject);
            }
            pJson.add(pKey, jsonArray);
        }
    }

    public static void fluidStacktoJson(JsonObject pJson, String pKey, FluidStack pFluidStack) {
        if (!pFluidStack.isEmpty()) {

            JsonObject jsonObject = new JsonObject();
            ResourceLocation fluidLocation = pFluidStack.getFluid().getRegistryName();
            String amount = String.valueOf(pFluidStack.getAmount());

            jsonObject.addProperty("fluid", Objects.requireNonNull(fluidLocation).toString());
            jsonObject.addProperty("amount", amount);
            pJson.add(pKey, jsonObject);
        }
    }
}
