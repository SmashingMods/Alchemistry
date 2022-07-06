package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class DissolverRecipeSerializer<T extends DissolverRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final DissolverRecipeSerializer.IFactory<T> factory;

    public DissolverRecipeSerializer(DissolverRecipeSerializer.IFactory<T> pFactory) {
        this.factory = pFactory;
    }

    @Override
    public T fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {

        String group = pSerializedRecipe.get("group").getAsString();
        ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));

        JsonObject outputJson = pSerializedRecipe.getAsJsonObject("output");
        int rolls = outputJson.get("rolls").getAsInt();
        boolean relativeProbability = outputJson.get("relativeProbability").getAsBoolean();
        List<ProbabilityGroup> groups = new ArrayList<>();
        JsonArray groupArray = outputJson.getAsJsonArray("groups");

        for (JsonElement element : groupArray) {
            List<ItemStack> output = new ArrayList<>();
            JsonObject jsonObject = element.getAsJsonObject();

            for (JsonElement stack : jsonObject.getAsJsonArray("results")) {
                try {
                    output.add(ShapedRecipe.itemStackFromJson(stack.getAsJsonObject()));
                } catch (JsonSyntaxException exception) {
                    exception.printStackTrace();
                }
            }
            double probability = jsonObject.get("probability").getAsFloat();
            groups.add(new ProbabilityGroup(output, probability));
        }
        ProbabilitySet output = new ProbabilitySet(groups, relativeProbability, rolls);

        return this.factory.create(pRecipeId, group, input, output);
    }

    @Override
    public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        String group = pBuffer.readUtf(Short.MAX_VALUE);
        ItemStack input = pBuffer.readItem();
        ProbabilitySet output = ProbabilitySet.read(pBuffer);
        return this.factory.create(pRecipeId, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, T pRecipe) {
        pBuffer.writeUtf(pRecipe.getGroup());
        pBuffer.writeItemStack(pRecipe.getInput(), true);
        pRecipe.getOutput().write(pBuffer);
    }

    public interface IFactory<T extends Recipe<Inventory>> {
        T create(ResourceLocation pId, String pGroup, ItemStack pInput, ProbabilitySet pOutput);
    }
}