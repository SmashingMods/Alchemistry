package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe> implements RecipeSerializer<T> {

    private final IFactory<T> factory;

    public AtomizerRecipeSerializer(IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

        String recipeGroup = pSerializedRecipe.has("group") ? pSerializedRecipe.get("group").getAsString() : "atomizer";

        if (!pSerializedRecipe.has("input")) {
            throw new JsonSyntaxException("Missing input, expected to find an object.");
        }

        JsonObject inputObject = pSerializedRecipe.getAsJsonObject("input");
        ResourceLocation fluidLocation = new ResourceLocation(inputObject.get("fluid").getAsString());
        int fluidAmount = inputObject.has("amount") ? inputObject.get("amount").getAsInt() : 1000;
        FluidStack input = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidLocation)), fluidAmount);

        if (!pSerializedRecipe.has("result")) {
            throw new JsonSyntaxException("Missing result, expected to find a string or object.");
        }

        ItemStack output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
        return this.factory.create(pRecipeId, recipeGroup, input, output);
    }

    @Override
    public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String recipeGroup = pBuffer.readUtf(Short.MAX_VALUE);
        FluidStack input = pBuffer.readFluidStack();
        ItemStack output = pBuffer.readItem();
        return this.factory.create(pRecipeId, recipeGroup, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeFluidStack(pRecipe.getInput());
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}
