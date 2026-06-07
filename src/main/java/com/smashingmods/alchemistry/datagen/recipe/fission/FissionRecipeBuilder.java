package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import javax.annotation.Nullable;

import java.util.Objects;

public class FissionRecipeBuilder implements RecipeBuilder {

    private String group;
    private final ElementItem input;
    private final ItemStack output1;
    private final ItemStack output2;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public FissionRecipeBuilder(ElementItem pInput) {
        this.input = pInput;
        int[] split = fissionSplit(pInput.getAtomicNumber());
        this.output1 = ItemRegistry.getElementByAtomicNumber(split[0]).map(ItemStack::new).orElse(ItemStack.EMPTY);
        this.output2 = ItemRegistry.getElementByAtomicNumber(split[1]).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    /**
     * Splits an input atomic number into the two fission output atomic numbers: an even number halves evenly,
     * while an odd number rounds the first output up so the pair still sums to the input.
     */
    public static int[] fissionSplit(int atomicNumber) {
        int half = atomicNumber / 2;
        return (atomicNumber % 2 == 0) ? new int[]{half, half} : new int[]{half + 1, half};
    }

    public static FissionRecipeBuilder createRecipe(ElementItem pInput) {
        return new FissionRecipeBuilder(pInput);
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(input)).getPath())))
                .requirements(AdvancementRequirements.Strategy.OR);
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
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pRecipeId) {
        ResourceLocation recipeId = new ResourceLocation(Alchemistry.MODID, String.format("fission/%s", pRecipeId.getPath()));
        ResourceLocation advancementId = new ResourceLocation(Alchemistry.MODID, String.format("recipes/fission/%s", pRecipeId.getPath()));

        FissionRecipe recipe = new FissionRecipe(recipeId, group, new ItemStack(input), output1, output2);
        pRecipeOutput.accept(recipeId, recipe, advancementBuilder.build(advancementId));
    }
}
