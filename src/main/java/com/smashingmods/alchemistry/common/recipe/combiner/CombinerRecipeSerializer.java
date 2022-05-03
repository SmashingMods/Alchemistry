package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CombinerRecipeSerializer<T extends CombinerRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final IFactory<T> factory;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    @Nonnull
    public T fromJson(@Nonnull ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

        String group = pSerializedRecipe.get("group").getAsString();
        JsonArray inputJson = pSerializedRecipe.getAsJsonArray("input");
        List<ItemStack> input = new ArrayList<>();
        ItemStack output;

        inputJson.forEach(element -> input.add(ShapedRecipe.itemStackFromJson(element.getAsJsonObject())));

        if (pSerializedRecipe.get("result").isJsonObject()) {
            output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
        } else {
            output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "item"));
        }

        return this.factory.create(pRecipeId, group, input, output);
    }

    @Override
    public T fromNetwork(@Nonnull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        List<ItemStack> inputList = Lists.newArrayList();
        for (int i = 0; i < 4; i++) {
            inputList.add(pBuffer.readItem());
        }
        ItemStack output = pBuffer.readItem();
        return this.factory.create(pRecipeId, group, inputList, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        for (int i = 0; i < 4; i++) {
            pBuffer.writeItemStack(pRecipe.input.get(i), true);
        }
        pBuffer.writeItemStack(pRecipe.output, true);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation pId, String pGroup, List<ItemStack> pInput, ItemStack pOutput);
    }
}