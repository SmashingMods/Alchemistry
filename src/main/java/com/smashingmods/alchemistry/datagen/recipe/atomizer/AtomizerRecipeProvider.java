package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.chemistry.CompoundRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;
import java.util.function.Consumer;

public class AtomizerRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public AtomizerRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public void register() {
        atomizer(new FluidStack(Fluids.WATER, 500),
                new ItemStack(CompoundRegistry.getByName("water").get(), 8));
    }

    public void atomizer(FluidStack pInput, ItemStack pOutput) {
        AtomizerRecipeBuilder.createRecipe(pInput, pOutput)
                .group(Alchemistry.MODID + ":atomizer")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput)))
                .save(consumer);
    }

    private ResourceLocation getLocation(ItemStack itemStack) {
        Objects.requireNonNull(itemStack.getItem().getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("atomizer/%s", itemStack.getItem().getRegistryName().getPath()));
    }
}
