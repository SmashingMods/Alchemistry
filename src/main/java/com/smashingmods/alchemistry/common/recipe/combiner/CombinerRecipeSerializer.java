package com.smashingmods.alchemistry.common.recipe.combiner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.LinkedHashSet;
import java.util.Set;

public class CombinerRecipeSerializer<T extends CombinerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

        String group = pSerializedRecipe.get("group").getAsString();
        JsonArray inputJson = pSerializedRecipe.getAsJsonArray("input");
        Set<IngredientStack> input = new LinkedHashSet<>();
        ItemStack output;

        inputJson.forEach(element -> input.add(IngredientStack.fromJson(element.getAsJsonObject())));

        if (pSerializedRecipe.get("result").isJsonObject()) {
            output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
        } else {
            output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("item"));
        }
        return this.factory.create(pRecipeId, group, input, output);
    }

    @Override
    public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        int inputCount = pBuffer.readInt();
        Set<IngredientStack> inputList = new LinkedHashSet<>();
        for (int i = 0; i < inputCount; i++) {
            inputList.add(IngredientStack.fromNetwork(pBuffer));
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
        T create(ResourceLocation pId, String pGroup, Set<IngredientStack> pInput, ItemStack pOutput);
    }
}