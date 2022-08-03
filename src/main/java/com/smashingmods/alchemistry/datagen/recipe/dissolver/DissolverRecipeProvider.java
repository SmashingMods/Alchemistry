package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilityGroup;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.datagen.recipe.RecipeUtils;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeBuilder;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.CompoundItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet.Builder.createSet;
import static com.smashingmods.alchemistry.datagen.recipe.RecipeUtils.toStack;

public class DissolverRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public DissolverRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new DissolverRecipeProvider(pConsumer).register();
    }

    private void register() {

        // Add Chemlib recipes
        ItemRegistry.getCompounds().forEach(compoundItem -> {
            List<ItemStack> components = new ArrayList<>();
            compoundItem.getComponents().forEach((name, count) -> {
                Optional<ElementItem> optionalElement = ItemRegistry.getElementByName(name);
                Optional<CompoundItem> optionalCompound = ItemRegistry.getCompoundByName(name);
                optionalElement.ifPresent(element -> components.add(new ItemStack(element, count)));
                optionalCompound.ifPresent(compound -> components.add(new ItemStack(compound, count)));
            });
            dissolver(compoundItem, createSet().addGroup(components, 100).build(), true);
        });

        // Stones
        for (Item item : newArrayList(Items.STONE, Items.SMOOTH_STONE, Items.STONE_BRICK_SLAB, Items.STONE_BRICKS, Items.CRACKED_STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.STONE_SLAB, Items.SMOOTH_STONE_SLAB)) {
            int rolls = 2;
            if (item == Items.STONE_BRICK_SLAB || item == Items.SMOOTH_STONE_SLAB || item == Items.STONE_SLAB) {
                rolls = 1;
            }
            dissolver(item, createSet().rolls(rolls).weighted()
                    .addGroup(20)
                    .addGroup(1, toStack("aluminum"))
                    .addGroup(2, toStack("iron"))
                    .addGroup(.7, toStack("gold"))
                    .addGroup(5, toStack("silicon_dioxide"))
                    .addGroup(.3, toStack("dysprosium"))
                    .addGroup(.6, toStack("zirconium"))
                    .addGroup(.5, toStack("tungsten"))
                    .addGroup(.5, toStack("nickel"))
                    .addGroup(.5, toStack("gallium"))
                    .build());
        }

        newArrayList(Items.ANDESITE, Items.POLISHED_ANDESITE).forEach(item ->
                dissolver(item, createSet().weighted()
                        .addGroup(4, toStack("aluminum_oxide"))
                        .addGroup(3, toStack("iron"))
                        .addGroup(4, toStack("potassium_chloride"))
                        .addGroup(10, toStack("silicon_dioxide"))
                        .addGroup(2, toStack("platinum"))
                        .addGroup(4, toStack("calcium"))
                        .build()));

        newArrayList(Items.DIORITE, Items.POLISHED_DIORITE).forEach(item ->
                dissolver(item, createSet().weighted()
                        .addGroup(4, toStack("aluminum_oxide"))
                        .addGroup(2, toStack("iron"))
                        .addGroup(4, toStack("potassium_chloride"))
                        .addGroup(10, toStack("silicon_dioxide"))
                        .addGroup(1.5, toStack("indium"))
                        .addGroup(2, toStack("manganese"))
                        .addGroup(2, toStack("osmium"))
                        .addGroup(3, toStack("tin"))
                        .build()));

        newArrayList(Items.GRANITE, Items.POLISHED_GRANITE).forEach(item ->
                dissolver(item, createSet().weighted()
                        .addGroup(5, toStack("aluminum_oxide"))
                        .addGroup(2, toStack("iron"))
                        .addGroup(2, toStack("potassium_chloride"))
                        .addGroup(10, toStack("silicon_dioxide"))
                        .addGroup(1, toStack("technetium"))
                        .addGroup(1.5, toStack("manganese"))
                        .addGroup(1.5, toStack("radium"))
                        .build()));

        for (Item item : newArrayList(Items.DEEPSLATE, Items.DEEPSLATE_BRICKS, Items.DEEPSLATE_BRICK_SLAB, Items.DEEPSLATE_TILES, Items.DEEPSLATE_TILE_SLAB, Items.COBBLED_DEEPSLATE, Items.COBBLED_DEEPSLATE_SLAB, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE, Items.POLISHED_DEEPSLATE_SLAB)) {
            int rolls;
            if (item == Items.DEEPSLATE_BRICK_SLAB || item == Items.COBBLED_DEEPSLATE_SLAB || item == Items.POLISHED_DEEPSLATE_SLAB) {
                rolls = 1;
            } else {
                rolls = 2;
            }
            dissolver(item, createSet().rolls(rolls).weighted()
                    .addGroup(40)
                    .addGroup(2, toStack("osmium"))
                    .addGroup(4, toStack("iron"))
                    .addGroup(2, toStack("gold"))
                    .addGroup(20, toStack("silicon_dioxide"))
                    .addGroup(2, toStack("silver"))
                    .addGroup(1, toStack("terbium"))
                    .addGroup(1, toStack("europium"))
                    .addGroup(1, toStack("scandium"))
                    .addGroup(1, toStack("yttrium"))
                    .build());
        }

        dissolver(Items.CALCITE, createSet()
                .addGroup(100, toStack("calcium_carbonate"))
                .build(), true);

        dissolver(Items.NETHERRACK, createSet().weighted()
                .addGroup(15)
                .addGroup(2, toStack("zinc_oxide"))
                .addGroup(1, toStack("gold"))
                .addGroup(1, toStack("phosphorus"))
                .addGroup(3, toStack("sulfur"))
                .addGroup(1, toStack("germanium"))
                .addGroup(4, toStack("silicon"))
                .build());

        for (Item item : newArrayList(Items.NETHER_BRICK, Items.NETHER_BRICKS, Items.CRACKED_NETHER_BRICKS, Items.CHISELED_NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.NETHER_BRICK_SLAB, Items.RED_NETHER_BRICK_SLAB)){
            int rolls = 4;
            if (item == Items.NETHER_BRICK) {
                rolls = 1;
            }
            else if (item == Items.NETHER_BRICK_SLAB || item == Items.RED_NETHER_BRICK_SLAB) {
                rolls = 2;
            }
            dissolver(item, createSet().weighted().rolls(rolls)
                    .addGroup(5)
                    .addGroup(2, toStack("zinc_oxide"))
                    .addGroup(1, toStack("gold"))
                    .addGroup(1, toStack("phosphorus"))
                    .addGroup(4, toStack("sulfur"))
                    .addGroup(1, toStack("germanium"))
                    .addGroup(4, toStack("silicon"))
                    .build());
        }

        for (Item item : newArrayList(Items.CLAY_BALL, Items.BRICK)) {
            dissolver(item, createSet()
                    .addGroup(100, toStack("kaolinite", 1))
                    .build(), true);
        }

        for (Item item : newArrayList(Items.CLAY, Items.BRICKS)) {
            dissolver(item, createSet()
                    .addGroup(100, toStack("kaolinite", 4))
                    .build(), true);
        }

        dissolver(Items.BRICK_SLAB, createSet()
                .addGroup(100, toStack("kaolinite", 2))
                .build());

        // Dirt
        dissolver("minecraft:dirt", createSet().weighted()
                .addGroup(3, toStack("water"))
                .addGroup(5, toStack("silicon_dioxide"))
                .addGroup(1, toStack("cellulose"))
                .addGroup(1, toStack("kaolinite"))
                .build());

        dissolver(Items.MYCELIUM, createSet().weighted()
                .addGroup(3, toStack("water"))
                .addGroup(5, toStack("silicon_dioxide"))
                .addGroup(1, toStack("cellulose"))
                .addGroup(1, toStack("kaolinite"))
                .addGroup(3, toStack("chitin"))
                .build());

        dissolver(Items.CRIMSON_NYLIUM, createSet().weighted()
                .addGroup(10)
                .addGroup(2, toStack("cellulose"))
                .addGroup(4, toStack("zinc_oxide"))
                .addGroup(1, toStack("gold"))
                .addGroup(1, toStack("phosphorus"))
                .addGroup(3, toStack("sulfur"))
                .addGroup(3, toStack("germanium"))
                .addGroup(5, toStack("silicon"))
                .addGroup(1, toStack("selenium"))
                .build());

        dissolver(Items.WARPED_NYLIUM, createSet().weighted()
                .addGroup(10)
                .addGroup(2, toStack("cellulose"))
                .addGroup(4, toStack("zinc_oxide"))
                .addGroup(1, toStack("gold"))
                .addGroup(1, toStack("phosphorus"))
                .addGroup(3, toStack("sulfur"))
                .addGroup(1, toStack("mercury"))
                .addGroup(5, toStack("silicon"))
                .addGroup(1, toStack("neodymium"))
                .build());

        // Woods
        dissolver("minecraft:logs", createSet()
                .addGroup(100, toStack("cellulose"))
                .build());

        dissolver("minecraft:planks", createSet()
                .addGroup(25, toStack("cellulose"))
                .build());

        dissolver("minecraft:wooden_slabs", createSet()
                .addGroup(12.5, toStack("cellulose"))
                .build());

        dissolver("forge:fences/wooden", createSet()
                .addGroup(33.3334, toStack("cellulose"))
                .build());

        dissolver("forge:fence_gates/wooden", createSet()
                .addGroup(100, toStack("cellulose"))
                .build());

        dissolver("forge:chests/wooden", createSet()
                .addGroup(100, toStack("cellulose", 2))
                .build());

        dissolver("minecraft:leaves", createSet()
                .addGroup(5, toStack("cellulose"))
                .build());

        dissolver("minecraft:saplings", createSet()
                .addGroup(25, toStack("cellulose"))
                .build());


        dissolver(Items.STICK, createSet().addGroup(10, toStack("cellulose")).build());

        dissolver(Items.CRAFTING_TABLE, createSet().addGroup(100, toStack("cellulose")).build());

        // Sands
        dissolver(Items.SAND, createSet().weighted()
                .addGroup(100, toStack("silicon_dioxide", 4))
                .addGroup(1, toStack("gold"))
                .build());

        for (Item item : newArrayList(Items.SANDSTONE, Items.CHISELED_SANDSTONE, Items.CUT_SANDSTONE, Items.SMOOTH_SANDSTONE, Items.SANDSTONE_SLAB, Items.SMOOTH_SANDSTONE_SLAB)) {
            int rolls;
            if (item == Items.SANDSTONE_SLAB || item == Items.SMOOTH_SANDSTONE_SLAB) {
                rolls = 2;
            } else {
                rolls = 4;
            }
            dissolver(item, createSet().weighted().rolls(rolls)
                    .addGroup(100, toStack("silicon_dioxide", 4))
                    .addGroup(1, toStack("gold"))
                    .build());
        }

        dissolver(Items.RED_SAND, createSet().weighted()
                .addGroup(100, toStack("silicon_dioxide", 4))
                .addGroup(10, toStack("iron_oxide"))
                .build());

        for (Item item : newArrayList(Items.RED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.RED_SANDSTONE_SLAB, Items.CHISELED_RED_SANDSTONE, Items.CUT_RED_SANDSTONE_SLAB, Items.SMOOTH_RED_SANDSTONE_SLAB)) {
            int rolls;
            if (item == Items.RED_SANDSTONE_SLAB || item == Items.CUT_RED_SANDSTONE_SLAB || item == Items.SMOOTH_RED_SANDSTONE_SLAB) {
                rolls = 2;
            } else {
                rolls = 4;
            }
            dissolver(item, createSet().weighted().rolls(rolls)
                    .addGroup(100, toStack("silicon_dioxide", 4))
                    .addGroup(10, toStack("iron_oxide"))
                    .build());
        }

        dissolver(Items.GRAVEL, createSet()
                .addGroup(1, toStack("silicon_dioxide")).build(), true);

        dissolver(Items.COBBLESTONE, createSet().weighted()
                .addGroup(700)
                .addGroup(2, toStack("aluminum"))
                .addGroup(4, toStack("iron"))
                .addGroup(1.5, toStack("gold"))
                .addGroup(10, toStack("silicon_dioxide"))
                .addGroup(1, toStack("dysprosium"))
                .addGroup(1.5, toStack("zirconium"))
                .addGroup(1, toStack("nickel"))
                .addGroup(1, toStack("gallium"))
                .addGroup(1, toStack("tungsten"))
                .build());

        dissolver(Items.COBBLESTONE_SLAB, createSet().weighted()
                .addGroup(1400)
                .addGroup(2, toStack("aluminum"))
                .addGroup(4, toStack("iron"))
                .addGroup(1.5, toStack("gold"))
                .addGroup(10, toStack("silicon_dioxide"))
                .addGroup(1, toStack("dysprosium"))
                .addGroup(1.5, toStack("zirconium"))
                .addGroup(1, toStack("nickel"))
                .addGroup(1, toStack("gallium"))
                .addGroup(1, toStack("tungsten"))
                .build());

        dissolver(Items.MOSSY_COBBLESTONE, createSet().weighted()
                .addGroup(697)
                .addGroup(2, toStack("aluminum"))
                .addGroup(4, toStack("iron"))
                .addGroup(1.5, toStack("gold"))
                .addGroup(10, toStack("silicon_dioxide"))
                .addGroup(1, toStack("dysprosium"))
                .addGroup(1.5, toStack("zirconium"))
                .addGroup(1, toStack("nickel"))
                .addGroup(1, toStack("gallium"))
                .addGroup(1, toStack("tungsten"))
                .addGroup(3, toStack("cellulose"))
                .build());

        dissolver(Items.MOSSY_COBBLESTONE_SLAB, createSet().weighted()
                .addGroup(1394)
                .addGroup(2, toStack("aluminum"))
                .addGroup(4, toStack("iron"))
                .addGroup(1.5, toStack("gold"))
                .addGroup(10, toStack("silicon_dioxide"))
                .addGroup(1, toStack("dysprosium"))
                .addGroup(1.5, toStack("zirconium"))
                .addGroup(1, toStack("nickel"))
                .addGroup(1, toStack("gallium"))
                .addGroup(1, toStack("tungsten"))
                .addGroup(3, toStack("cellulose"))
                .build());

        dissolver(Items.MAGMA_BLOCK, createSet().weighted().rolls(2)
                .addGroup(10, toStack("manganese", 2))
                .addGroup(5, toStack("aluminum_oxide"))
                .addGroup(20, toStack("magnesium_oxide"))
                .addGroup(2, toStack("potassium_chloride"))
                .addGroup(10, toStack("silicon_dioxide", 2))
                .addGroup(20, toStack("sulfur", 2))
                .addGroup(10, toStack("iron_oxide"))
                .addGroup(8, toStack("lead", 2))
                .addGroup(4, toStack("fluorine"))
                .addGroup(4, toStack("bromine"))
                .build());

        for (Item item : newArrayList(Items.PRISMARINE_SHARD, Items.PRISMARINE, Items.PRISMARINE_BRICKS)) {
            int multiplier = 1;
            if (item == Items.PRISMARINE) {
                multiplier = 4;
            } else if (item == Items.PRISMARINE_BRICKS) {
                multiplier = 9;
            }
            dissolver(item, createSet().addGroup(100,
                    toStack("beryl", 4 * multiplier),
                    toStack("niobium", 3 * multiplier),
                    toStack("selenium", multiplier)).build(), true);
        }

        dissolver(Items.PRISMARINE_CRYSTALS, createSet().addGroup(100,
                toStack("silicon_dioxide", 4),
                toStack("phosphorus", 3),
                toStack("selenium", 1)).build(), true);

        dissolver(Items.DARK_PRISMARINE, createSet().addGroup(100,
                toStack("beryl", 32),
                toStack("niobium", 24),
                toStack("selenium", 8),
                toStack("titanium_oxide", 4)).build(), true);

        for (Item item : newArrayList(Items.BASALT, Items.POLISHED_BASALT, Items.BLACKSTONE,
                Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, Items.SMOOTH_BASALT, Items.POLISHED_BLACKSTONE_BRICKS, Items.POLISHED_BLACKSTONE_SLAB, Items.BLACKSTONE_SLAB, Items.POLISHED_BLACKSTONE_BRICK_SLAB)) {
            double multiplier = 1;
            if (item == Items.BLACKSTONE_SLAB || item == Items.POLISHED_BLACKSTONE_SLAB || item == Items.POLISHED_BLACKSTONE_BRICK_SLAB) {
                multiplier = 0.5;
            }
            dissolver(item, createSet().weighted()
                    .addGroup(49 * multiplier, toStack("silicon_dioxide"))
                    .addGroup(5 * multiplier, toStack("sodium_oxide"))
                    .addGroup(2 * multiplier, toStack("potassium_oxide"))
                    .addGroup(2 * multiplier, toStack("titanium_oxide"))
                    .addGroup(7 * multiplier, toStack("iron_ii_oxide"))
                    .addGroup(15 * multiplier, toStack("aluminum_oxide"))
                    .addGroup(10 * multiplier, toStack("calcium_oxide"))
                    .addGroup(10 * multiplier, toStack("magnesium_oxide"))
                    .build());
        }

        dissolver(Items.GILDED_BLACKSTONE, createSet()
                .addGroup(29, toStack("silicon_dioxide"))
                .addGroup(20, toStack("gold", 8))
                .addGroup(5, toStack("sodium_oxide"))
                .addGroup(2, toStack("potassium_oxide"))
                .addGroup(2, toStack("titanium_oxide"))
                .addGroup(7, toStack("iron_ii_oxide"))
                .addGroup(15, toStack("aluminum_oxide"))
                .addGroup(10, toStack("calcium_oxide"))
                .addGroup(10, toStack("magnesium_oxide"))
                .build());

        // Ores
        newArrayList(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE).forEach(item ->
        dissolver(item, createSet()
                .addGroup(100, toStack("graphite", 8), toStack("sulfur", 8))
                .build()));

        dissolver(Items.COAL_BLOCK, createSet().addGroup(100, toStack("graphite", 9 * 2)).build());

        dissolver(Items.COAL, createSet().addGroup(100, toStack("graphite", 2)).build());

        dissolver(Items.CHARCOAL, createSet().addGroup(100, toStack("graphite", 2)).build());

        for (Item item : newArrayList(Items.EMERALD, Items.DEEPSLATE_EMERALD_ORE, Items.EMERALD_ORE, Items.EMERALD_BLOCK)) {
            int multiplier = 1;
            if (item == Items.EMERALD_ORE || item == Items.DEEPSLATE_EMERALD_ORE) {
                multiplier = 2;
            } else if (item == Items.EMERALD_BLOCK) {
                multiplier = 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("beryl", 8 * multiplier),
                            toStack("chromium", 8 * multiplier),
                            toStack("vanadium", 4 * multiplier))
                    .build());
        }

        for (Item item : newArrayList(Items.DIAMOND, Items.DEEPSLATE_DIAMOND_ORE, Items.DIAMOND_ORE, Items.DIAMOND_BLOCK)) {
            int multiplier = 1;
            if (item == Items.DIAMOND_ORE || item == Items.DEEPSLATE_DIAMOND_ORE) {
                multiplier = 2;
            } else if (item == Items.DIAMOND_BLOCK) {
                multiplier = 9;
            }
            dissolver(item, createSet()
                    .addGroup(100, toStack("graphite", 64 * 2 * multiplier)).build());
        }

        for (Item item : newArrayList(Items.LAPIS_LAZULI, Items.LAPIS_BLOCK, Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE)) {
            int multiplier = 1;
            if (item == Items.LAPIS_BLOCK) multiplier = 9;
            else if (item == Items.LAPIS_ORE || item == Items.DEEPSLATE_LAPIS_ORE) multiplier = 4;
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("sodium", 6 * multiplier),
                            toStack("mullite", 3 * multiplier),
                            toStack("calcium_sulfide", 2 * multiplier),
                            toStack("silicon", 3 * multiplier))
                    .build());
        }

        for (Item item : newArrayList(Items.QUARTZ, Items.NETHER_QUARTZ_ORE, Items.QUARTZ_SLAB, Items.SMOOTH_QUARTZ_SLAB, Items.QUARTZ_BRICKS, Items.QUARTZ_PILLAR, Items.CHISELED_QUARTZ_BLOCK, Items.SMOOTH_QUARTZ)) {
            int multiplier = 4;
            if (item == Items.QUARTZ) {
                multiplier = 1;
            } else if (item == Items.QUARTZ_SLAB || item == Items.SMOOTH_QUARTZ_SLAB) {
                multiplier = 2;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("barium", 16 * multiplier),
                            toStack("silicon_dioxide", 32 * multiplier))
                    .build());
        }
        dissolver("forge:storage_blocks/quartz", createSet()
                .addGroup(toStack("barium", 16 * 4), toStack("silicon_dioxide", 32 * 4)) .build());

        for (Item item : newArrayList(Items.REDSTONE, Items.DEEPSLATE_REDSTONE_ORE, Items.REDSTONE_ORE, Items.REDSTONE_BLOCK)) {
            int multiplier = 1;
            if (item == Items.REDSTONE_ORE || item == Items.DEEPSLATE_REDSTONE_ORE) {
                multiplier = 2;
            } else if (item == Items.REDSTONE_BLOCK) {
                multiplier = 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("iron_oxide", multiplier),
                            toStack("strontium_carbonate", multiplier))
                    .build());
        }

        for (Item item : newArrayList(Items.IRON_INGOT, Items.IRON_NUGGET, Items.DEEPSLATE_IRON_ORE, Items.IRON_ORE, Items.IRON_BLOCK, Items.RAW_IRON, Items.RAW_IRON_BLOCK)) {
            int multiplier = 1;
            if (item == Items.IRON_INGOT || item == Items.RAW_IRON) {
                multiplier = 16;
            } else if (item == Items.IRON_ORE || item == Items.DEEPSLATE_IRON_ORE) {
                multiplier = 32;
            } else if (item == Items.IRON_BLOCK || item == Items.RAW_IRON_BLOCK) {
                multiplier = 16 * 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("iron", multiplier))
                    .build());
        }

        for (Item item : newArrayList(Items.GOLD_INGOT, Items.GOLD_NUGGET, Items.DEEPSLATE_GOLD_ORE, Items.GOLD_ORE, Items.GOLD_BLOCK, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK, Items.NETHER_GOLD_ORE)) {
            int multiplier = 1;
            if (item == Items.GOLD_INGOT || item == Items.RAW_GOLD) {
                multiplier = 16;
            } else if (item == Items.GOLD_ORE || item == Items.DEEPSLATE_GOLD_ORE || item == Items.NETHER_GOLD_ORE) {
                multiplier = 32;
            } else if (item == Items.GOLD_BLOCK || item == Items.RAW_GOLD_BLOCK) {
                multiplier = 16 * 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("gold", multiplier))
                    .build());
        }

        for (Item item : newArrayList(Items.COPPER_INGOT, Items.DEEPSLATE_COPPER_ORE, Items.COPPER_ORE, Items.COPPER_BLOCK, Items.RAW_COPPER, Items.RAW_COPPER_BLOCK)) {
            int multiplier = 1;
            if (item == Items.COPPER_INGOT || item == Items.RAW_COPPER) {
                multiplier = 16;
            } else if (item == Items.COPPER_ORE || item == Items.DEEPSLATE_COPPER_ORE) {
                multiplier = 32;
            } else if (item == Items.COPPER_BLOCK || item == Items.RAW_COPPER_BLOCK) {
                multiplier = 16 * 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toStack("copper", multiplier))
                    .build());
        }

        for (Item item : newArrayList(Items.EXPOSED_COPPER, Items.EXPOSED_CUT_COPPER, Items.EXPOSED_CUT_COPPER_SLAB, Items.WEATHERED_COPPER, Items.WEATHERED_CUT_COPPER, Items.WEATHERED_CUT_COPPER_SLAB, Items.OXIDIZED_COPPER, Items.OXIDIZED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER_SLAB)) {
            int oxide = 5;
            int multiplier = 2;
            if (item == Items.WEATHERED_COPPER || item == Items.WEATHERED_CUT_COPPER || item == Items.WEATHERED_CUT_COPPER_SLAB) {
                oxide = 15;
            } else if (item == Items.OXIDIZED_COPPER || item == Items.OXIDIZED_CUT_COPPER || item == Items.OXIDIZED_CUT_COPPER_SLAB) {
                oxide = 25;
            }
            if (item == Items.WEATHERED_CUT_COPPER_SLAB || item == Items.EXPOSED_CUT_COPPER_SLAB || item == Items.OXIDIZED_CUT_COPPER_SLAB) {
                multiplier = 1;
            }
            dissolver(item, createSet().rolls(8 * multiplier)
                    .addGroup(100 - oxide, toStack("copper", 9))
                    .addGroup(oxide, toStack("copper_i_oxide", 9))
                    .build());
        }

        dissolver(Items.NETHERITE_SCRAP, createSet()
                .addGroup(100, toStack("tungsten", 16), toStack("carbon", 16)).build());
        dissolver(Items.ANCIENT_DEBRIS, createSet()
                .addGroup(100, toStack("tungsten", 16), toStack("carbon", 16)).build());

        dissolver(Items.AMETHYST_SHARD, createSet()
                .addGroup(100, toStack("silicon_dioxide", 1), toStack("iron", 1)).build(), true);
        dissolver(Items.AMETHYST_BLOCK, createSet()
                .weighted().rolls(4)
                .addGroup(1000, toStack("silicon_dioxide", 1), toStack("iron", 1))
                .addGroup(50, toStack("uranium", 1))
                .addGroup(1, toStack("polonium", 1))
                .build());

        // End
        newArrayList(Items.CHORUS_FLOWER, Items.CHORUS_FRUIT, Items.CHORUS_PLANT, Items.POPPED_CHORUS_FRUIT).forEach(item ->
                dissolver(item, createSet()
                        .addGroup(100, toStack("cellulose"))
                        .addGroup(50, toStack("lutetium"))
                        .build()));

        newArrayList(Items.PURPUR_BLOCK, Items.PURPUR_PILLAR).forEach(item ->
                dissolver(item, createSet()
                        .addGroup(100, toStack("silicon_dioxide", 4))
                        .addGroup(50, toStack("lutetium"))
                        .build()));

        dissolver(Items.PURPUR_SLAB, createSet()
                .addGroup(100, toStack("silicon_dioxide", 2))
                .addGroup(25, toStack("lutetium"))
                .build());

        for (Item item : newArrayList(Items.END_STONE, Items.END_STONE_BRICKS)) {
            dissolver(item, createSet().weighted()
                    .addGroup(50, toStack("mercury"))
                    .addGroup(5, toStack("neodymium"))
                    .addGroup(250, toStack("silicon_dioxide"))
                    .addGroup(50, toStack("lithium"))
                    .addGroup(2, toStack("thorium"))
                    .build());
        }

        dissolver(Items.END_STONE_BRICK_SLAB, createSet().weighted()
                .addGroup(350)
                .addGroup(50, toStack("mercury"))
                .addGroup(5, toStack("neodymium"))
                .addGroup(250, toStack("silicon_dioxide"))
                .addGroup(50, toStack("lithium"))
                .addGroup(2, toStack("thorium"))
                .build());

        dissolver(Items.ENDER_PEARL, createSet()
                .addGroup(100,
                    toStack("silicon", 16),
                    toStack("mercury", 16),
                    toStack("neodymium", 16))
                .build(), true);

        // Dyes
        dissolver("forge:dyes/white", createSet().addGroup(100, toStack("hydroxylapatite")).build());
        dissolver("forge:dyes/orange", createSet().addGroup(100, toStack("potassium_dichromate", 4)).build());
        dissolver("forge:dyes/magenta", createSet().addGroup(100, toStack("han_purple", 4)).build());
        dissolver("forge:dyes/light_blue", createSet().addGroup(100, toStack("cobalt_aluminate", 2), toStack("antimony_trioxide", 2)).build());
        dissolver("forge:dyes/yellow", createSet().addGroup(100, toStack("lead_iodide", 4)).build());
        dissolver("forge:dyes/lime", createSet().addGroup(100, toStack("cadmium_sulfide", 2), toStack("chromium_oxide", 2)).build());
        dissolver("forge:dyes/pink", createSet().addGroup(100, toStack("arsenic_sulfide", 4)).build());
        dissolver("forge:dyes/gray", createSet().addGroup(100, toStack("barium_sulfate", 4)).build());
        dissolver("forge:dyes/light_gray", createSet().addGroup(100, toStack("magnesium_sulfate", 4)).build());
        dissolver("forge:dyes/cyan", createSet().addGroup(100, toStack("copper_chloride", 4)).build());
        dissolver("forge:dyes/purple", createSet().addGroup(100, toStack("potassium_permanganate", 4)).build());
        dissolver("forge:dyes/blue", createSet().addGroup(100, toStack("cobalt_aluminate", 4)).build());
        dissolver("forge:dyes/brown", createSet().addGroup(100, toStack("cellulose", 4)).build());
        dissolver("forge:dyes/green", createSet().addGroup(100, toStack("nickel_chloride", 4)).build());
        dissolver("forge:dyes/red", createSet().addGroup(100, toStack("mercury_sulfide", 4)).build());
        dissolver("forge:dyes/black", createSet().addGroup(100, toStack("titanium_oxide", 4)).build());

        dissolver("minecraft:wool", createSet().addGroup(100, toStack("keratin", 2), toStack("triglyceride")).build());
        dissolver("minecraft:carpets", createSet().addGroup((2.0 / 3.0) * 100, toStack("keratin", 2), toStack("triglyceride")).build());

        dissolver("forge:glass", createSet().addGroup(100, toStack("silicon_dioxide", 4)).build());


        // mobs
        dissolver(Items.SLIME_BALL, createSet()
                .addGroup(100,
                        toStack("protein", 2),
                        toStack("sucrose", 2))
                .build(), true);
        dissolver(Items.SLIME_BLOCK, createSet().addGroup(100,toStack("protein", 9*2), toStack("sucrose", 9*2)).build(), true);
        dissolver(Items.COBWEB, createSet().addGroup(100, toStack("protein", 2)).build(), true);

        for (Item item : newArrayList(Items.TALL_GRASS, Items.SEAGRASS, Items.GRASS, Items.DEAD_BUSH)) {
            dissolver(item, createSet()
                    .addGroup(25, toStack("cellulose"))
            .build(), true);
        }

        dissolver(Items.FLINT, createSet().addGroup(1.0, toStack("silicon_dioxide", 3)).build(), true);

        dissolver(Items.COCOA_BEANS, createSet()
                .addGroup(100, toStack("caffeine"))
                .addGroup(50, toStack("cellulose"))
        .build(), true);


        dissolver(Items.APPLE, createSet().addGroup(100,
                toStack("sucrose"),
                toStack("cellulose"))
        .build(), true);

        for (Item item : newArrayList(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS)) {
            dissolver(item, createSet()
                    .addGroup(100, toStack("cellulose"))
                    .build());
        }

        dissolver(Items.SPIDER_EYE, createSet().addGroup(100,
                                toStack("beta_carotene", 2),
                                toStack("chitin", 2))
                        .build(), true);

        dissolver(Items.IRON_HORSE_ARMOR, createSet().addGroup(100, toStack("iron", 64)).build());
        dissolver(Items.GOLDEN_HORSE_ARMOR, createSet().addGroup(100, toStack("gold", 64)).build());
        dissolver(Items.DIAMOND_HORSE_ARMOR, createSet().addGroup(100, toStack("graphite", 64 * 8)).build());
        dissolver(Items.ANVIL, createSet().addGroup(100, toStack("iron", (144 * 3) + (16 * 4))).build());
        dissolver(Items.IRON_DOOR, createSet().addGroup(100, toStack("iron", 32)).build());
        dissolver(Items.IRON_TRAPDOOR, createSet().addGroup(100, toStack("iron", 64)).build());

        for (Item item : newArrayList(Items.SNOW_BLOCK, Items.ICE)) {
            dissolver(item, createSet().addGroup(100, toStack("water", 16)).build(), true);
        }

        dissolver(Items.PACKED_ICE, createSet().addGroup(100, toStack("water", 16 * 9)).build());
        dissolver(Items.BLUE_ICE, createSet().addGroup(100, toStack("water", 16 * 9 * 4)).build());
        dissolver(Items.SNOWBALL, createSet().addGroup(100, toStack("water", 4)).build(), true);
        dissolver(Items.SNOW, createSet().addGroup(100, toStack("water", 4)).build(), true);
        
        dissolver("minecraft:music_discs", createSet().addGroup(100,
                toStack("polyvinyl_chloride", 64),
                toStack("lead", 16),
                toStack("cadmium", 16))
        .build());

        dissolver(Items.JUKEBOX, createSet().addGroup(100,
                toStack("graphite", 64 * 2),
                toStack("cellulose", 2))
        .build());

        dissolver(Items.VINE, createSet().addGroup(25, toStack("cellulose", 1)).build());

        dissolver(Items.PAPER, createSet().addGroup(100, toStack("cellulose")).build(), true);

        dissolver(Items.LILY_PAD, createSet().addGroup(25, toStack("cellulose", 1)).build(), true);

        for (Item item : newArrayList(Items.PUMPKIN, Items.CARVED_PUMPKIN)) {
            dissolver(item, createSet().addGroup(50, toStack("cucurbitacin", 1)).build());
        }

        dissolver(Items.BROWN_MUSHROOM, createSet().addGroup(100,
                toStack("phosphoric_acid"),
                toStack("chitin"))
        .build(), true);

        dissolver(Items.RED_MUSHROOM, createSet().addGroup(100,
                toStack("phosphoric_acid"),
                toStack("chitin"))
        .build(), true);

        dissolver(Items.CRIMSON_FUNGUS, createSet().addGroup(100,
                toStack("chitin"),
                toStack("phosphoric_acid"),
                toStack("selenium"))
        .build(), true);


        dissolver(Items.WARPED_FUNGUS, createSet().addGroup(100,
                toStack("chitin"),
                toStack("phosphoric_acid"),
                toStack("mercury"))
        .build(), true);

        dissolver(Items.CRIMSON_ROOTS, createSet().addGroup(100,
                toStack("cellulose"),
                toStack("mercury_sulfide"))
        .build(), true);

        dissolver(Items.WARPED_ROOTS, createSet().addGroup(100,
                toStack("cellulose"),
                toStack("copper_chloride"))
        .build(), true);

        dissolver(Items.NETHER_SPROUTS, createSet().addGroup(100,
                toStack("cellulose"),
                toStack("copper_chloride"))
        .build(), true);

        dissolver(Items.TWISTING_VINES, createSet().addGroup(100,
                toStack("cellulose"),
                toStack("copper_chloride"))
        .build(), true);

        dissolver(Items.WEEPING_VINES, createSet().addGroup(100,
                toStack("cellulose"),
                toStack("mercury_sulfide"))
        .build(), true);

        for (Item item : newArrayList(Items.SOUL_SAND, Items.SOUL_SOIL)) {
            dissolver(item, createSet().addGroup(100,
                    toStack("thulium"),
                    toStack("silicon_dioxide", 4))
            .build(), true);
        }

        dissolver(Items.SUGAR_CANE, createSet().addGroup(100, toStack("sucrose")).build(), true);

        dissolver(Items.SUGAR, createSet().addGroup(100, toStack("sucrose")).build(), true);

        dissolver(Items.GUNPOWDER, createSet().addGroup(100,
                toStack("potassium_nitrate", 2),
                toStack("sulfur", 8),
                toStack("graphite", 2))
        .build(), true);

        dissolver(Items.BLAZE_POWDER, createSet().addGroup(100,
                toStack("germanium", 8),
                toStack("graphite", 2),
                toStack("sulfur", 8))
        .build(), true);

        dissolver(Items.NETHER_WART, createSet().addGroup(100,
                toStack("chitin"),
                toStack("germanium", 4),
                toStack("selenium", 4))
        .build(), true);

        dissolver(Items.NETHER_WART_BLOCK, createSet().addGroup(100,
                toStack("chitin", 9),
                toStack("germanium", 4 * 9),
                toStack("selenium", 4 * 9))
        .build());

        dissolver(Items.WARPED_WART_BLOCK, createSet().addGroup(100,
                                toStack("chitin", 9),
                                toStack("neodymium", 4),
                                toStack("mercury", 4))
                        .build());

        dissolver(Items.GLOWSTONE_DUST, createSet().addGroup(100, toStack("phosphorus", 4)).build(), true);

        dissolver(Items.GLOWSTONE, createSet().addGroup(100, toStack("phosphorus", 16)).build());

        dissolver(Items.IRON_BARS, createSet().addGroup(100, toStack("iron", 6)).build());

         dissolver(Items.STRING, createSet().addGroup(50, toStack("keratin")).build(), true);

        for (Item item : newArrayList(Items.WHEAT, Items.HAY_BLOCK)) {
            int rolls = 1;
            if (item == Items.HAY_BLOCK) rolls = 9;
            dissolver(item, createSet()
                    .rolls(rolls)
                    .addGroup(5, toStack("starch"))
                    .addGroup(25, toStack("cellulose"))
            .build());
        }

        dissolver(Items.MELON, createSet()
                .addGroup(50, toStack("cucurbitacin"))
                .addGroup(100, toStack("water", 4), toStack("sucrose", 2))
        .build());

        dissolver(Items.CACTUS, createSet()
                .addGroup(100,
                        toStack("cellulose"),
                        toStack("sucrose"),
                        toStack("nitrate"))
                .build(), true);

        // Terracotta
        for (Item item : newArrayList(Items.TERRACOTTA, Items.BLACK_GLAZED_TERRACOTTA, Items.BLACK_TERRACOTTA,
                Items.BLUE_GLAZED_TERRACOTTA, Items.BLUE_TERRACOTTA, Items.BROWN_GLAZED_TERRACOTTA, Items.BROWN_TERRACOTTA,
                Items.CYAN_GLAZED_TERRACOTTA, Items.CYAN_TERRACOTTA, Items.GRAY_GLAZED_TERRACOTTA, Items.GRAY_TERRACOTTA,
                Items.GREEN_GLAZED_TERRACOTTA, Items.GREEN_TERRACOTTA, Items.LIGHT_BLUE_GLAZED_TERRACOTTA, Items.LIGHT_BLUE_TERRACOTTA,
                Items.LIGHT_GRAY_GLAZED_TERRACOTTA, Items.LIGHT_GRAY_TERRACOTTA, Items.LIME_GLAZED_TERRACOTTA, Items.LIME_TERRACOTTA,
                Items.MAGENTA_GLAZED_TERRACOTTA, Items.MAGENTA_TERRACOTTA, Items.ORANGE_GLAZED_TERRACOTTA, Items.ORANGE_TERRACOTTA,
                Items.PINK_TERRACOTTA, Items.PINK_GLAZED_TERRACOTTA, Items.YELLOW_TERRACOTTA, Items.YELLOW_GLAZED_TERRACOTTA,
                Items.PURPLE_TERRACOTTA, Items.PURPLE_GLAZED_TERRACOTTA, Items.RED_TERRACOTTA, Items.RED_GLAZED_TERRACOTTA)) {
            dissolver(item, createSet().addGroup(100, toStack("kaolinite", 4)).build());
        }

        // Concrete
        for (Item item : newArrayList(Items.BLACK_CONCRETE_POWDER, Items.BLUE_CONCRETE_POWDER, Items.CYAN_CONCRETE_POWDER,
                Items.BROWN_CONCRETE_POWDER, Items.GRAY_CONCRETE_POWDER, Items.GREEN_CONCRETE_POWDER, Items.LIGHT_BLUE_CONCRETE_POWDER,
                Items.LIGHT_GRAY_CONCRETE_POWDER, Items.LIME_CONCRETE_POWDER, Items.MAGENTA_CONCRETE_POWDER,
                Items.ORANGE_CONCRETE_POWDER, Items.PINK_CONCRETE_POWDER, Items.PURPLE_CONCRETE_POWDER,
                Items.RED_CONCRETE_POWDER, Items.WHITE_CONCRETE_POWDER, Items.YELLOW_CONCRETE_POWDER)) {
            dissolver(item, createSet().addGroup(100, toStack("silicon_dioxide", 2)).build());
        }

        for (Item item : newArrayList(Items.BLACK_CONCRETE, Items.BLUE_CONCRETE, Items.CYAN_CONCRETE,
                Items.BROWN_CONCRETE, Items.GRAY_CONCRETE, Items.GREEN_CONCRETE, Items.LIGHT_BLUE_CONCRETE,
                Items.LIGHT_GRAY_CONCRETE, Items.LIME_CONCRETE, Items.MAGENTA_CONCRETE,
                Items.ORANGE_CONCRETE, Items.PINK_CONCRETE, Items.PURPLE_CONCRETE,
                Items.RED_CONCRETE, Items.WHITE_CONCRETE, Items.YELLOW_CONCRETE)) {
            dissolver(item, createSet().addGroup(100, toStack("silicon_dioxide", 2), toStack("water", 4)).build());
        }

        for (Item item : newArrayList(Items.DEAD_BRAIN_CORAL, Items.DEAD_BRAIN_CORAL_BLOCK, Items.DEAD_BRAIN_CORAL_FAN,
                Items.DEAD_BUBBLE_CORAL, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_FAN,
                Items.DEAD_FIRE_CORAL, Items.DEAD_FIRE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_FAN,
                Items.DEAD_HORN_CORAL, Items.DEAD_HORN_CORAL_BLOCK, Items.DEAD_HORN_CORAL_FAN,
                Items.DEAD_TUBE_CORAL, Items.DEAD_TUBE_CORAL_BLOCK, Items.DEAD_TUBE_CORAL_FAN)) {
            int quantity = 4;
            if (item == Items.DEAD_BRAIN_CORAL_BLOCK || item == Items.DEAD_BUBBLE_CORAL_BLOCK || item == Items.DEAD_FIRE_CORAL_BLOCK
                    || item == Items.DEAD_HORN_CORAL_BLOCK || item == Items.DEAD_TUBE_CORAL_BLOCK) {
                quantity = 16;
            }
            dissolver(item, createSet().addGroup(100, toStack("calcium_carbonate", quantity)).build());
        }

        for (Item item : newArrayList(Items.BRAIN_CORAL, Items.BRAIN_CORAL_BLOCK, Items.BRAIN_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.BRAIN_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100,
                    toStack("calcium_carbonate", multiplier),
                    toStack("arsenic_sulfide", (multiplier / 2)))
            .build());
        }
        for (Item item : newArrayList(Items.BUBBLE_CORAL, Items.BUBBLE_CORAL_BLOCK, Items.BUBBLE_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.BUBBLE_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100,
                    toStack("calcium_carbonate", multiplier),
                    toStack("han_purple", (multiplier / 2)))
            .build());
        }

        for (Item item : newArrayList(Items.FIRE_CORAL, Items.FIRE_CORAL_BLOCK, Items.FIRE_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.FIRE_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100, toStack("calcium_carbonate", multiplier),
                    toStack("cobalt_nitrate", (multiplier / 2))).build());
        }
        for (Item item : newArrayList(Items.HORN_CORAL, Items.HORN_CORAL_BLOCK, Items.HORN_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.HORN_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100, toStack("calcium_carbonate", multiplier),
                    toStack("lead_iodide", (multiplier / 2)))
            .build());
        }
        for (Item item : newArrayList(Items.TUBE_CORAL, Items.TUBE_CORAL_BLOCK, Items.TUBE_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.TUBE_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100, toStack("calcium_carbonate", multiplier),
                    toStack("cobalt_aluminate", (multiplier / 2)))
            .build());
        }

        dissolver(Items.KELP, createSet().weighted()
                .addGroup(100, toStack("cellulose"))
                .addGroup(17, toStack("sodium_carbonate", 2))
                .addGroup(5, toStack("potassium_iodide", 2))
        .build());

        dissolver(Items.DRIED_KELP, createSet()
                .addGroup(100, toStack("sodium_carbonate"),
                        toStack("potassium_iodide"))
        .build());

        dissolver(Items.DRIED_KELP_BLOCK, createSet()
                .addGroup(100, toStack("sodium_carbonate", 9),
                        toStack("potassium_iodide", 9))
        .build());

        dissolver(Items.SEA_PICKLE, createSet()
                .addGroup(100, toStack("cellulose"),
                        toStack("cadmium_sulfide", 2),
                        toStack("chromium_oxide", 2))
        .build());

        for (Item item : newArrayList(Items.POTATO, Items.BAKED_POTATO)) {
            dissolver(item, createSet().weighted()
                    .addGroup(10, toStack("starch"))
                    .addGroup(25, toStack("potassium", 5))
            .build());
        }

        for (Item item : newArrayList(Items.COOKED_PORKCHOP, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_CHICKEN, Items.COOKED_RABBIT)) {
            dissolver(item, createSet().addGroup(100, toStack("protein", 4)).build());
        }

        for (Item item : newArrayList(Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT)) {
            dissolver(item, createSet().addGroup(100, toStack("protein", 4)).build(), true);
        }

        for (Item item : newArrayList(Items.COD, Items.TROPICAL_FISH)) {
            dissolver(item, createSet().addGroup(100,
                    toStack("protein", 4),
                    toStack("selenium", 2))
            .build(), true);
        }
        dissolver(Items.COOKED_COD, createSet().addGroup(100,
                toStack("protein", 4),
                toStack("selenium", 2))
        .build());
        dissolver(Items.SALMON, createSet().addGroup(100,
                toStack("protein", 4),
                toStack("selenium", 4))
        .build(), true);
        dissolver(Items.COOKED_SALMON, createSet().addGroup(100,
                toStack("protein", 4),
                toStack("selenium", 4))
        .build());

        dissolver(Items.PUFFERFISH, createSet().addGroup(100,
                toStack("protein", 4),
                toStack("potassium_cyanide", 4))
        .build());

        dissolver(Items.SPONGE, createSet().addGroup(100,
                toStack("kaolinite", 8),
                toStack("calcium_carbonate", 8))
        .build(), true);

        dissolver(Items.LEATHER, createSet().addGroup(100, toStack("protein", 3)).build(), true);
        dissolver(Items.ROTTEN_FLESH, createSet().addGroup(100, toStack("protein", 3)).build());
        dissolver(Items.FEATHER, createSet().addGroup(100, toStack("protein", 2)).build(), true);
        dissolver(Items.BONE_MEAL, createSet().addGroup(50, toStack("hydroxylapatite")).build(), true);
        dissolver(Items.BONE_BLOCK, createSet().rolls(9).addGroup(50, toStack("hydroxylapatite")).build());

        dissolver(Items.EGG, createSet().addGroup(100,
                toStack("calcium_carbonate", 8),
                toStack("protein", 2))
        .build(), true);

        dissolver(Items.CARROT, createSet().addGroup(20, toStack("beta_carotene")).build());

        dissolver(Items.WITHER_SKELETON_SKULL, createSet().addGroup(100,
                toStack("hydroxylapatite", 8),
                toStack("mendelevium", 32))
        .build(), true);

        dissolver(Items.SKELETON_SKULL, createSet().addGroup(100, toStack("hydroxylapatite", 8)).build(), true);
        dissolver(Items.BELL, createSet().addGroup(100, toStack("gold", 64)).build());

        dissolver(Items.SHROOMLIGHT, createSet().addGroup(100, toStack("phosphorus", 16),
                        toStack("chitin"), toStack("phosphoric_acid")).build());

        dissolver(Items.HONEYCOMB, createSet().addGroup(100, toStack("sucrose", 3)).build());
        dissolver(Items.HONEY_BOTTLE, createSet().addGroup(100, toStack("sucrose", 3)).build());
        dissolver(Items.HONEY_BLOCK, createSet().addGroup(100, toStack("sucrose", 12)).build());
        dissolver(Items.HONEYCOMB_BLOCK, createSet().addGroup(100, toStack("sucrose", 12)).build());

        dissolver(Items.TURTLE_EGG, createSet().addGroup(100, toStack("protein", 4), toStack("calcium_carbonate", 8)).build());

        dissolver(Items.SCUTE, createSet().addGroup(100, toStack("protein", 2)).build());

        dissolver(Items.GOLDEN_APPLE, createSet().addGroup(100, toStack("gold", 8 * 16),
                toStack("cellulose"),
                toStack("sucrose"))
        .build());

        dissolver(Items.BEETROOT, createSet()
                .addGroup(100, toStack("sucrose"))
                .addGroup(50, toStack("iron_oxide"))
        .build());

        dissolver(Items.BONE, createSet()
                .addGroup(50, toStack("hydroxylapatite", 3))
        .build(), true);

        dissolver(Items.OBSIDIAN, createSet().addGroup(100,
                toStack("magnesium_oxide", 8),
                toStack("potassium_chloride", 8),
                toStack("aluminum_oxide", 8),
                toStack("silicon_dioxide", 24))
        .build(), true);

        dissolver(Items.BAMBOO, createSet().addGroup(100, toStack("cellulose")).build(), true);

        dissolver(Items.RABBIT_FOOT, createSet()
                .addGroup(100, toStack("protein", 2))
                .addGroup(50, toStack("keratin", 1))
                .build(), true);

        dissolver(Items.RABBIT_HIDE, createSet().addGroup(100, toStack("protein", 2)).build(), true);

        dissolver(Items.SHULKER_SHELL, createSet().addGroup(100, toStack("calcium_carbonate", 8), toStack("lutetium", 8)).build(), true);

        dissolver(Items.DRAGON_BREATH, createSet().addGroup(100,
                toStack("xenon", 8),
                toStack("radon", 8),
                toStack("oganesson", 8))
        .build());

        dissolver(Items.GHAST_TEAR, createSet().addGroup(100, toStack("polonium", 16)).build(), true);
        dissolver(Items.NAUTILUS_SHELL, createSet().addGroup(100, toStack("calcium_carbonate", 16)).build());
        dissolver(Items.PHANTOM_MEMBRANE, createSet().addGroup(100, toStack("cerium", 8)).build(), true);
        dissolver(Items.SWEET_BERRIES, createSet().addGroup(100, toStack("cellulose"), toStack("sucrose")).build(), true);
        dissolver(Items.CHAIN, createSet().addGroup(100, toStack("iron", 18)).build());
        dissolver(Items.INK_SAC, createSet().addGroup(100, toStack("titanium_oxide", 4)).build());

        ItemRegistry.getElements().stream()
            .filter(element -> element.getMetalType().equals(MetalType.METAL) && element.getMatterState().equals(MatterState.SOLID))
            .forEach(element -> {
                String name = element.getChemicalName();
                Arrays.stream(ChemicalItemType.values()).sequential()
                    .filter(type -> !type.equals(ChemicalItemType.COMPOUND))
                    .forEach(type -> {
                        int count = switch (type) {
                            case DUST, INGOT, PLATE -> 16;
                            default -> 1;
                        };
                        ItemRegistry.getChemicalItemByNameAndType(name, type).ifPresent(item -> dissolver(item, createSet().addGroup(100, toStack(name, count)).build()));
                    });
                ItemRegistry.getChemicalBlockItemByName(String.format("%s_metal_block", name)).ifPresent(blockItem -> dissolver(blockItem, createSet().addGroup(100, toStack(name, 144)).build()));
            });
    }

    private void dissolver(Ingredient pIngredient, ProbabilitySet pSet, ResourceLocation pRecipeId) {
        DissolverRecipeBuilder.createRecipe(pIngredient, pSet, pRecipeId)
                .group(String.format("%s:dissolver", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .save(consumer, pRecipeId);
    }

    private void dissolver(ItemLike pItemLike, ProbabilitySet pSet) {
        dissolver(pItemLike, pSet, false);
    }

    private void dissolver(ItemLike pItemLike, ProbabilitySet pSet, boolean pReversible) {
        dissolver(Ingredient.of(pItemLike), pSet, Objects.requireNonNull(pItemLike.asItem().getRegistryName()));

        if (pReversible) {
            ItemStack output = new ItemStack(pItemLike);
            List<ItemStack> items = new ArrayList<>();

            pSet.getProbabilityGroups().stream().map(ProbabilityGroup::getOutput).forEach(items::addAll);

            if (items.size() <= 4) {
                CombinerRecipeBuilder.createRecipe(output, items)
                        .group(Alchemistry.MODID + ":combiner")
                        .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(RecipeUtils.getLocation(output, "combiner")))
                        .save(consumer);
            }
        }
    }

    private void dissolver(String pItemTag, ProbabilitySet pSet) {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(pItemTag));
        Ingredient ingredient = Ingredient.of(tagKey);
        dissolver(ingredient, pSet, new ResourceLocation(pItemTag));
    }
}
