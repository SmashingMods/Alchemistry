package com.smashingmods.alchemistry.common.recipe.fusion;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.HolderLookup;
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

public class FusionRecipe extends AbstractProcessingRecipe {

    private final ItemStack input1;
    private final ItemStack input2;
    private final ItemStack output;

    public FusionRecipe(ResourceLocation pId, String pGroup, ItemStack pInput1, ItemStack pInput2, ItemStack pOutput) {
        super(pId, pGroup);
        this.input1 = pInput1;
        this.input2 = pInput2;
        this.output = pOutput;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return RecipeRegistry.FUSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return RecipeRegistry.FUSION_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        // Fusion recipes are crafted in the machine, never placed through the vanilla recipe book grid.
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        // These recipes are not shown in the vanilla recipe book; use the catch-all crafting category.
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", List.of(input1, input2), output);
    }

    @Override
    public int compareTo(@Nonnull AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public FusionRecipe copy() {
        return new FusionRecipe(getId(), getGroup(), input1.copy(), input2.copy(), output.copy());
    }

    @Override
    public Object getInput() {
        return List.of(input1, input2);
    }

    public ItemStack getInput1() {
        return input1;
    }

    public ItemStack getInput2() {
        return input2;
    }

    public ItemStack getOutput() {
        return output;
    }
}
