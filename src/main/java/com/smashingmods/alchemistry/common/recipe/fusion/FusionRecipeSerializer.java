package com.smashingmods.alchemistry.common.recipe.fusion;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FusionRecipeSerializer<T extends FusionRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final FusionRecipeSerializer.IFactory<T> factory;

    public FusionRecipeSerializer(FusionRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    @Nonnull
    public T fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
        String group = pSerializedRecipe.get("group").getAsString();
        ItemStack input1 = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("input1"));
        ItemStack input2 = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("input2"));
        ItemStack output = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("output"));
        return factory.create(pRecipeId, group, input1, input2, output);
    }

    @Nullable
    @Override
    public T fromNetwork(@Nonnull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        ItemStack input1 = pBuffer.readItem();
        ItemStack input2 = pBuffer.readItem();
        ItemStack output = pBuffer.readItem();
        return factory.create(pRecipeId, group, input1, input2, output);
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeItem(pRecipe.getInput1());
        pBuffer.writeItem(pRecipe.getInput2());
        pBuffer.writeItem(pRecipe.getOutput());
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, ItemStack pInput1, ItemStack pInput2, ItemStack pOutput);
    }
}
