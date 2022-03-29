package al132.alchemistry.blocks.dissolver;

import al132.alchemistry.misc.ProbabilityGroup;
import al132.alchemistry.misc.ProbabilitySet;
import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.IngredientStack;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class DissolverRecipeSerializer<T extends DissolverRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private IFactory<T> factory;

    public DissolverRecipeSerializer(DissolverRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = json.get("group").getAsString();//.getString(json, "group", "");
        JsonElement jsonelement = (JsonElement) (json.get("input").isJsonArray()
                ? json.getAsJsonArray("input")
                : json.getAsJsonObject("input"));
        Ingredient input = Ingredient.fromJson(jsonelement);
        int inputCount = json.has("inputCount") ? json.get("inputCount").getAsInt() : 1;

        JsonObject outputJson = json.getAsJsonObject("output");//.getJsonObject(json, "output");
        int rolls = outputJson.get("rolls").getAsInt();//JSONUtils.getInt(outputJson, "rolls");
        boolean relativeProbability = outputJson.get("relativeProbability").getAsBoolean();//JSONUtils.getBoolean(outputJson, "relativeProbability");

        List<ProbabilityGroup> groups = Lists.newArrayList();
        JsonArray groupJSON = outputJson.getAsJsonArray("groups");//JSONUtils.getJsonArray(outputJson,"groups");
        for (JsonElement e : groupJSON) {
            List<ItemStack> outputs = Lists.newArrayList();
            JsonObject temp = e.getAsJsonObject();
            for (JsonElement stack : temp.getAsJsonArray("stacks")) {
                try {
                    outputs.add(ShapedRecipe.itemStackFromJson(stack.getAsJsonObject()));
                } catch (JsonSyntaxException jse) {
                    outputs.add(new ItemStack(Items.AIR));
                }
            }
            double probability = temp.get("probability").getAsFloat();//JSONUtils.getFloat(temp, "probability");
            groups.add(new ProbabilityGroup(outputs, probability));
        }
        ProbabilitySet output = new ProbabilitySet(groups, relativeProbability, rolls);

        return this.factory.create(recipeId, group, new IngredientStack(input, inputCount), output);
    }

    @Override
    public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf(32767);
        IngredientStack input = IngredientStack.read(buffer);
        ProbabilitySet output = ProbabilitySet.read(buffer);
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        recipe.inputIngredient.write(buffer);
        recipe.outputs.write(buffer);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation id, String group, IngredientStack input, ProbabilitySet output);
    }
}