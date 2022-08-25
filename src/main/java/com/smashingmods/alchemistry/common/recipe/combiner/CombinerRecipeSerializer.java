package com.smashingmods.alchemistry.common.recipe.combiner;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smashingmods.alchemylib.common.item.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class CombinerRecipeSerializer<T extends CombinerRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final IFactory<T> factory;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

        String group = pSerializedRecipe.get("group").getAsString();
        JsonArray inputJson = pSerializedRecipe.getAsJsonArray("input");
        List<IngredientStack> input = new ArrayList<>();
        ItemStack output;

        inputJson.forEach(element -> input.add(IngredientStack.fromJson(element.getAsJsonObject())));

        if (pSerializedRecipe.get("result").isJsonObject()) {
            output = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject("result"), false, true);
        } else {
            output = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject("item"), false, true);
        }
        return this.factory.create(pRecipeId, group, input, output);
    }

    @Override
    public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        int inputCount = pBuffer.readInt();
        List<IngredientStack> inputList = Lists.newArrayList();
        for (int i = 0; i < inputCount; i++) {
            inputList.add(i, IngredientStack.fromNetwork(pBuffer));
        }
        ItemStack output = pBuffer.readItem();
        return this.factory.create(pRecipeId, group, inputList, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeInt(pRecipe.getInput().size());
        for (int i = 0; i < pRecipe.getInput().size(); i++) {
            pRecipe.getInput().get(i).toNetwork(pBuffer);
        }
        pBuffer.writeItemStack(pRecipe.getOutput(), true);
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, List<IngredientStack> pInput, ItemStack pOutput);
    }
}