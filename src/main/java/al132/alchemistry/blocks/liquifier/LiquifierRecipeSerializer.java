package al132.alchemistry.blocks.liquifier;

import al132.alchemistry.misc.ProcessingRecipe;
import al132.alchemistry.utils.IngredientStack;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class LiquifierRecipeSerializer<T extends LiquifierRecipe>
        extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    private IFactory<T> factory;

    public LiquifierRecipeSerializer(LiquifierRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String group = JSONUtils.getString(json, "group", "");

        JsonElement jsonelement = (JsonElement) (JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
        Ingredient input = Ingredient.deserialize(jsonelement);
        JsonObject inputObject = JSONUtils.getJsonObject(json, "result");
        int inputCount = JSONUtils.hasField(json, "inputCount") ? JSONUtils.getInt(json, "inputCount") : 1;

        ResourceLocation fluidLocation = new ResourceLocation(inputObject.get("fluid").getAsString());
        int fluidAmount = inputObject.get("amount").getAsInt();
        FluidStack output = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidLocation), fluidAmount);
        return this.factory.create(recipeId, group, new IngredientStack(input, inputCount), output);
    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        String group = buffer.readString(32767);
        IngredientStack input = IngredientStack.read(buffer);
        FluidStack output = buffer.readFluidStack();
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeString(recipe.getGroup());
        recipe.input.write(buffer);
        buffer.writeFluidStack(recipe.output);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation id, String group, IngredientStack input, FluidStack output);
    }
}