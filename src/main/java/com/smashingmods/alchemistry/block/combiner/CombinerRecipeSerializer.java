package com.smashingmods.alchemistry.block.combiner;

import com.smashingmods.alchemistry.misc.ProcessingRecipe;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class CombinerRecipeSerializer<T extends CombinerRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private IFactory<T> factory;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }


    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = json.get("group").getAsString();//.getString(json, "group", "");
        List<ItemStack> input = Lists.newArrayList();
        JsonArray inputJson = json.getAsJsonArray("input");//JSONUtils.getJsonArray(json, "input");
        inputJson.forEach(entry -> {
            JsonObject obj = entry.getAsJsonObject();
            Item item = Items.AIR;
            try {
                item = ShapedRecipe.itemFromJson(obj.getAsJsonObject());//json, "item"));// ItemStack.of(JsonUtils.(obj,"item")).getItem();;//.getItem(obj, "item");
            } catch (JsonSyntaxException e) {
                //lol
            }
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            input.add(new ItemStack(item, count));
        });
        ItemStack output = ItemStack.EMPTY;
        if (json.get("result").isJsonObject()) {
            JsonObject obj = json.getAsJsonObject("result");//JSONUtils.getJsonObject(json, "result");
            Item item = ShapedRecipe.itemFromJson(obj.getAsJsonObject());//ItemStack.of(JsonUtils.readNBT(obj,"item")).getItem();//.getItem(obj, "item");
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            output = new ItemStack(item, count);
        } else {

            Item item = ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "item"));//ItemStack.of(JsonUtils.readNBT(json,"result")).getItem();
            //ResourceLocation resourcelocation = new ResourceLocation(s1);
            output = new ItemStack(item);//Registry.ITEM.getOrDefault(resourcelocation));
        }
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf(32767);
        List<ItemStack> input = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            input.add(buffer.readItem());
        }
        ItemStack output = buffer.readItem();
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        for (int i = 0; i < 9; i++) {
            buffer.writeItemStack(recipe.inputs.get(i), true);
        }
        buffer.writeItemStack(recipe.output, true);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation id, String group, List<ItemStack> input, ItemStack output);
    }
}