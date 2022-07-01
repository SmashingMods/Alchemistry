package com.smashingmods.alchemistry.datagen.recipe.fusion;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.common.items.ElementItem;
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

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class FusionRecipeBuilder implements RecipeBuilder {

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

    @Override
    @Nonnull
    public RecipeBuilder unlockedBy(@Nonnull String pCriterionName, @Nonnull CriterionTriggerInstance pCriterionTrigger) {
        Objects.requireNonNull(output.getItem().getRegistryName());

        advancementBuilder.addCriterion(pCriterionName, pCriterionTrigger)
                .rewards(AdvancementRewards.Builder.recipe(new ResourceLocation(Alchemistry.MODID, output.getItem().getRegistryName().getPath())))
                .requirements(RequirementsStrategy.OR);
        return this;
    }

    @Override
    @Nonnull
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    @Nonnull
    public Item getResult() {
        return output.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {

        Objects.requireNonNull(input1.getRegistryName());
        Objects.requireNonNull(input2.getRegistryName());
        Objects.requireNonNull(output.getItem().getRegistryName());

        String input1String = String.format("%s%s", input1.getAtomicNumber(), input1.getRegistryName().getPath());
        String input2String = String.format("%s%s", input2.getAtomicNumber(), input2.getRegistryName().getPath());
        String outputString = String.format("%s%s", ((ElementItem) output.getItem()).getAtomicNumber(), output.getItem().getRegistryName().getPath());

        ResourceLocation recipeId = new ResourceLocation(Alchemistry.MODID, String.format("fusion/%s_and_%s_to_%s", input1String, input2String, outputString));
        ResourceLocation advancementId = new ResourceLocation(Alchemistry.MODID, String.format("recipes/fusion/%s", pRecipeId.getPath()));

        pFinishedRecipeConsumer.accept(new FusionRecipeResult(
                group,
                advancementBuilder,
                recipeId,
                advancementId,
                input1,
                input2,
                output
        ));
    }
}
