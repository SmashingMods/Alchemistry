package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.api.item.IngredientStack;
import com.smashingmods.alchemistry.common.recipe.AbstractAlchemistryRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CombinerRecipe extends AbstractAlchemistryRecipe implements Comparable<CombinerRecipe> {

    private final ItemStack output;
    private final List<IngredientStack> input = new ArrayList<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, List<IngredientStack> pInputList, ItemStack pOutput) {
        super(pId, pGroup);
        this.output = pOutput;
        input.addAll(pInputList);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.COMBINER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.COMBINER_TYPE;
    }

    @Override
    public ItemStack assemble(Inventory pContainer) {
        return output;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public int compareTo(@NotNull CombinerRecipe pRecipe) {
        Objects.requireNonNull(this.output.getItem().getRegistryName());
        Objects.requireNonNull(pRecipe.output.getItem().getRegistryName());
        return this.output.getItem().getRegistryName().compareNamespaced(pRecipe.output.getItem().getRegistryName());
    }

    @Override
    public String toString() {
        return String.format("input=[%s],output=[%s]", input, output);
    }

    public List<IngredientStack> getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public boolean matchInputs(List<ItemStack> pStacks) {
        List<ItemStack> inputStacks = pStacks.stream().filter(itemStack -> !itemStack.isEmpty()).toList();

        // Iterate over all recipe input IngredientStacks to make sure that all match the contents of the ItemStack input list.
        // Each ingredient must match to *any* of the input item stacks, have an equal or greater count, and both lists must be the same size.

        return input.stream().allMatch(ingredientStack -> inputStacks.stream()
                        .anyMatch(itemStack -> itemStack.getCount() >= ingredientStack.getCount() && ingredientStack.matches(itemStack)))
                && input.size() == inputStacks.size();
    }
}
