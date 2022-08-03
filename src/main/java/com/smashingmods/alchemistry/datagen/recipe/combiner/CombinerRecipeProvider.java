package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.google.common.collect.Lists;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.smashingmods.alchemistry.datagen.recipe.StackUtils.toStack;

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
        combiner(new ItemStack(BlockRegistry.REACTOR_GLASS.get().asItem()), Lists.newArrayList(
                toStack("silicon_dioxide"),
                toStack("lead_oxide")));

        // saplings
        combiner(new ItemStack(Items.OAK_SAPLING), Lists.newArrayList(
                toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.SPRUCE_SAPLING), Lists.newArrayList(
                toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.JUNGLE_SAPLING), Lists.newArrayList(
                toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.ACACIA_SAPLING), Lists.newArrayList(
                toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.DARK_OAK_SAPLING), Lists.newArrayList(
                toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.BIRCH_SAPLING), Lists.newArrayList(
                toStack("oxygen"),
                toStack("cellulose", 2)));

        // food
        combiner(new ItemStack(Items.CARROT), Lists.newArrayList(
                toStack("cellulose"),
                toStack("beta_carotene")));

        combiner(new ItemStack(Items.POTATO), Lists.newArrayList(
                toStack("starch"),
                toStack("potassium", 4)));

        combiner(new ItemStack(Items.WHEAT_SEEDS), Lists.newArrayList(
                toStack("triglyceride"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.PUMPKIN_SEEDS), Lists.newArrayList(
                toStack("triglyceride"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.MELON_SEEDS), Lists.newArrayList(
                toStack("triglyceride"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.BEETROOT_SEEDS), Lists.newArrayList(
                toStack("triglyceride"),
                toStack("sucrose"),
                toStack("iron_oxide")));

        combiner(new ItemStack(Items.BEETROOT), Lists.newArrayList(
                toStack("sucrose"),
                toStack("iron_oxide")));

        // mob drops
        combiner(new ItemStack(Items.NETHER_STAR), Lists.newArrayList(
                toStack("lutetium", 64),
                toStack("titanium", 64),
                toStack("dysprosium", 64),
                toStack("mendelevium", 64)));

        combiner(new ItemStack(Items.WHITE_WOOL), Lists.newArrayList(
                toStack("keratin", 2),
                toStack("triglyceride", 1)));

        // gems
        combiner(new ItemStack(Items.DIAMOND), Lists.newArrayList(
                toStack("graphite", 64),
                toStack("graphite", 64)));

        combiner(new ItemStack(Items.EMERALD), Lists.newArrayList(
                toStack("beryl", 8),
                toStack("chromium", 8),
                toStack("vanadium", 4)));

        combiner(new ItemStack(Items.LAPIS_LAZULI), Lists.newArrayList(
                toStack("sodium", 6),
                toStack("mullite", 3),
                toStack("calcium_sulfide", 2),
                toStack("silicon", 3)));


        combiner(new ItemStack(Items.DIRT), Lists.newArrayList(
                toStack("water"),
                toStack("cellulose"),
                toStack("kaolinite")));

        combiner(new ItemStack(Items.GRASS_BLOCK), Lists.newArrayList(
                toStack("water"),
                toStack("cellulose"),
                toStack("kaolinite")));

        combiner(new ItemStack(Items.MYCELIUM), Lists.newArrayList(
                toStack("water"),
                toStack("chitin"),
                toStack("kaolinite")));

        combiner(new ItemStack(Items.WATER_BUCKET), Lists.newArrayList(
                toStack("water", 16),
                new ItemStack(Items.BUCKET)));

        combiner(new ItemStack(Items.MILK_BUCKET), Lists.newArrayList(
                toStack("calcium", 4),
                toStack("protein", 2),
                toStack("water", 16),
                new ItemStack(Items.BUCKET)));

        combiner(new ItemStack(Items.REDSTONE_BLOCK), Lists.newArrayList(
                toStack("iron_oxide", 9),
                toStack("strontium_carbonate", 9)));

        combiner(new ItemStack(Items.REDSTONE), Lists.newArrayList(
                toStack("iron_oxide"),
                toStack("strontium_carbonate")));

        combiner(new ItemStack(Items.PACKED_ICE), Lists.newArrayList(
                toStack("water", 36),
                toStack("water", 36),
                toStack("water", 36),
                toStack("water", 36)));
    }

    private void combiner(ItemStack pOutput, List<ItemStack> pInput) {
        CombinerRecipeBuilder.createRecipe(pOutput, pInput)
                .group(Alchemistry.MODID + ":combiner")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput)))
                .save(consumer);
    }

    private ResourceLocation getLocation(ItemStack itemStack) {
        Objects.requireNonNull(itemStack.getItem().getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("combiner/%s", itemStack.getItem().getRegistryName().getPath()));
    }
}
