package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.datagen.DatagenHelpers;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class AtomizerRecipeResult implements FinishedRecipe {

    private final String group;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation id;
    private final ResourceLocation advancementId;
    private final FluidStack input;
    private final ItemStack result;

    public AtomizerRecipeResult(String pGroup,
                                Advancement.Builder pBuilder,
                                ResourceLocation pId,
                                ResourceLocation pAdvancementId,
                                FluidStack pInput,
                                ItemStack pOutput) {
        this.group = pGroup;
        this.advancementBuilder = pBuilder;
        this.id = pId;
        this.advancementId = pAdvancementId;
        this.input = pInput;
        this.result = pOutput;
    }

    @Override
    public void serializeRecipeData(JsonObject pJson) {
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        DatagenHelpers.fluidStacktoJson(pJson, "input", input);
        DatagenHelpers.itemStackToJson(pJson, "result", result);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeRegistry.ATOMIZER_SERIALIZER.get();
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
