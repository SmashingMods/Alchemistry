package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FissionRecipeBuilder {

    @Nullable
    private String group;
    private final ElementItem input;
    private final ItemStack output1;
    private final ItemStack output2;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public FissionRecipeBuilder(ElementItem pInput) {
        this.input = pInput;
        int inputAtomicNumber = pInput.getAtomicNumber();
        if (inputAtomicNumber % 2 == 0) {
            this.output1 = ItemRegistry.getElementByAtomicNumber(inputAtomicNumber / 2).map(ItemStack::new).orElse(ItemStack.EMPTY);
        } else {
            this.output1 = ItemRegistry.getElementByAtomicNumber((inputAtomicNumber / 2) + 1).map(ItemStack::new).orElse(ItemStack.EMPTY);
        }
        this.output2 = ItemRegistry.getElementByAtomicNumber(inputAtomicNumber / 2).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    public static FissionRecipeBuilder createRecipe(ElementItem pInput) {
        return new FissionRecipeBuilder(pInput);
    }

    public FissionRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public FissionRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        ResourceLocation itemKey = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(input));
        ResourceLocation recipeLocation = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("fission/%s", itemKey.getPath()));
        save(pOutput, recipeLocation);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/fission/"));

        FissionRecipe recipe = new FissionRecipe(group == null ? "fission" : group, new ItemStack(input), output1, output2);
        pOutput.accept(pId, recipe, advHolder);
    }
}
