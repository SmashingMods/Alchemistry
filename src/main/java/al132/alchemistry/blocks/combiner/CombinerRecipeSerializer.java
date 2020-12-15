package al132.alchemistry.blocks.combiner;

import al132.alchemistry.misc.ProcessingRecipe;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class CombinerRecipeSerializer<T extends CombinerRecipe>
        extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    private IFactory<T> factory;

    public CombinerRecipeSerializer(CombinerRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }


    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String group = JSONUtils.getString(json, "group", "");
        List<ItemStack> input = Lists.newArrayList();
        JsonArray inputJson = JSONUtils.getJsonArray(json, "input");
        inputJson.forEach(entry -> {
            JsonObject obj = entry.getAsJsonObject();
            Item item = JSONUtils.getItem(obj, "item");
            int count = JSONUtils.hasField(obj, "count") ? JSONUtils.getInt(obj, "count") : 1;
            input.add(new ItemStack(item, count));
        });
        ItemStack output = ItemStack.EMPTY;
        if (json.get("result").isJsonObject()) {
            JsonObject obj = JSONUtils.getJsonObject(json, "result");
            Item item = JSONUtils.getItem(obj, "item");
            int count = JSONUtils.hasField(obj, "count") ? JSONUtils.getInt(obj, "count") : 1;
            output = new ItemStack(item, count);
        } else {
            Item item = JSONUtils.getItem(json, "result");
            //ResourceLocation resourcelocation = new ResourceLocation(s1);
            output = new ItemStack(item);//Registry.ITEM.getOrDefault(resourcelocation));
        }
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        String group = buffer.readString(32767);
        List<ItemStack> input = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            input.add(buffer.readItemStack());
        }
        ItemStack output = buffer.readItemStack();
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeString(recipe.getGroup());
        for (int i = 0; i < 9; i++) {
            buffer.writeItemStack(recipe.inputs.get(i));
        }
        buffer.writeItemStack(recipe.output);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation id, String group, List<ItemStack> input, ItemStack output);
    }
}