package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.misc.ProbabilityGroup;
import al132.alchemistry.misc.ProbabilitySet;
import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.IngredientStack;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class DissolverRecipeSerializer<T extends DissolverRecipe>
        extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    private IFactory<T> factory;

    public DissolverRecipeSerializer(DissolverRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String group = JSONUtils.getString(json, "group", "");
        JsonElement jsonelement = (JsonElement) (JSONUtils.isJsonArray(json, "input")
                ? JSONUtils.getJsonArray(json, "input")
                : JSONUtils.getJsonObject(json, "input"));
        Ingredient input = Ingredient.deserialize(jsonelement);
        int inputCount = JSONUtils.hasField(json, "inputCount") ? JSONUtils.getInt(json, "inputCount") : 1;

        JsonObject outputJson = JSONUtils.getJsonObject(json, "output");
        int rolls = JSONUtils.getInt(outputJson, "rolls");
        boolean relativeProbability = JSONUtils.getBoolean(outputJson, "relativeProbability");
        
        List<ProbabilityGroup> groups = Lists.newArrayList();
        JsonArray groupJSON = JSONUtils.getJsonArray(outputJson,"groups");
        for (JsonElement e : groupJSON) {
            List<ItemStack> outputs = Lists.newArrayList();
            JsonObject temp = e.getAsJsonObject();
            for (JsonElement stack : temp.getAsJsonArray("stacks")) {
                outputs.add(ShapedRecipe.deserializeItem(stack.getAsJsonObject()));
            }
            double probability = JSONUtils.getFloat(temp, "probability");
            groups.add(new ProbabilityGroup(outputs, probability));
        }
        ProbabilitySet output = new ProbabilitySet(groups, relativeProbability, rolls);

        return this.factory.create(recipeId, group, new IngredientStack(input, inputCount), output);
    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        String group = buffer.readString(32767);
        IngredientStack input = IngredientStack.read(buffer);
        ProbabilitySet output = ProbabilitySet.read(buffer);
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeString(recipe.getGroup());
        recipe.inputIngredient.write(buffer);
        recipe.outputs.write(buffer);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation id, String group, IngredientStack input, ProbabilitySet output);
    }
}