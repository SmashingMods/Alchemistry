package com.smashingmods.alchemistry.common.recipe.fusion;

import com.smashingmods.alchemistry.api.recipe.AbstractProcessingRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

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
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.FUSION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FUSION_TYPE.get();
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
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.of(input1, input2));
    }

    @Override
    public String toString(){
        return String.format("input=%s, outputs=%s", List.of(input1, input2), output);
    }

    @Override
    public int compareTo(@NotNull AbstractProcessingRecipe pRecipe) {
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
