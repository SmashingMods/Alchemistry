package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.datagen.recipe.RecipeBuilder;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class FissionRecipeBuilder implements RecipeBuilder {

    private final int input;
    private final String group;

    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public FissionRecipeBuilder(int pElementNumber) {
        this(pElementNumber, "minecraft:misc");
    }

    public FissionRecipeBuilder(int pElementNumber, String pGroup) {
        this.input = pElementNumber;
        this.group = pGroup;
    }

    public static FissionRecipeBuilder recipe(int input) {
        return new FissionRecipeBuilder(input);
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        String name = Integer.toString(input);
        this.build(consumerIn, new ResourceLocation(Alchemistry.MODID, "fission/" + name));

    }

    @Override
    public void build(Consumer<FinishedRecipe> pConsumer, ResourceLocation pId) {
        String group = this.group == null ? "" : this.group;
        String path = String.format("recipes/%s/%s", input, pId.getPath());
        ResourceLocation advancementId = new ResourceLocation(pId.getNamespace(), path);
        this.validate(pId);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                .rewards(AdvancementRewards.Builder.recipe(pId)).requirements(RequirementsStrategy.OR);
        pConsumer.accept(new FissionRecipeResult
                (group, advancementBuilder, pId, advancementId, input));
    }

    @Override
    public void validate(ResourceLocation id) {

    }
}
