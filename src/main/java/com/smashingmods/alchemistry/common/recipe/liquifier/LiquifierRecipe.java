package com.smashingmods.alchemistry.common.recipe.liquifier;

import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class LiquifierRecipe extends AbstractProcessingRecipe {

    private final IngredientStack input;
    private final FluidStack output;

    public LiquifierRecipe(ResourceLocation pId, String pGroup, IngredientStack pInput, FluidStack pOutput) {
        super(pId, pGroup);
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
    public boolean matches(Inventory pContainer, Level pLevel) {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(input.getIngredient());
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", input, output);
    }

    @Override
    public int compareTo(@NotNull AbstractProcessingRecipe pRecipe) {
        return getId().compareNamespaced(pRecipe.getId());
    }

    @Override
    public LiquifierRecipe copy() {
        return new LiquifierRecipe(getId(), getGroup(), input.copy(), output.copy());
    }

    public IngredientStack getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }
}