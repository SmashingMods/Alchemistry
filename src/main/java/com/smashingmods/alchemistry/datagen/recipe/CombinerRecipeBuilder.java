package com.smashingmods.alchemistry.datagen.recipe;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.Registry;
import com.smashingmods.alchemistry.datagen.DatagenUtils;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CombinerRecipeBuilder extends BaseRecipeBuilder {

    private final String group = "minecraft:misc";
    private final List<ItemStack> input;
    private final ItemStack output;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public CombinerRecipeBuilder(List<ItemStack> input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public static CombinerRecipeBuilder recipe(ItemStack output, ItemStack... input) {
        return recipe(output, Arrays.asList(input));
    }

    public static CombinerRecipeBuilder recipe(ItemStack output, List<ItemStack> input) {
        return new CombinerRecipeBuilder(input, output);
    }


    public void build(Consumer<FinishedRecipe> consumerIn) {
        String name = this.output.getItem().getRegistryName().getPath();
        this.build(consumerIn, new ResourceLocation(Alchemistry.MODID, "combiner/" + name));
    }

    @Override
    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumerIn.accept(new CombinerRecipeBuilder.Result
                (id, this.group == null ? "" : this.group, this.input, this.output,
                        this.advancementBuilder, new ResourceLocation(id.getNamespace(),
                        "recipes/" + this.output.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath())));


    }

    @Override
    void validate(ResourceLocation id) {

    }

    public static class Result implements FinishedRecipe {
        private final String group;
        private final ResourceLocation id;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementID;
        private final List<ItemStack> input;
        private final ItemStack output;

        public Result(ResourceLocation id, String group, List<ItemStack> input, ItemStack output,
                      Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
            this.id = id;
            this.group = group;
            this.input = input;
            this.output = output;
            this.advancementBuilder = advancementBuilder;
            this.advancementID = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) json.addProperty("group", this.group);
            DatagenUtils.addStackListToJson(json, "input", input);
            DatagenUtils.addStackToJson(json, "result", output);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return Registry.COMBINER_SERIALIZER.get();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementID;
        }
    }
}
