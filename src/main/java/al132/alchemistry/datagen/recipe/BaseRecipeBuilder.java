package al132.alchemistry.datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;


public abstract class BaseRecipeBuilder {

    public abstract void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id);

    abstract void validate(ResourceLocation id);
}
