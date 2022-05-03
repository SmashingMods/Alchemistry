package com.smashingmods.alchemistry.common.recipe.liquifier;

import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import com.smashingmods.alchemistry.datagen.recipe.IngredientStack;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class LiquifierRecipeSerializer<T extends LiquifierRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private IFactory<T> factory;

    public LiquifierRecipeSerializer(LiquifierRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = json.get("group").getAsString();//JSONUtils.getString(json, "group", "");

        JsonElement jsonelement = json.get("ingredient").isJsonArray()
                ? json.getAsJsonArray("ingredient"): json.getAsJsonObject("ingredient");// JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
        Ingredient input = Ingredient.fromJson(jsonelement);
        JsonObject inputObject = json.getAsJsonObject("result");//JSONUtils.getJsonObject(json, "result");
        int inputCount = json.has("inputCount") ? json.get("inputCount").getAsInt() : 1;

        ResourceLocation fluidLocation = new ResourceLocation(inputObject.get("fluid").getAsString());
        int fluidAmount = inputObject.get("amount").getAsInt();
        FluidStack output = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidLocation), fluidAmount);
        return this.factory.create(recipeId, group, new IngredientStack(input, inputCount), output);
    }

    @Override
    public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf(32767);
        IngredientStack input = IngredientStack.fromNetwork(buffer);
        FluidStack output = buffer.readFluidStack();
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        recipe.input.toNetwork(buffer);
        buffer.writeFluidStack(recipe.output);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation id, String group, IngredientStack input, FluidStack output);
    }
}