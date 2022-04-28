package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.common.recipe.ProbabilitySet;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemistry.datagen.recipe.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;

public class DissolverRecipeResult implements FinishedRecipe {

    private final String group;
    private final ResourceLocation id;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementID;
    private final IngredientStack input;
    private final ProbabilitySet output;

    public DissolverRecipeResult(ResourceLocation id,
                                 String group,
                                 IngredientStack input,
                                 ProbabilitySet output,
                                 Advancement.Builder advancementBuilder,
                                 ResourceLocation advancementId) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.advancementBuilder = advancementBuilder;
        this.advancementID = advancementId;
    }

    @Override
    public void serializeRecipeData(@Nonnull JsonObject json) {
        if (!this.group.isEmpty()) json.addProperty("group", this.group);
        json.add("input", input.ingredient.toJson());
        json.add("output", output.serialize());
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.DISSOLVER_SERIALIZER.get();
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
