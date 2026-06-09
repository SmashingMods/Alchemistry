package com.smashingmods.alchemistry.common.recipe.fission;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
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
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return RecipeRegistry.FISSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RecipeRegistry.FISSION_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        // Fission recipes are crafted in the machine, never placed through the vanilla recipe book grid.
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        // These recipes are not shown in the vanilla recipe book; use the catch-all crafting category.
        return RecipeBookCategories.CRAFTING_MISC;
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
