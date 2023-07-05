package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.smashingmods.alchemylib.datagen.DatagenHelpers.getLocation;
import static com.smashingmods.alchemylib.datagen.DatagenHelpers.toIngredientStack;

public class CombinerRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public CombinerRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new CombinerRecipeProvider(pConsumer).register();
    }

    private void register() {

        // Reactor Glass
        combiner(BlockRegistry.REACTOR_GLASS.get().asItem(), toIngredientStack("silicon_dioxide"), toIngredientStack("lead_oxide"));

        // saplings
        combiner(Items.OAK_SAPLING, toIngredientStack("oxygen"), toIngredientStack("cellulose", 2));
        combiner(Items.SPRUCE_SAPLING, toIngredientStack("oxygen"), toIngredientStack("cellulose", 2));
        combiner(Items.JUNGLE_SAPLING, toIngredientStack("oxygen"), toIngredientStack("cellulose", 2));
        combiner(Items.ACACIA_SAPLING, toIngredientStack("oxygen"), toIngredientStack("cellulose", 2));
        combiner(Items.DARK_OAK_SAPLING, toIngredientStack("oxygen"), toIngredientStack("cellulose", 2));
        combiner(Items.BIRCH_SAPLING, toIngredientStack("oxygen"), toIngredientStack("cellulose", 2));

        // food
        combiner(Items.CARROT, toIngredientStack("cellulose"), toIngredientStack("beta_carotene"));
        combiner(Items.POTATO, toIngredientStack("starch"), toIngredientStack("potassium", 4));
        combiner(Items.WHEAT_SEEDS, toIngredientStack("triglyceride"), toIngredientStack("sucrose"));
        combiner(Items.PUMPKIN_SEEDS, toIngredientStack("triglyceride"), toIngredientStack("sucrose"));
        combiner(Items.MELON_SEEDS, toIngredientStack("triglyceride"), toIngredientStack("sucrose"));
        combiner(Items.BEETROOT_SEEDS, toIngredientStack("triglyceride"), toIngredientStack("sucrose"), toIngredientStack("iron_oxide"));
        combiner(Items.BEETROOT, toIngredientStack("sucrose"), toIngredientStack("iron_oxide"));

        // mob drops
        combiner(Items.NETHER_STAR, toIngredientStack("lutetium", 64), toIngredientStack("titanium", 64), toIngredientStack("dysprosium", 64), toIngredientStack("mendelevium", 64));
        combiner(Items.WHITE_WOOL, toIngredientStack("keratin", 2), toIngredientStack("triglyceride", 1));

        // gems
        combiner(Items.EMERALD, toIngredientStack("beryl", 8), toIngredientStack("chromium", 8), toIngredientStack("vanadium", 4));
        combiner(Items.LAPIS_LAZULI, toIngredientStack("sodium", 6), toIngredientStack("mullite", 3), toIngredientStack("calcium_sulfide", 2), toIngredientStack("silicon", 3));

        // misc, everything else
        combiner(Items.DIRT, toIngredientStack("water"), toIngredientStack("cellulose"), toIngredientStack("kaolinite"));
        combiner(Items.DEEPSLATE, toIngredientStack("silicon_dioxide", 1), toIngredientStack("aluminum", 1), toIngredientStack("iron", 1));
        combiner(Items.BASALT, toIngredientStack("silicon_dioxide", 1), toIngredientStack("aluminum_oxide", 1));
        combiner(Items.GRASS_BLOCK, toIngredientStack("water"), toIngredientStack("cellulose"), toIngredientStack("kaolinite"));
        combiner(Items.MYCELIUM, toIngredientStack("water"), toIngredientStack("chitin"), toIngredientStack("kaolinite"), toIngredientStack("silicon_dioxide"));
        combiner(Items.WATER_BUCKET, toIngredientStack("water", 16), new ItemStack(Items.BUCKET));
        combiner(Items.MILK_BUCKET, toIngredientStack("calcium", 4), toIngredientStack("protein", 2), toIngredientStack("water", 16), new ItemStack(Items.BUCKET));
        combiner(Items.REDSTONE_BLOCK, toIngredientStack("iron_oxide", 9), toIngredientStack("strontium_carbonate", 9));
        combiner(Items.REDSTONE, toIngredientStack("iron_oxide"), toIngredientStack("strontium_carbonate"));

        // Testing space, if there's anything below this line yell at Tim because he forgot to delete it
    }

    private void combiner(ItemLike pOutput, Object ... pInput) {
        combiner(pOutput, null, pInput);
    }
    
    @SuppressWarnings("SameParameterValue")
    private void combiner(ItemLike pOutput, @Nullable ICondition pCondition, Object ... pInput) {
        List<IngredientStack> ingredientStackList = new ArrayList<>();
        for (Object obj : pInput) {
            if (obj instanceof ItemLike itemLike) {
                ingredientStackList.add(new IngredientStack(itemLike));
            } else if (obj instanceof ItemStack itemStack) {
                ingredientStackList.add(new IngredientStack(itemStack));
            } else if (obj instanceof IngredientStack ingredientStack) {
                ingredientStackList.add(ingredientStack);
            } else if (obj instanceof String itemTag) {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(itemTag));
                ingredientStackList.add(new IngredientStack(Ingredient.of(tagKey)));
            }
        }

        if (pCondition == null) {
            combiner(new ItemStack(pOutput), ingredientStackList);
        } else {
            combiner(new ItemStack(pOutput), ingredientStackList, pCondition);
        }
    }

    @SuppressWarnings("unused")
    private void combiner(ItemLike pOutput, List<Object> pInput, @Nullable ICondition pCondition) {
        List<IngredientStack> ingredientStackList = new ArrayList<>();
        for (Object obj : pInput) {
            if (obj instanceof ItemStack itemStack) {
                ingredientStackList.add(new IngredientStack(itemStack));
            } else if (obj instanceof String itemTag) {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(itemTag));
                ingredientStackList.add(new IngredientStack(Ingredient.of(tagKey)));
            }
        }
        if (pCondition == null) {
            combiner(new ItemStack(pOutput), ingredientStackList);
        } else {
            combiner(new ItemStack(pOutput), ingredientStackList, pCondition);
        }
    }

    private void combiner(ItemStack pOutput, List<IngredientStack> pInput, ICondition pCondition) {
        ResourceLocation recipeId = ForgeRegistries.ITEMS.getKey(pOutput.getItem());
        ConditionalRecipe.builder()
                .addCondition(pCondition)
                .addRecipe(CombinerRecipeBuilder.createRecipe(pOutput, pInput, Objects.requireNonNull(recipeId))
                        .group(String.format("%s:combiner", Alchemistry.MODID))
                        .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput, "combiner", Alchemistry.MODID)))
                        ::save)
                .build(consumer, new ResourceLocation(Alchemistry.MODID, String.format("combiner/%s", recipeId.getPath())));
    }

    private void combiner(ItemStack pOutput, List<IngredientStack> pInput) {
        CombinerRecipeBuilder.createRecipe(pOutput, pInput, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.getItem())))
                .group(String.format("%s:combiner", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput, "combiner", Alchemistry.MODID)))
                .save(consumer);
    }
}
