package com.smashingmods.alchemistry.datagen.recipe;

import com.smashingmods.alchemistry.Registration;
import com.smashingmods.alchemistry.misc.ProbabilitySet;
import com.smashingmods.alchemistry.utils.IngredientStack;
import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.Alchemistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Consumer;

public class DissolverRecipeBuilder extends BaseRecipeBuilder {


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
        consumerIn.accept(new DissolverRecipeBuilder.Result
                (id, this.group == null ? "" : this.group, this.input, this.output,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + "dissolver" + "/" + id.getPath())));

    }

    @Override
    void validate(ResourceLocation id) {

    }

    public static class Result implements FinishedRecipe {
        private final String group;
        private final ResourceLocation id;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementID;
        private final IngredientStack input;
        private final ProbabilitySet output;

        public Result(ResourceLocation id, String group, IngredientStack input, ProbabilitySet output,
                      Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.output = output;
            this.advancementBuilder = advancementBuilder;
            this.advancementID = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            json.add("input", input.ingredient.toJson());
            json.add("output", output.serialize());
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return Registration.DISSOLVER_SERIALIZER.get();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementID;
        }
    }
}
