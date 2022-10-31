package com.smashingmods.alchemistry.common.recipe.combiner;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CombinerRecipe extends AbstractProcessingRecipe {

    private final ItemStack output;
    private final Set<IngredientStack> input = new LinkedHashSet<>();

    public CombinerRecipe(ResourceLocation pId, String pGroup, Set<IngredientStack> pInputList, ItemStack pOutput) {
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
        return RecipeRegistry.COMBINER_TYPE.get();
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
    public String toString() {
        return String.format("input=[%s],output=[%s]", input, output);
    }

    @Override
    public int compareTo(@NotNull AbstractProcessingRecipe pRecipe) {
        return getId().compareTo(pRecipe.getId());
    }

    @Override
    public CombinerRecipe copy() {
        return new CombinerRecipe(getId(), getGroup(), Set.copyOf(input), output.copy());
    }

    public List<IngredientStack> getInput() {
        return new LinkedList<>(input);
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
