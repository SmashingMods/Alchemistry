package com.smashingmods.alchemistry.datagen.recipe.liquifier;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

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
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        pJson.add("input", input.toJson());
        DatagenUtil.fluidStacktoJson(pJson, "result", result);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.LIQUIFIER_SERIALIZER.get();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return advancementBuilder.serializeToJson();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return advancementId;
    }
}
