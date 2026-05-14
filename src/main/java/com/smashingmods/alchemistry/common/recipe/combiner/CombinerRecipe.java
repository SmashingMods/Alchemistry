package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CombinerRecipe extends AbstractProcessingRecipe {

    private final ItemStack output;
    private final List<IngredientStack> input;

    public CombinerRecipe(String pGroup, List<IngredientStack> pInputList, ItemStack pOutput) {
        super(pGroup);
        this.output = pOutput;
        this.input = new ArrayList<>(pInputList);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COMBINER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.COMBINER_TYPE.get();
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
    public String toString() {
        return String.format("input=[%s],output=[%s]", input, output);
    }

    @Override
    public int compareTo(@NotNull AbstractProcessingRecipe pRecipe) {
        return AbstractProcessingRecipe.compareIds(getId(), pRecipe.getId());
    }

    @Override
    public CombinerRecipe copy() {
        CombinerRecipe c = new CombinerRecipe(getGroup(), List.copyOf(input), output.copy());
        c.setId(getId());
        return c;
    }

    public List<IngredientStack> getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean matchInputs(List<ItemStack> pStacks) {
        List<ItemStack> inputStacks = pStacks.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        return input.stream().allMatch(ingredientStack -> inputStacks.stream()
                        .anyMatch(itemStack -> itemStack.getCount() >= ingredientStack.getCount() && ingredientStack.matches(itemStack)))
                && input.size() == inputStacks.size();
    }
}
