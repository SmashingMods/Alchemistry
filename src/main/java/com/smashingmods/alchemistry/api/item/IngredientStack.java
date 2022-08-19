package com.smashingmods.alchemistry.api.item;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientStack {

    private final Ingredient ingredient;
    private final int count;

    public IngredientStack(Ingredient pIngredient, int pCount) {
        this.ingredient = pIngredient;
        this.count = pCount;
    }

    public IngredientStack(Ingredient pIngredient) {
        this(pIngredient, 1);
    }

    public IngredientStack(ItemStack pItemStack) {
        this(Ingredient.of(pItemStack.getItem()), pItemStack.getCount());
    }

    public IngredientStack(ItemStack pItemStack, int pCount) {
        this(Ingredient.of(pItemStack.getItem()), pCount);
    }

    public IngredientStack(ItemLike pItemLike, int pCount) {
        this(Ingredient.of(pItemLike), pCount);
    }

    public IngredientStack(ItemLike pItemLike) {
        this(Ingredient.of(pItemLike));
    }

    public void toNetwork(FriendlyByteBuf pBuffer) {
        ingredient.toNetwork(pBuffer);
        pBuffer.writeInt(count);
    }

    public static IngredientStack fromNetwork(FriendlyByteBuf pBuffer) {
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        int count = pBuffer.readInt();
        return new IngredientStack(ingredient, count);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("count", count);
        return json;
    }

    public static IngredientStack fromJson(JsonObject pJson) {
        Ingredient ingredient = Ingredient.fromJson(pJson.getAsJsonObject("ingredient"));
        int count = GsonHelper.getAsInt(pJson, "count", 1);
        return new IngredientStack(ingredient, count);
    }

    public List<ItemStack> toStacks() {
        return Arrays.stream(ingredient.getItems())
                .peek(item -> item.setCount(count))
                .collect(Collectors.toList());
    }

    public boolean matches(ItemStack pItemStack) {
        return ingredient.test(pItemStack);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return ingredient.isEmpty();
    }
}
