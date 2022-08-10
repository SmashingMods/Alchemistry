package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public class ProbabilityGroup {

    private final List<ItemStack> output;
    private final double probability;

    public ProbabilityGroup(List<ItemStack> pOutput, double pProbability) {
        this.output = pOutput;
        this.probability = pProbability;
    }

    public ProbabilityGroup(List<ItemStack> pOutput) {
        this.output = pOutput;
        this.probability = 1;
    }

    public List<ItemStack> getOutput() {
        return this.output;
    }

    public double getProbability() {
        return this.probability;
    }

    public JsonElement serialize() {
        JsonObject output = new JsonObject();
        output.add("probability", new JsonPrimitive(probability));
        JsonArray results = new JsonArray();

        for (ItemStack itemStack : this.output) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("item", new JsonPrimitive(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemStack.getItem())).toString()));

            if (itemStack.getCount() > 1) {
                jsonObject.add("count", new JsonPrimitive(itemStack.getCount()));
            }
            results.add(jsonObject);
        }
        output.add("results", results);
        return output;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(output.size());
        for (ItemStack stack : output) {
            buf.writeItemStack(stack, false);
        }
        buf.writeDouble(probability);
    }

    public static ProbabilityGroup read(FriendlyByteBuf buf) {
        List<ItemStack> stacks = Lists.newArrayList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            stacks.add(buf.readItem());
        }
        double probability = buf.readDouble();
        return new ProbabilityGroup(stacks, probability);
    }
}
