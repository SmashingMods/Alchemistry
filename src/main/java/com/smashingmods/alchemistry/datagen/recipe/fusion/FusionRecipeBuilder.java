package com.smashingmods.alchemistry.datagen.recipe.fusion;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.chemlib.common.items.ElementItem;
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

public class FusionRecipeBuilder {

    @Nullable
    private String group;
    private final ElementItem input1;
    private final ElementItem input2;
    private final ItemStack output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public FusionRecipeBuilder(ElementItem pInput1, ElementItem pInput2, ElementItem pOutput) {
        this.input1 = pInput1;
        this.input2 = pInput2;
        this.output = new ItemStack(pOutput);
    }

    public static FusionRecipeBuilder createRecipe(ElementItem pInput1, ElementItem pInput2, ElementItem pOutput) {
        return new FusionRecipeBuilder(pInput1, pInput2, pOutput);
    }

    public FusionRecipeBuilder unlockedBy(String pCriterionName, Criterion<?> pCriterionTrigger) {
        this.advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public FusionRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public void save(RecipeOutput pOutput) {
        String input1String = String.format("%s%s", input1.getAtomicNumber(), Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(input1)).getPath());
        String input2String = String.format("%s%s", input2.getAtomicNumber(), Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(input2)).getPath());
        String outputString = String.format("%s%s", ((ElementItem) output.getItem()).getAtomicNumber(), Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(output.getItem())).getPath());

        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("fusion/%s_and_%s_to_%s", input1String, input2String, outputString));
        save(pOutput, recipeId);
    }

    public void save(RecipeOutput pOutput, ResourceLocation pId) {
        advancementBuilder
                .rewards(AdvancementRewards.Builder.recipe(pId))
                .requirements(AdvancementRequirements.Strategy.OR);

        AdvancementHolder advHolder = advancementBuilder.build(pId.withPrefix("recipes/fusion/"));

        FusionRecipe recipe = new FusionRecipe(group == null ? "fusion" : group, new ItemStack(input1), new ItemStack(input2), output);
        pOutput.accept(pId, recipe, advHolder);
    }
}
