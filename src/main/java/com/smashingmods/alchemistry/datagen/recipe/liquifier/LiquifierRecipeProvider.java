package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class LiquifierRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public LiquifierRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public void register() {
        ItemRegistry.getElements().stream().filter(element -> element.getMatterState().equals(MatterState.LIQUID) || element.getMatterState().equals(MatterState.GAS) && !element.isArtificial()).forEach(fluidToChemicalRecipe());
        ItemRegistry.getCompounds().stream().filter(compound -> compound.getMatterState().equals(MatterState.LIQUID) || compound.getMatterState().equals(MatterState.GAS)).forEach(fluidToChemicalRecipe());
        liquifier(new ItemStack(ItemRegistry.getCompoundByName("water").get(), 8), new FluidStack(Fluids.WATER, 500));
    }

    private void liquifier(ItemStack pInput, FluidStack pResult) {
        LiquifierRecipeBuilder.createRecipe(pInput, pResult)
                .group(Alchemistry.MODID + ":liquifier")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pResult)))
                .save(consumer);
    }

    private Consumer<? super Chemical> fluidToChemicalRecipe() {
        return chemical -> {
            FluidRegistry.FLUIDS.getEntries().stream()
                    .map(RegistryObject::get)
                    .filter(fluid -> fluid.getRegistryName()
                            .getPath()
                            .contains(chemical.getChemicalName()))
                    .findFirst()
                    .map(fluid -> new FluidStack(fluid, 500))
                    .ifPresent(fluidStack -> liquifier(new ItemStack(chemical, 8), fluidStack));
        };
    }

    private ResourceLocation getLocation(FluidStack pFluidStack) {
        return new ResourceLocation(Alchemistry.MODID, String.format("liquifier/%s", pFluidStack.getFluid().getRegistryName().getPath()));
    }
}
