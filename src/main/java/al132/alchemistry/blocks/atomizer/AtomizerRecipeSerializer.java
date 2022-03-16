package al132.alchemistry.blocks.atomizer;

import al132.alchemistry.misc.ProcessingRecipe;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AtomizerRecipeSerializer<T extends AtomizerRecipe>
        extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private IFactory<T> factory;

    public AtomizerRecipeSerializer(AtomizerRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
        String group = json.get("group").getAsString();//.getString(json, "group", "");
        JsonObject inputObject = json.getAsJsonObject("input");//JSONUtils.getJsonObject(json, "input");

        ResourceLocation fluidLocation = new ResourceLocation(inputObject.get("fluid").getAsString());
        int fluidAmount = inputObject.get("amount").getAsInt();
        FluidStack inputStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidLocation), fluidAmount);
        if (!json.has("result"))
            throw new JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack output;
        if (json.get("result").isJsonObject())
            output = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));//JSONUtils.getJsonObject(json, "result"));
        else {
            String s1 = json.get("result").getAsString();//.getString(json, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);

            output = new ItemStack(Registry.ITEM.get(resourcelocation));
        }
        return this.factory.create(recipeId, group, inputStack, output);
    }

    @Override
    public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf(32767);
        FluidStack input = buffer.readFluidStack();
        ItemStack output = buffer.readItem();
        return this.factory.create(recipeId, group, input, output);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeFluidStack(recipe.input);
        buffer.writeItemStack(recipe.output, true);
    }


    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation resource, String group, FluidStack input, ItemStack output);
    }
}