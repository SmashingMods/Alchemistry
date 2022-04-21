package com.smashingmods.alchemistry.common.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public class ProbabilityGroup {

    private final List<ItemStack> outputs;
    private final double probability;

    public ProbabilityGroup(List<ItemStack> outputs, double probability) {
        this.outputs = outputs;
        this.probability = probability;
    }

    public List<ItemStack> getOutputs() {
        return this.outputs;
    }

    public double getProbability() {
        return this.probability;
    }

    public JsonElement serialize() {
        JsonObject output = new JsonObject();
        output.add("probability", new JsonPrimitive(probability));
        JsonArray stacks = new JsonArray();
        for (ItemStack stack : outputs) {
            JsonObject temp = new JsonObject();
            temp.add("item", new JsonPrimitive(Objects.requireNonNull(stack.getItem().getRegistryName()).toString()));
            if (stack.getCount() > 1) temp.add("count", new JsonPrimitive(stack.getCount()));
            stacks.add(temp);
        }
        output.add("stacks", stacks);
        return output;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(outputs.size());
        for (ItemStack stack : outputs) {
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
