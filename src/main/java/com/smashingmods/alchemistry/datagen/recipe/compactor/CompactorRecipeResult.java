package com.smashingmods.alchemistry.datagen.recipe.compactor;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CompactorRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation recipeId;
    private final ResourceLocation advancementId;
    private final ItemStack input;
    private final ItemStack result;

    public CompactorRecipeResult(String pGroup,
                                 Advancement.Builder pBuilder,
                                 ResourceLocation pRecipeId,
                                 ResourceLocation pAdvancementId,
                                 ItemStack pInput,
                                 ItemStack pResult) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.recipeId = pRecipeId;
        this.advancementId = pAdvancementId;
        this.input = pInput;
        this.result = pResult;
    }

    @Override
    @Nonnull
    public void serializeRecipeData(@Nonnull JsonObject pJson) {
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        DatagenUtil.itemStackToJson(pJson, "input", input);
        DatagenUtil.itemStackToJson(pJson, "result", result);
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.COMPACTOR_SERIALIZER.get();
    }

    @Override
    @Nullable
    public JsonObject serializeAdvancement() {
        return advancementBuilder.serializeToJson();
    }

    @Override
    @Nullable
    public ResourceLocation getAdvancementId() {
        return advancementId;
    }
}
