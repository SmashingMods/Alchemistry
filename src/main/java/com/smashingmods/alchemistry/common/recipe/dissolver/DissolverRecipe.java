package com.smashingmods.alchemistry.common.recipe.dissolver;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

public class DissolverRecipe extends AbstractProcessingRecipe {

    private final IngredientStack input;
    private final ProbabilitySet output;

    public DissolverRecipe(ResourceLocation pId, String pGroup, IngredientStack pInput, ProbabilitySet pOutput) {
        super(pId, pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public DissolverRecipeSerializer<DissolverRecipe> getSerializer() {
        return RecipeRegistry.DISSOLVER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DISSOLVER_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        // NonNullList.of(default, elements...) treats the first arg as the list's default, not an element, so
        // passing only the ingredient yields a size-0 list (JEI then renders an empty input slot). Supply
        // Ingredient.EMPTY as the default and the real ingredient as the single element.
        return NonNullList.of(Ingredient.EMPTY, input.getIngredient());
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, output);
    }

    @Override
    public int compareTo(AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public DissolverRecipe copy() {
        return new DissolverRecipe(getId(), getGroup(), input.copy(), output.copy());
    }

    public IngredientStack getInput() {
        return input;
    }

    public ProbabilitySet getOutput() {
        return output;
    }

    public boolean matches(ItemStack pItemStack) {
        return input.matches(pItemStack);
    }
}