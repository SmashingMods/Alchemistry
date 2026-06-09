package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.FluidRegistry;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.function.Consumer;

import static com.smashingmods.alchemylib.datagen.DatagenHelpers.getLocation;

public class LiquifierRecipeProvider {

    private final RecipeOutput consumer;
    private final HolderGetter<Item> itemLookup;

    public LiquifierRecipeProvider(RecipeOutput pConsumer, HolderGetter<Item> pItemLookup) {
        this.consumer = pConsumer;
        this.itemLookup = pItemLookup;
    }

    public static void register(RecipeOutput pConsumer, HolderGetter<Item> pItemLookup) {
        new LiquifierRecipeProvider(pConsumer, pItemLookup).register();
    }

    private void register() {
        ItemRegistry.getElements().stream().filter(element -> (element.getMatterState().equals(MatterState.LIQUID) || element.getMatterState().equals(MatterState.GAS)) && !element.isArtificial()).forEach(fluidToChemicalRecipe());
        ItemRegistry.getCompounds().stream().filter(compound -> compound.getMatterState().equals(MatterState.LIQUID) || compound.getMatterState().equals(MatterState.GAS)).forEach(fluidToChemicalRecipe());
        ItemRegistry.getCompoundByName("water").ifPresent(water -> liquifier(new ItemStack(water, 8), new FluidStack(Fluids.WATER, 500)));
    }

    private Consumer<? super Chemical> fluidToChemicalRecipe() {
        return chemical -> FluidRegistry.FLUIDS.getEntries().stream()
                .map(DeferredHolder::get)
                .filter(fluid -> Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(fluid))
                        .getPath()
                        .contentEquals(String.format("%s_fluid", chemical.getChemicalName())))
                .findFirst()
                .map(fluid -> new FluidStack(fluid, 500))
                .ifPresent(fluidStack -> liquifier(new ItemStack(chemical, 8), fluidStack));
    }

    @SuppressWarnings("unused")
    private void liquifier(String pItemTag, FluidStack pOutput) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(pItemTag));
        liquifier(new IngredientStack(Ingredient.of(itemLookup.getOrThrow(tagKey))), pOutput);
    }

    private void liquifier(ItemStack pInput, FluidStack pOutput) {
        liquifier(new IngredientStack(pInput), pOutput);
    }

    @SuppressWarnings("unused")
    private void liquifier(IngredientStack pInput, FluidStack pOutput, ICondition pCondition) {
        ResourceLocation recipeId = Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(pOutput.getFluid()));
        LiquifierRecipeBuilder
                .createRecipe(pInput, pOutput, recipeId)
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(ResourceKey.create(Registries.RECIPE, getLocation(pOutput, "liquifier", Alchemistry.MODID))))
                .save(consumer.withConditions(pCondition), ResourceKey.create(Registries.RECIPE, recipeId));
    }

    private void liquifier(IngredientStack pInput, FluidStack pOutput) {
        LiquifierRecipeBuilder.createRecipe(pInput, pOutput, Objects.requireNonNull(BuiltInRegistries.FLUID.getKey(pOutput.getFluid())))
                .group(String.format("%s:liquifier", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(ResourceKey.create(Registries.RECIPE, getLocation(pOutput, "liquifier", Alchemistry.MODID))))
                .save(consumer);
    }
}
