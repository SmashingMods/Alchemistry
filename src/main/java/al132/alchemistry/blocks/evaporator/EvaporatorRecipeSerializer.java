package al132.alchemistry.blocks.evaporator;

import al132.alchemistry.misc.ProcessingRecipe;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EvaporatorRecipeSerializer<T extends EvaporatorRecipe>
        extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    private EvaporatorRecipeSerializer.IFactory<T> factory;

    public EvaporatorRecipeSerializer(EvaporatorRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String group = JSONUtils.getString(json, "group", "");
        JsonObject inputObject = JSONUtils.getJsonObject(json, "input");

        ResourceLocation fluidLocation = new ResourceLocation(inputObject.get("fluid").getAsString());
        int fluidAmount = inputObject.get("amount").getAsInt();

        FluidStack inputStack = FluidStack.EMPTY;
        if(ForgeRegistries.FLUIDS.containsKey(fluidLocation)) {
            inputStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidLocation), fluidAmount);
        }
        if (!json.has("result"))
            throw new JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack output;
        if (json.get("result").isJsonObject())
            output = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        else {
            String s1 = JSONUtils.getString(json, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);

            output = new ItemStack(Registry.ITEM.getOrDefault(resourcelocation));
        }
        return this.factory.create(recipeId, group, inputStack, output);
    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        String group = buffer.readString(32767);
        FluidStack input = buffer.readFluidStack();
        ItemStack output = buffer.readItemStack();
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeString(recipe.getGroup());
        buffer.writeFluidStack(recipe.input);
        buffer.writeItemStack(recipe.output);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}