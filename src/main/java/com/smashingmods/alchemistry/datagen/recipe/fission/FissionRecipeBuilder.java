package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.smashingmods.alchemistry.datagen.recipe.RecipeBuilder;
import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.SerializerRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class FissionRecipeBuilder implements RecipeBuilder {

    private int input;
    private String group = "minecraft:misc";

    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public static FissionRecipeBuilder recipe(int input) {
        return new FissionRecipeBuilder(input);
    }

    public FissionRecipeBuilder(int inputNumber) {
        this.input = inputNumber;
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        String name = Integer.toString(input);
        this.build(consumerIn, new ResourceLocation(Alchemistry.MODID, "fission/" + name));

    }

    @Override
    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumerIn.accept(new FissionRecipeBuilder.Result
                (id, this.group == null ? "" : this.group, this.input,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + this.input + "/" + id.getPath())));


    }

    @Override
    public void validate(ResourceLocation id) {

    }

    public static class Result implements FinishedRecipe {
        private final String group;
        private final ResourceLocation id;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementID;
        private final int input;

        public Result(ResourceLocation id, String group, int input, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.advancementBuilder = advancementBuilder;
            this.advancementID = advancementId;
        }

        @Override
        public void serializeRecipeData(@Nonnull JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            json.addProperty("input", input);
            if (input % 2 == 0) {
                json.addProperty("output", input / 2);
                json.addProperty("output2", input / 2);
            } else {
                json.addProperty("output", (input / 2) + 1);
                json.addProperty("output2", input / 2);
            }

        }

        @Override
        @Nonnull
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        @Nonnull
        public RecipeSerializer<?> getType() {
            return SerializerRegistry.FISSION_SERIALIZER.get();
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
