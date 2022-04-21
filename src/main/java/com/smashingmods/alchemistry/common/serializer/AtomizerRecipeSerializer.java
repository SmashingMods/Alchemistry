package com.smashingmods.alchemistry.common.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.smashingmods.alchemistry.common.recipe.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final AtomizerRecipeSerializer.IFactory<T> factory;

    public AtomizerRecipeSerializer(AtomizerRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    @Nonnull
    public T fromJson(@Nonnull ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

        JsonObject inputObject = pSerializedRecipe.getAsJsonObject("input");
        ItemStack outputStack;
        String recipeGroup = pSerializedRecipe.get("group").getAsString();

        ResourceLocation fluidResourceLocation = new ResourceLocation(inputObject.get("fluid").getAsString());
        int fluidAmount = inputObject.get("amount").getAsInt();
        FluidStack inputFluidStack = new FluidStack(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluidResourceLocation)), fluidAmount);

        if (!pSerializedRecipe.has("result")) {
            throw new JsonSyntaxException("Missing result, expected to find a string or object");
        }

        if (pSerializedRecipe.get("result").isJsonObject())
            outputStack = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
        else {
            String recipeResult = pSerializedRecipe.get("result").getAsString();
            ResourceLocation resourcelocation = new ResourceLocation(recipeResult);
            outputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        return this.factory.create(pRecipeId, recipeGroup, inputFluidStack, outputStack);
    }

    @Override
    public T fromNetwork(@Nonnull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String recipeGroup = pBuffer.readUtf(Short.MAX_VALUE);
        FluidStack inputFluidStack = pBuffer.readFluidStack();
        ItemStack outputStack = pBuffer.readItem();
        return this.factory.create(pRecipeId, recipeGroup, inputFluidStack, outputStack);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeFluidStack(pRecipe.input);
        pBuffer.writeItemStack(pRecipe.output, true);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}
