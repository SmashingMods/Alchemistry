package com.smashingmods.alchemistry.utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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

    public void write(FriendlyByteBuf buf) {
        ingredient.toNetwork(buf);
        buf.writeInt(count);
    }

    public static IngredientStack read(FriendlyByteBuf buf) {
        Ingredient ing = Ingredient.fromNetwork(buf);
        int count = buf.readInt();
        return new IngredientStack(ing, count);
    }

    public List<ItemStack> toStacks() {
        return Arrays.stream(ingredient.getItems())
                .map(x -> {
                    x.setCount(this.count);
                    return x;
                }).collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return "ingredient=" + ingredient + "\tcount=" + count;
    }
}
