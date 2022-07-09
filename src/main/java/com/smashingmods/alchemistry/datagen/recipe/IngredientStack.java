package com.smashingmods.alchemistry.datagen.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientStack {

    public final Ingredient ingredient;
    public final int count;

    public IngredientStack(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public IngredientStack(Ingredient ingredient) {
        this(ingredient, 1);
    }

    public static IngredientStack of(ItemLike pItemLike) {
        return new IngredientStack(Ingredient.of(pItemLike));
    }

    public static IngredientStack of(Ingredient pIngredient) {
        return new IngredientStack(pIngredient);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        ingredient.toNetwork(buf);
        buf.writeInt(count);
    }

    public static IngredientStack fromNetwork(FriendlyByteBuf buf) {
        Ingredient ing = Ingredient.fromNetwork(buf);
        int count = buf.readInt();
        return new IngredientStack(ing, count);
    }

    public List<ItemStack> toStacks() {
        return Arrays.stream(ingredient.getItems())
                .peek(itemStack -> itemStack.setCount(this.count))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("ingredient=%s, count=%d", ingredient, count);
    }
}
