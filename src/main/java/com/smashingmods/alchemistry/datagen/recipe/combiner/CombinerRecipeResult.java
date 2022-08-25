package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.common.item.IngredientStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class CombinerRecipeResult implements FinishedRecipe {
    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation id;
    private final ResourceLocation advancementId;
    private final List<IngredientStack> input;
    private final ItemStack output;

    public CombinerRecipeResult(String pGroup,
                                Advancement.Builder pBuilder,
                                ResourceLocation pId,
                                ResourceLocation pAdvancementId,
                                List<IngredientStack> pInput,
                                ItemStack pOutput
                                ) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.id = pId;
        this.advancementId = pAdvancementId;
        this.input = pInput;
        this.output = pOutput;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        DatagenUtil.ingredientStackListToJson(pJson, "input", input);
        DatagenUtil.itemStackToJson(pJson, "result", output);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.COMBINER_SERIALIZER.get();
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
