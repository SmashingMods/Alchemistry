package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.google.common.collect.Lists;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.datagen.recipe.RecipeUtils;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.smashingmods.alchemistry.datagen.recipe.RecipeUtils.toStack;

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
        combiner(BlockRegistry.REACTOR_GLASS.get().asItem(), Lists.newArrayList(toStack("silicon_dioxide"), toStack("lead_oxide")));

        // saplings
        combiner(Items.OAK_SAPLING, Lists.newArrayList(toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.SPRUCE_SAPLING, Lists.newArrayList(toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.JUNGLE_SAPLING, Lists.newArrayList(toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.ACACIA_SAPLING, Lists.newArrayList(toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.DARK_OAK_SAPLING, Lists.newArrayList(toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.BIRCH_SAPLING, Lists.newArrayList(toStack("oxygen"),toStack("cellulose", 2)));

        // food
        combiner(Items.CARROT, Lists.newArrayList(toStack("cellulose"), toStack("beta_carotene")));
        combiner(Items.POTATO, Lists.newArrayList(toStack("starch"), toStack("potassium", 4)));
        combiner(Items.WHEAT_SEEDS, Lists.newArrayList(toStack("triglyceride"), toStack("sucrose")));
        combiner(Items.PUMPKIN_SEEDS, Lists.newArrayList(toStack("triglyceride"), toStack("sucrose")));
        combiner(Items.MELON_SEEDS, Lists.newArrayList(toStack("triglyceride"), toStack("sucrose")));
        combiner(Items.BEETROOT_SEEDS, Lists.newArrayList(toStack("triglyceride"), toStack("sucrose"), toStack("iron_oxide")));
        combiner(Items.BEETROOT, Lists.newArrayList(toStack("sucrose"), toStack("iron_oxide")));

        // mob drops
        combiner(Items.NETHER_STAR, Lists.newArrayList(toStack("lutetium", 64), toStack("titanium", 64), toStack("dysprosium", 64), toStack("mendelevium", 64)));
        combiner(Items.WHITE_WOOL, Lists.newArrayList(toStack("keratin", 2), toStack("triglyceride", 1)));

        // gems
        combiner(Items.DIAMOND, Lists.newArrayList(toStack("graphite", 64), toStack("graphite", 64)));
        combiner(Items.EMERALD, Lists.newArrayList(toStack("beryl", 8), toStack("chromium", 8), toStack("vanadium", 4)));
        combiner(Items.LAPIS_LAZULI, Lists.newArrayList(toStack("sodium", 6), toStack("mullite", 3), toStack("calcium_sulfide", 2), toStack("silicon", 3)));

        // misc, everything else
        combiner(Items.DIRT, Lists.newArrayList(toStack("water"), toStack("cellulose"), toStack("kaolinite")));
        combiner(Items.DEEPSLATE, Lists.newArrayList(toStack("silicon_dioxide", 1), toStack("aluminum", 1), toStack("iron", 1)));
        combiner(Items.BASALT, Lists.newArrayList(toStack("silicon_dioxide", 1), toStack("aluminum_oxide", 1)));
        combiner(Items.GRASS_BLOCK, Lists.newArrayList(toStack("water"), toStack("cellulose"), toStack("kaolinite")));
        combiner(Items.MYCELIUM, Lists.newArrayList(toStack("water"), toStack("chitin"), toStack("kaolinite"), toStack("silicon_dioxide")));
        combiner(Items.WATER_BUCKET, Lists.newArrayList(toStack("water", 16), new ItemStack(Items.BUCKET)));
        combiner(Items.MILK_BUCKET, Lists.newArrayList(toStack("calcium", 4), toStack("protein", 2), toStack("water", 16), new ItemStack(Items.BUCKET)));
        combiner(Items.REDSTONE_BLOCK, Lists.newArrayList(toStack("iron_oxide", 9), toStack("strontium_carbonate", 9)));
        combiner(Items.REDSTONE, Lists.newArrayList(toStack("iron_oxide"), toStack("strontium_carbonate")));
        combiner(Items.PACKED_ICE, Lists.newArrayList(toStack("water", 36), toStack("water", 36), toStack("water", 36), toStack("water", 36)));
    }

    private void combiner(ItemLike pOutput, List<ItemStack> pInput) {
        combiner(new ItemStack(pOutput), pInput);
    }

    private void combiner(ItemStack pOutput, List<ItemStack> pInput) {
        CombinerRecipeBuilder.createRecipe(pOutput, pInput, Objects.requireNonNull(pOutput.getItem().getRegistryName()))
                .group(String.format("%s:combiner", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(RecipeUtils.getLocation(pOutput, "combiner")))
                .save(consumer);
    }
}
