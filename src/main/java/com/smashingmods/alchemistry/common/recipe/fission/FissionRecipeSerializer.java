package com.smashingmods.alchemistry.common.recipe.fission;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class FissionRecipeSerializer<T extends FissionRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final FissionRecipeSerializer.IFactory<T> factory;

    public FissionRecipeSerializer(FissionRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    @Nonnull
    public T fromJson(@Nonnull ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        String group = pSerializedRecipe.get("group").getAsString();
        ItemStack input = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("input"));
        ItemStack output1 = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("output1"));
        ItemStack output2 = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("output2"));
        return this.factory.create(pRecipeId, group, input, output1, output2);
    }

    @Override
    public T fromNetwork(@Nonnull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        ItemStack input = pBuffer.readItem();
        ItemStack output1 = pBuffer.readItem();
        ItemStack output2 = pBuffer.readItem();
        return this.factory.create(pRecipeId, group, input, output1, output2);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeItem(recipe.getInput());
        buffer.writeItem(recipe.getOutput().get(0));
        buffer.writeItem(recipe.getOutput().get(1));
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pgroup, ItemStack pInput, ItemStack pOutput1, ItemStack pOutput2);
    }
}