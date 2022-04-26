package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.datagen.recipe.RecipeBuilder;
import com.smashingmods.alchemistry.common.recipe.ProbabilitySet;
import com.smashingmods.alchemistry.datagen.recipe.IngredientStack;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class DissolverRecipeBuilder implements RecipeBuilder {

    private final String group = "minecraft:misc";
    private final String name;
    private final IngredientStack input;
    private final ProbabilitySet output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public DissolverRecipeBuilder(IngredientStack input, ProbabilitySet output, String name) {
        this.input = input;
        this.output = output;
        this.name = name;
    }

    public static DissolverRecipeBuilder recipe(IngredientStack input, ProbabilitySet output, String name) {
        return new DissolverRecipeBuilder(input, output, name);
    }

    public static DissolverRecipeBuilder recipe(Ingredient input, int count, ProbabilitySet output, String name) {
        return recipe(new IngredientStack(input, count), output, name);
    }

    public static DissolverRecipeBuilder recipe(Ingredient input, ProbabilitySet output, String name) {
        return recipe(new IngredientStack(input, 1), output, name);
    }


    public void build(Consumer<FinishedRecipe> consumer) {
        this.build(consumer, new ResourceLocation(Alchemistry.MODID, "dissolver/" + name));
    }

    @Override
    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumerIn.accept(new DissolverRecipeResult
                (id, this.group, this.input, this.output,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + "dissolver" + "/" + id.getPath())));

    }

    @Override
    public void validate(ResourceLocation id) {

    }
}
