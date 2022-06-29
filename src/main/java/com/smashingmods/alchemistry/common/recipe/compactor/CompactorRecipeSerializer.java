package com.smashingmods.alchemistry.common.recipe.compactor;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.common.recipe.ProcessingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CompactorRecipeSerializer<T extends CompactorRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final CompactorRecipeSerializer.IFactory<T> factory;

    public CompactorRecipeSerializer(CompactorRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    @Nonnull
    public T fromJson(@Nonnull ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        String group = pSerializedRecipe.get("group").getAsString();
        ItemStack input = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("input"));
        ItemStack output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
        return this.factory.create(pRecipeId, group, input, output);
    }

    @Nullable
    @Override
    public T fromNetwork(@Nonnull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        ItemStack input = pBuffer.readItem();
        ItemStack output = pBuffer.readItem();
        return this.factory.create(pRecipeId, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeItem(pRecipe.getInput());
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation pId, String pGroup, ItemStack pInput, ItemStack pOutput);
    }
}
