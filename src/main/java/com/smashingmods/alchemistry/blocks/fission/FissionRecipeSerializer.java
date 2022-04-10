package com.smashingmods.alchemistry.blocks.fission;

import com.smashingmods.alchemistry.misc.ProcessingRecipe;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;


public class FissionRecipeSerializer<T extends FissionRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private IFactory<T> factory;

    public FissionRecipeSerializer(FissionRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
        String s = json.get("group").getAsString();//"group", "");
        int input = json.get("input").getAsInt();//.getInt(json,"input");
        int output= json.get("output").getAsInt();//JSONUtils.getInt(json,"output");
        int output2 = json.get("output2").getAsInt();//.readNBT(json,"output2");

        return this.factory.create(recipeId, s, input, output, output2);

    }

    @Override
    public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf(32767);
        int input = buffer.readInt();
        int output = buffer.readInt();
        int output2 = buffer.readInt();
        return this.factory.create(recipeId, group, input, output, output2);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeInt(recipe.input);
        buffer.writeInt(recipe.output1);
        buffer.writeInt(recipe.output2);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation resource, String group, int input1, int output, int output2);
    }
}