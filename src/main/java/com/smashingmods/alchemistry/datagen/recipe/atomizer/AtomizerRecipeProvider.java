package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.datagen.recipe.RecipeUtils;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Consumer;

public class AtomizerRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public AtomizerRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new AtomizerRecipeProvider(pConsumer).register();
    }

    private void register() {
        ItemRegistry.getElements().stream().filter(element -> element.getMatterState().equals(MatterState.LIQUID) || element.getMatterState().equals(MatterState.GAS) && !element.isArtificial()).forEach(chemicalToFluidRecipe());
        ItemRegistry.getCompounds().stream().filter(compound -> compound.getMatterState().equals(MatterState.LIQUID) || compound.getMatterState().equals(MatterState.GAS)).forEach(chemicalToFluidRecipe());
        ItemRegistry.getCompoundByName("water").ifPresent(water -> atomizer(new FluidStack(Fluids.WATER, 500), new ItemStack(water, 8)));
    }

    private void atomizer(FluidStack pInput, ItemStack pOutput) {
        AtomizerRecipeBuilder.createRecipe(pInput, pOutput)
                .group(Alchemistry.MODID + ":atomizer")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(RecipeUtils.getLocation(pOutput, "atomizer")))
                .save(consumer);
    }

    private Consumer<? super Chemical> chemicalToFluidRecipe() {
        return chemical -> FluidRegistry.FLUIDS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(fluid -> Objects.requireNonNull(fluid.getRegistryName())
                        .getPath()
                        .contains(chemical.getChemicalName()))
                .findFirst()
                .map(fluid -> new FluidStack(fluid, 500))
                .ifPresent(fluidStack -> atomizer(fluidStack, new ItemStack(chemical, 8)));
    }
}
