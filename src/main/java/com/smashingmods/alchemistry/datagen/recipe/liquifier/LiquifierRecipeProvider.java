package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.item.IngredientStack;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Consumer;

public class LiquifierRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public LiquifierRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new LiquifierRecipeProvider(pConsumer).register();
    }

    private void register() {
        ItemRegistry.getElements().stream().filter(element -> element.getMatterState().equals(MatterState.LIQUID) || element.getMatterState().equals(MatterState.GAS) && !element.isArtificial()).forEach(fluidToChemicalRecipe());
        ItemRegistry.getCompounds().stream().filter(compound -> compound.getMatterState().equals(MatterState.LIQUID) || compound.getMatterState().equals(MatterState.GAS)).forEach(fluidToChemicalRecipe());
        ItemRegistry.getCompoundByName("water").ifPresent(water -> liquifier(new ItemStack(water, 8), new FluidStack(Fluids.WATER, 500)));
    }

    private void liquifier(String pItemTag, FluidStack pOutput) {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(pItemTag));
        liquifier(new IngredientStack(Ingredient.of(tagKey)), pOutput);
    }

    private void liquifier(ItemStack pInput, FluidStack pOutput) {
        liquifier(new IngredientStack(pInput), pOutput);
    }

    private void liquifier(IngredientStack pInput, FluidStack pOutput) {
        LiquifierRecipeBuilder.createRecipe(pInput, pOutput, Objects.requireNonNull(pOutput.getFluid().getRegistryName()))
                .group(Alchemistry.MODID + ":liquifier")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(DatagenUtil.getLocation(pOutput, "liquifier")))
                .save(consumer);
    }

    private Consumer<? super Chemical> fluidToChemicalRecipe() {
        return chemical -> FluidRegistry.FLUIDS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(fluid -> Objects.requireNonNull(fluid.getRegistryName())
                        .getPath()
                        .contains(chemical.getChemicalName()))
                .findFirst()
                .map(fluid -> new FluidStack(fluid, 500))
                .ifPresent(fluidStack -> liquifier(new ItemStack(chemical, 8), fluidStack));
    }
}
