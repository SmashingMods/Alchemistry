package com.smashingmods.alchemistry.common.recipe.compactor;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class CompactorRecipe extends AbstractProcessingRecipe {

    private final IngredientStack input;
    private final ItemStack output;

    public CompactorRecipe(String pGroup, IngredientStack pInput, ItemStack pOutput) {
        super(pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COMPACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.COMPACTOR_TYPE.get();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pProvider) {
        return output;
    }

    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pProvider) {
        return output.copy();
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
    public CompactorRecipe copy() {
        CompactorRecipe c = new CompactorRecipe(getGroup(), input.copy(), output.copy());
        c.setId(getId());
        return c;
    }

    public IngredientStack getInput() { return input; }
    public ItemStack getOutput() { return output; }

    @SuppressWarnings("unused")
    public boolean matches(ItemStack pItemStack) {
        return input.matches(pItemStack);
    }
}
