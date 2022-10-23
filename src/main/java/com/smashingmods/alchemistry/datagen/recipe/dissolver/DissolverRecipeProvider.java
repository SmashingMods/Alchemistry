package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.item.IngredientStack;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeBuilder;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DissolverRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public DissolverRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new DissolverRecipeProvider(pConsumer).register();
    }

    private void register() {
        ChemlibRecipes.register(consumer);
        MinecraftRecipes.register(consumer);
        ThermalRecipes.register(consumer);
    }

    public void dissolver(ItemLike pItemLike, ProbabilitySet pSet) {
        dissolver(pItemLike, pSet, false);
    }

    public void dissolver(ItemLike pItemLike, ProbabilitySet pSet, boolean pReversible) {
        dissolver(new IngredientStack(pItemLike), pSet, Objects.requireNonNull(pItemLike.asItem().getRegistryName()));

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

                ResourceLocation recipeId = DatagenUtil.getLocation(output, "combiner");
                CombinerRecipeBuilder.createRecipe(output, ingredientStackList, Objects.requireNonNull(output.getItem().getRegistryName()))
                        .group(String.format("%s:combiner", Alchemistry.MODID))
                        .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                        .save(consumer);
            }
        }
    }

    public void dissolver(String pItemTag, ProbabilitySet pSet) {
        ResourceLocation itemId = new ResourceLocation(pItemTag);
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, itemId);
        dissolver(new IngredientStack(Ingredient.of(tagKey)), pSet, itemId);
    }

    public void dissolver(String pItemTag, ProbabilitySet pSet, ICondition pCondition) {
        ResourceLocation itemId = new ResourceLocation(pItemTag);
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, itemId);
        dissolver(new IngredientStack(Ingredient.of(tagKey)), pSet, itemId, pCondition);
    }

    public void dissolver(IngredientStack pIngredient, ProbabilitySet pSet, ResourceLocation pRecipeId, ICondition pCondition) {
        ConditionalRecipe.builder()
                .addCondition(pCondition)
                .addRecipe(DissolverRecipeBuilder.createRecipe(pIngredient, pSet, pRecipeId)
                        .group(String.format("%s:dissolver", Alchemistry.MODID))
                        .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                        ::save)
                .build(consumer, new ResourceLocation(Alchemistry.MODID, String.format("dissolver/%s", pRecipeId.getPath())));
    }

    public void dissolver(IngredientStack pIngredient, ProbabilitySet pSet, ResourceLocation pRecipeId) {
        DissolverRecipeBuilder.createRecipe(pIngredient, pSet, pRecipeId)
                .group(String.format("%s:dissolver", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .save(consumer, pRecipeId);
    }
}
