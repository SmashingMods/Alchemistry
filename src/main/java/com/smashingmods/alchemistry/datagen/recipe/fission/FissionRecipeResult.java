package com.smashingmods.alchemistry.datagen.recipe.fission;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FissionRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation recipeId;
    private final ResourceLocation advancementId;
    private final ElementItem input;
    private final ItemStack output1;
    private final ItemStack output2;

    public FissionRecipeResult(String pGroup,
                               Advancement.Builder pBuilder,
                               ResourceLocation pId,
                               ResourceLocation pAdvancementId,
                               ElementItem pInput,
                               ItemStack pOutput1,
                               ItemStack pOutput2) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.recipeId = pId;
        this.advancementId = pAdvancementId;
        this.input = pInput;
        this.output1 = pOutput1;
        this.output2 = pOutput2;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        DatagenUtil.itemStackToJson(pJson, "input", new ItemStack(input));
        DatagenUtil.itemStackToJson(pJson, "output1", output1);
        DatagenUtil.itemStackToJson(pJson, "output2", output2);
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.FISSION_SERIALIZER.get();
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