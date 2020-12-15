package al132.alchemistry.blocks.fission;

import al132.alchemistry.misc.ProcessingRecipe;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FissionRecipeSerializer<T extends FissionRecipe>
        extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    private IFactory<T> factory;

    public FissionRecipeSerializer(FissionRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
        String s = JSONUtils.getString(json, "group", "");
        int input = JSONUtils.getInt(json,"input");
        int output= JSONUtils.getInt(json,"output");
        int output2 = JSONUtils.getInt(json,"output2");

        return this.factory.create(recipeId, s, input, output, output2);

    }

    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
        String group = buffer.readString(32767);
        int input = buffer.readInt();
        int output = buffer.readInt();
        int output2 = buffer.readInt();
        return this.factory.create(recipeId, group, input, output, output2);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        buffer.writeString(recipe.getGroup());
        buffer.writeInt(recipe.input);
        buffer.writeInt(recipe.output1);
        buffer.writeInt(recipe.output2);
    }

    public interface IFactory<T extends ProcessingRecipe> {
        T create(ResourceLocation resource, String group, int input1, int output, int output2);
    }
}