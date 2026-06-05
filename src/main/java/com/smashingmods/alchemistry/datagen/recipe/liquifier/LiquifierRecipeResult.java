package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.datagen.DatagenHelpers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

public class LiquifierRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation id;
    private final ResourceLocation advancementId;
    private final IngredientStack input;
    private final FluidStack result;

    public LiquifierRecipeResult(String pGroup,
                                 Advancement.Builder pBuilder,
                                 ResourceLocation pId,
                                 ResourceLocation pAdvancementId,
                                 IngredientStack pInput,
                                 FluidStack pResult) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.id = pId;
        this.advancementId = pAdvancementId;
        this.input = pInput;
        this.result = pResult;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        pJson.addProperty("id", id.toString());
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        pJson.add("input", input.toJson());
        DatagenHelpers.fluidStacktoJson(pJson, "result", result);
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public RecipeSerializer<?> type() {
        return RecipeRegistry.LIQUIFIER_SERIALIZER.get();
    }

    @Nullable
    @Override
    public AdvancementHolder advancement() {
        return advancementBuilder.build(advancementId);
    }
}
