package com.smashingmods.alchemistry.common.recipe.atomizer;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class AtomizerRecipe extends AbstractProcessingRecipe {

    private final FluidStack input;
    private final ItemStack output;

    public AtomizerRecipe(ResourceLocation pId, String pGroup, FluidStack pInput, ItemStack pOutput) {
        super(pId, pGroup);
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ATOMIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ATOMIZER_TYPE.get();
    }

    @Override
    public ItemStack assemble(Inventory pContainer, HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        // NonNullList.of(default, elements...) treats the first arg as the list's default, not an element, so
        // passing only the ingredient yields a size-0 list (JEI then renders an empty input slot). Supply
        // Ingredient.EMPTY as the default and the real ingredient as the single element.
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(input.getFluid().getBucket()));
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, output);
    }

    @Override
    public int compareTo(@Nonnull AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public AtomizerRecipe copy() {
        return new AtomizerRecipe(getId(), getGroup(), input.copy(), output.copy());
    }

    public FluidStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}