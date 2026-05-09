package com.smashingmods.alchemistry.common.recipe.liquifier;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class LiquifierRecipe extends AbstractProcessingRecipe {

    private final IngredientStack input;
    private final FluidStack output;

    public LiquifierRecipe(String pGroup, IngredientStack pInput, FluidStack pOutput) {
        super(pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.LIQUIFIER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.LIQUIFIER_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(input.getIngredient());
    }

    @Override
    public String toString() {
        return String.format("input=%s, outputs=%s", input, output);
    }

    @Override
    public int compareTo(@NotNull AbstractProcessingRecipe pRecipe) {
        return AbstractProcessingRecipe.compareIds(getId(), pRecipe.getId());
    }

    @Override
    public LiquifierRecipe copy() {
        LiquifierRecipe c = new LiquifierRecipe(getGroup(), input.copy(), output.copy());
        c.setId(getId());
        return c;
    }

    public IngredientStack getInput() { return input; }
    public FluidStack getOutput() { return output; }
}
