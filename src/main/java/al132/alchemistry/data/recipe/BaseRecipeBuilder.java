package al132.alchemistry.data.recipe;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public abstract class BaseRecipeBuilder {

    public abstract void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id);

    abstract void validate(ResourceLocation id);
}
