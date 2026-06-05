package com.smashingmods.alchemistry.datagen.recipe.atomizer;

import com.google.gson.JsonObject;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.datagen.DatagenHelpers;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nullable;

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
        pJson.addProperty("id", id.toString());
        if (!group.isEmpty()) {
            pJson.addProperty("group", group);
        }
        DatagenHelpers.fluidStacktoJson(pJson, "input", input);
        DatagenHelpers.itemStackToJson(pJson, "result", result);
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public RecipeSerializer<?> type() {
        return RecipeRegistry.ATOMIZER_SERIALIZER.get();
    }

    @Nullable
    @Override
    public AdvancementHolder advancement() {
        return advancementBuilder.build(advancementId);
    }
}
