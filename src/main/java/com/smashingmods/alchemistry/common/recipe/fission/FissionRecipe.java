package com.smashingmods.alchemistry.common.recipe.fission;

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
import javax.annotation.Nonnull;

import java.util.List;

public class FissionRecipe extends AbstractProcessingRecipe {

    private final ItemStack input;
    private final ItemStack output1;
    private final ItemStack output2;

    public FissionRecipe(ResourceLocation pId, String pGroup, ItemStack pInput, ItemStack pOutput1, ItemStack pOutput2) {
        super(pId, pGroup);
        this.input = pInput;
        this.output1 = pOutput1;
        this.output2 = pOutput2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FISSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FISSION_TYPE.get();
    }

    @Override
    public ItemStack assemble(Inventory pContainer, HolderLookup.Provider pRegistries) {
        return output1;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        // NonNullList.of(default, elements...) treats the first arg as the list's default, not an element, so
        // passing only the ingredient yields a size-0 list (JEI then renders an empty input slot). Supply
        // Ingredient.EMPTY as the default and the real ingredient as the single element.
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(input));
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, List.of(output1, output2));
    }

    @Override
    public int compareTo(@Nonnull AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public FissionRecipe copy() {
        return new FissionRecipe(getId(), getGroup(), input.copy(), output1.copy(), output2.copy());
    }

    public ItemStack getInput() {
        return input;
    }

    @Override
    public Object getOutput() {
        return List.of(output1, output2);
    }

    public ItemStack getOutput1() {
        return output1;
    }

    public ItemStack getOutput2() {
        return output2;
    }
}
