package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.function.Consumer;

import static com.smashingmods.alchemylib.datagen.DatagenHelpers.getLocation;

public class AtomizerRecipeProvider {

    private final RecipeOutput consumer;

    public AtomizerRecipeProvider(RecipeOutput pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(RecipeOutput pConsumer) {
        new AtomizerRecipeProvider(pConsumer).register();
    }

    private void register() {
        ItemRegistry.getElements().stream().filter(element -> (element.getMatterState().equals(MatterState.LIQUID) || element.getMatterState().equals(MatterState.GAS)) && !element.isArtificial()).forEach(chemicalToFluidRecipe());
        ItemRegistry.getCompounds().stream().filter(compound -> compound.getMatterState().equals(MatterState.LIQUID) || compound.getMatterState().equals(MatterState.GAS)).forEach(chemicalToFluidRecipe());
        ItemRegistry.getCompoundByName("water").ifPresent(water -> atomizer(new FluidStack(Fluids.WATER, 500), new ItemStack(water, 8)));
    }

    private Consumer<? super Chemical> chemicalToFluidRecipe() {
        return chemical -> FluidRegistry.FLUIDS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(fluid -> Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(fluid)).getPath().contains(chemical.getChemicalName()))
                .findFirst()
                .map(fluid -> new FluidStack(fluid, 500))
                .ifPresent(fluidStack -> atomizer(fluidStack, new ItemStack(chemical, 8)));
    }

    @SuppressWarnings("unused")
    private void atomizer(FluidStack pInput, ItemStack pOutput, ICondition pCondition) {
        ResourceLocation recipeId = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pOutput.getItem()));
        AtomizerRecipeBuilder.createRecipe(pInput, pOutput, recipeId)
                .group(String.format("%s:atomizer", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput, "atomizer", Alchemistry.MODID)))
                .save(consumer.withConditions(pCondition), recipeId);
    }

    private void atomizer(FluidStack pInput, ItemStack pOutput) {
        ResourceLocation recipeId = BuiltInRegistries.ITEM.getKey(pOutput.getItem());
        AtomizerRecipeBuilder.createRecipe(pInput, pOutput, Objects.requireNonNull(recipeId))
                .group(String.format("%s:atomizer", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput, "atomizer", Alchemistry.MODID)))
                .save(consumer);
    }
}
