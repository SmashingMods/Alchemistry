package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeBuilder;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.smashingmods.alchemylib.datagen.DatagenHelpers;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DissolverRecipeProvider {

    protected final RecipeOutput consumer;

    public DissolverRecipeProvider(RecipeOutput pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(RecipeOutput pConsumer) {
        new DissolverRecipeProvider(pConsumer).register();
    }

    protected void register() {
        ChemlibRecipes.register(consumer);
        MinecraftRecipes.register(consumer);
        ThermalRecipes.register(consumer);
    }

    protected static ICondition tagNotEmptyCondition(String pTag) {
        return new NotCondition(new TagEmptyCondition(ResourceLocation.parse(pTag)));
    }

    public void dissolver(ItemLike pItemLike, ProbabilitySet pSet) {
        dissolver(pItemLike, pSet, false);
    }

    public void dissolver(ItemLike pItemLike, ProbabilitySet pSet, boolean pReversible) {
        dissolver(new IngredientStack(pItemLike), pSet, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(pItemLike.asItem())));

        if (pReversible) {
            ItemStack output = new ItemStack(pItemLike);
            List<ItemStack> items = new ArrayList<>();

            pSet.getProbabilityGroups().stream().map(ProbabilityGroup::getOutput).forEach(items::addAll);

            if (items.size() <= 4) {
                items = items.stream().filter(itemStack -> !itemStack.isEmpty()).toList();

                List<IngredientStack> ingredientStackList = new ArrayList<>();
                for (ItemStack itemStack : items) {
                    ingredientStackList.add(new IngredientStack(itemStack));
                }

                ResourceLocation itemKey = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(output.getItem()));
                ResourceLocation fullId = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("combiner/%s", itemKey.getPath()));
                CombinerRecipeBuilder.createRecipe(output, ingredientStackList, itemKey)
                        .group(String.format("%s:combiner", Alchemistry.MODID))
                        .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(DatagenHelpers.getLocation(output, "combiner", Alchemistry.MODID)))
                        .save(consumer, fullId);
            }
        }
    }

    public void dissolver(String pItemTag, ProbabilitySet pSet) {
        ResourceLocation itemId = ResourceLocation.parse(pItemTag);
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, itemId);
        dissolver(new IngredientStack(Ingredient.of(tagKey)), pSet, itemId);
    }

    public void dissolver(String pItemTag, ProbabilitySet pSet, ICondition pCondition) {
        ResourceLocation itemId = ResourceLocation.parse(pItemTag);
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, itemId);
        dissolver(new IngredientStack(Ingredient.of(tagKey)), pSet, itemId, pCondition);
    }

    public void dissolver(IngredientStack pIngredient, ProbabilitySet pSet, ResourceLocation pRecipeId, ICondition pCondition) {
        ResourceLocation fullId = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("dissolver/%s", pRecipeId.getPath()));
        DissolverRecipeBuilder.createRecipe(pIngredient, pSet, pRecipeId)
                .group(String.format("%s:dissolver", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .save(consumer, fullId, pCondition);
    }

    public void dissolver(IngredientStack pIngredient, ProbabilitySet pSet, ResourceLocation pRecipeId) {
        ResourceLocation fullId = ResourceLocation.fromNamespaceAndPath(Alchemistry.MODID, String.format("dissolver/%s", pRecipeId.getPath()));
        DissolverRecipeBuilder.createRecipe(pIngredient, pSet, pRecipeId)
                .group(String.format("%s:dissolver", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .save(consumer, fullId);
    }
}
