package com.smashingmods.alchemistry.datagen.recipe.fusion;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class FusionRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation recipeId;
    private final ResourceLocation advancementId;
    private final ElementItem input1;
    private final ElementItem input2;
    private final ItemStack output;

    public FusionRecipeResult(String pGroup,
                              Advancement.Builder pBuilder,
                              ResourceLocation pId,
                              ResourceLocation pAdvancementId,
                              ElementItem pInput1,
                              ElementItem pInput2,
                              ItemStack pOutput) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.recipeId = pId;
        this.advancementId = pAdvancementId;
        this.input1 = pInput1;
        this.input2 = pInput2;
        this.output = pOutput;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        DatagenUtil.itemStackToJson(pJson, "input1", new ItemStack(input1));
        DatagenUtil.itemStackToJson(pJson, "input2", new ItemStack(input2));
        DatagenUtil.itemStackToJson(pJson, "output", output);
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.FUSION_SERIALIZER.get();
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
