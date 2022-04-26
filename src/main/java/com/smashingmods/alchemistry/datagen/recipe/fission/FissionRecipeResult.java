package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.registry.SerializerRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import javax.annotation.Nonnull;

public class FissionRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation id;
    private final ResourceLocation advancementId;
    private final int input;

    public FissionRecipeResult(String pGroup,
                               Advancement.Builder pBuilder,
                               ResourceLocation pId,
                               ResourceLocation pAdvancementId,
                               int pInput) {
        this.id = pId;
        this.group = pGroup;
        this.input = pInput;
        this.advancementBuilder = pBuilder;
        this.advancementId = pAdvancementId;
    }

    @Override
    public void serializeRecipeData(@Nonnull JsonObject pJson) {
        if (!this.group.isEmpty()) {
            pJson.addProperty("group", this.group);
        }
        pJson.addProperty("input", input);

        if (input % 2 == 0) {
            pJson.addProperty("output", input / 2);
            pJson.addProperty("output2", input / 2);
        } else {
            pJson.addProperty("output", (input / 2) + 1);
            pJson.addProperty("output2", input / 2);
        }
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getType() {
        return SerializerRegistry.FISSION_SERIALIZER.get();
    }

    @Override
    public JsonObject serializeAdvancement() {
        return advancementBuilder.serializeToJson();
    }

    @Override
    public ResourceLocation getAdvancementId() {
        return advancementId;
    }
}