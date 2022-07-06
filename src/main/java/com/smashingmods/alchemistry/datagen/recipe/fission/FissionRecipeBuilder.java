package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class FissionRecipeBuilder implements RecipeBuilder {

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

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {

        Objects.requireNonNull(input.getRegistryName());

        advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, input.getRegistryName().getPath())))
                .requirements(RequirementsStrategy.OR);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return input;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        ResourceLocation recipeId = new ResourceLocation(Alchemistry.MODID, String.format("fission/%s", pRecipeId.getPath()));
        ResourceLocation advancementId = new ResourceLocation(Alchemistry.MODID, String.format("recipes/fission/%s", pRecipeId.getPath()));

        pFinishedRecipeConsumer.accept(new FissionRecipeResult(
                group,
                advancementBuilder,
                recipeId,
                advancementId,
                input,
                output1,
                output2
        ));
    }
}
