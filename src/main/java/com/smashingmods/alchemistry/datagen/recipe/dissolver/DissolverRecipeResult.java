package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import javax.annotation.Nullable;

public class DissolverRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation id;
    private final ResourceLocation advancementId;
    private final IngredientStack input;
    private final ProbabilitySet output;

    public DissolverRecipeResult(String pGroup,
                                 Advancement.Builder pBuilder,
                                 ResourceLocation pId,
                                 ResourceLocation pAdvancementId,
                                 IngredientStack pInput,
                                 ProbabilitySet pOutput) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.id = pId;
        this.advancementId = pAdvancementId;
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        json.addProperty("id", id.toString());
        if (!group.isEmpty()) {
            json.addProperty("group", group);
        }
        json.add("input", input.toJson());
        json.add("output", output.serialize());
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> type() {
        return RecipeRegistry.DISSOLVER_SERIALIZER.get();
    }

    @Nullable
    @Override
    public AdvancementHolder advancement() {
        return advancementBuilder.build(advancementId);
    }
}
