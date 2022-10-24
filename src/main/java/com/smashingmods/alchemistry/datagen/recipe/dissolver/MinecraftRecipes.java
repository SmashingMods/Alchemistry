package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

import static com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet.Builder.createSet;
import static com.smashingmods.alchemistry.datagen.DatagenUtil.tagNotEmptyCondition;
import static com.smashingmods.alchemistry.datagen.DatagenUtil.toItemStack;

public class MinecraftRecipes extends DissolverRecipeProvider {

    public MinecraftRecipes(Consumer<FinishedRecipe> pConsumer) {
        super(pConsumer);
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new MinecraftRecipes(pConsumer).register();
    }

    protected void register() {
        // Stones
        for (Item item : List.of(Items.STONE, Items.SMOOTH_STONE, Items.STONE_BRICK_SLAB, Items.STONE_BRICKS, Items.CRACKED_STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.STONE_SLAB, Items.SMOOTH_STONE_SLAB)) {
            int rolls = 2;
            if (item == Items.STONE_BRICK_SLAB || item == Items.SMOOTH_STONE_SLAB || item == Items.STONE_SLAB) {
                rolls = 1;
            }
            dissolver(item, createSet().rolls(rolls).weighted()
                    .addGroup(20)
                    .addGroup(1, toItemStack("aluminum"))
                    .addGroup(2, toItemStack("iron"))
                    .addGroup(.7, toItemStack("gold"))
                    .addGroup(5, toItemStack("silicon_dioxide"))
                    .addGroup(.3, toItemStack("dysprosium"))
                    .addGroup(.6, toItemStack("zirconium"))
                    .addGroup(.5, toItemStack("tungsten"))
                    .addGroup(.5, toItemStack("nickel"))
                    .addGroup(.5, toItemStack("gallium"))
                    .build());
        }

        List.of(Items.ANDESITE, Items.POLISHED_ANDESITE).forEach(item ->
                dissolver(item, createSet().weighted()
                        .addGroup(4, toItemStack("aluminum_oxide"))
                        .addGroup(3, toItemStack("iron"))
                        .addGroup(4, toItemStack("potassium_chloride"))
                        .addGroup(10, toItemStack("silicon_dioxide"))
                        .addGroup(2, toItemStack("platinum"))
                        .addGroup(4, toItemStack("calcium"))
                        .build()));

        List.of(Items.DIORITE, Items.POLISHED_DIORITE).forEach(item ->
                dissolver(item, createSet().weighted()
                        .addGroup(4, toItemStack("aluminum_oxide"))
                        .addGroup(2, toItemStack("iron"))
                        .addGroup(4, toItemStack("potassium_chloride"))
                        .addGroup(10, toItemStack("silicon_dioxide"))
                        .addGroup(1.5, toItemStack("indium"))
                        .addGroup(2, toItemStack("manganese"))
                        .addGroup(2, toItemStack("osmium"))
                        .addGroup(3, toItemStack("tin"))
                        .build()));

        List.of(Items.GRANITE, Items.POLISHED_GRANITE).forEach(item ->
                dissolver(item, createSet().weighted()
                        .addGroup(5, toItemStack("aluminum_oxide"))
                        .addGroup(2, toItemStack("iron"))
                        .addGroup(2, toItemStack("potassium_chloride"))
                        .addGroup(10, toItemStack("silicon_dioxide"))
                        .addGroup(1, toItemStack("technetium"))
                        .addGroup(1.5, toItemStack("manganese"))
                        .addGroup(1.5, toItemStack("radium"))
                        .build()));

        for (Item item : List.of(Items.DEEPSLATE, Items.DEEPSLATE_BRICKS, Items.DEEPSLATE_BRICK_SLAB, Items.DEEPSLATE_TILES, Items.DEEPSLATE_TILE_SLAB, Items.COBBLED_DEEPSLATE, Items.COBBLED_DEEPSLATE_SLAB, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE, Items.POLISHED_DEEPSLATE_SLAB)) {
            int rolls;
            if (item == Items.DEEPSLATE_BRICK_SLAB || item == Items.COBBLED_DEEPSLATE_SLAB || item == Items.POLISHED_DEEPSLATE_SLAB) {
                rolls = 1;
            } else {
                rolls = 2;
            }
            dissolver(item, createSet().rolls(rolls).weighted()
                    .addGroup(40)
                    .addGroup(2, toItemStack("osmium"))
                    .addGroup(4, toItemStack("iron"))
                    .addGroup(2, toItemStack("gold"))
                    .addGroup(2, toItemStack("aluminum"))
                    .addGroup(20, toItemStack("silicon_dioxide"))
                    .addGroup(2, toItemStack("silver"))
                    .addGroup(1, toItemStack("terbium"))
                    .addGroup(1, toItemStack("europium"))
                    .addGroup(1, toItemStack("scandium"))
                    .addGroup(1, toItemStack("yttrium"))
                    .build());
        }

        dissolver(Items.CALCITE, createSet()
                .addGroup(100, toItemStack("calcium_carbonate"))
                .build());

        dissolver(Items.NETHERRACK, createSet().weighted()
                .addGroup(15)
                .addGroup(2, toItemStack("zinc_oxide"))
                .addGroup(1, toItemStack("gold"))
                .addGroup(1, toItemStack("phosphorus"))
                .addGroup(3, toItemStack("sulfur"))
                .addGroup(1, toItemStack("germanium"))
                .addGroup(4, toItemStack("silicon"))
                .build());

        for (Item item : List.of(Items.NETHER_BRICK, Items.NETHER_BRICKS, Items.CRACKED_NETHER_BRICKS, Items.CHISELED_NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.NETHER_BRICK_SLAB, Items.RED_NETHER_BRICK_SLAB)){
            int rolls = 4;
            if (item == Items.NETHER_BRICK) {
                rolls = 1;
            }
            else if (item == Items.NETHER_BRICK_SLAB || item == Items.RED_NETHER_BRICK_SLAB) {
                rolls = 2;
            }
            dissolver(item, createSet().weighted().rolls(rolls)
                    .addGroup(5)
                    .addGroup(2, toItemStack("zinc_oxide"))
                    .addGroup(1, toItemStack("gold"))
                    .addGroup(1, toItemStack("phosphorus"))
                    .addGroup(4, toItemStack("sulfur"))
                    .addGroup(1, toItemStack("germanium"))
                    .addGroup(4, toItemStack("silicon"))
                    .build());
        }

        for (Item item : List.of(Items.CLAY_BALL, Items.BRICK)) {
            dissolver(item, createSet()
                    .addGroup(100, toItemStack("kaolinite", 1))
                    .build());
        }

        for (Item item : List.of(Items.CLAY, Items.BRICKS)) {
            dissolver(item, createSet()
                    .addGroup(100, toItemStack("kaolinite", 4))
                    .build());
        }

        dissolver(Items.BRICK_SLAB, createSet()
                .addGroup(100, toItemStack("kaolinite", 2))
                .build());

        // Dirt
        dissolver("minecraft:dirt", createSet().weighted()
                .addGroup(3, toItemStack("water"))
                .addGroup(5, toItemStack("silicon_dioxide"))
                .addGroup(1, toItemStack("cellulose"))
                .addGroup(1, toItemStack("kaolinite"))
                .build());

        dissolver(Items.MYCELIUM, createSet().weighted()
                .addGroup(3, toItemStack("water"))
                .addGroup(5, toItemStack("silicon_dioxide"))
                .addGroup(1, toItemStack("cellulose"))
                .addGroup(1, toItemStack("kaolinite"))
                .addGroup(3, toItemStack("chitin"))
                .build());

        dissolver(Items.MOSS_BLOCK, createSet()
                .addGroup(100, toItemStack("water"))
                .addGroup(100, toItemStack("cellulose"))
                .build(), true);

        dissolver(Items.MOSS_CARPET, createSet()
                .addGroup(66.6, toItemStack("water"))
                .addGroup(66.6, toItemStack("cellulose"))
                .build());

        dissolver(Items.CRIMSON_NYLIUM, createSet().weighted()
                .addGroup(10)
                .addGroup(2, toItemStack("cellulose"))
                .addGroup(4, toItemStack("zinc_oxide"))
                .addGroup(1, toItemStack("gold"))
                .addGroup(1, toItemStack("phosphorus"))
                .addGroup(3, toItemStack("sulfur"))
                .addGroup(3, toItemStack("germanium"))
                .addGroup(5, toItemStack("silicon"))
                .addGroup(1, toItemStack("selenium"))
                .build());

        dissolver(Items.WARPED_NYLIUM, createSet().weighted()
                .addGroup(10)
                .addGroup(2, toItemStack("cellulose"))
                .addGroup(4, toItemStack("zinc_oxide"))
                .addGroup(1, toItemStack("gold"))
                .addGroup(1, toItemStack("phosphorus"))
                .addGroup(3, toItemStack("sulfur"))
                .addGroup(1, toItemStack("mercury"))
                .addGroup(5, toItemStack("silicon"))
                .addGroup(1, toItemStack("neodymium"))
                .build());

        // Woods
        dissolver("minecraft:logs", createSet()
                .addGroup(100, toItemStack("cellulose"))
                .build());

        dissolver("minecraft:planks", createSet()
                .addGroup(25, toItemStack("cellulose"))
                .build());

        dissolver("minecraft:wooden_slabs", createSet()
                .addGroup(12.5, toItemStack("cellulose"))
                .build());

        dissolver("forge:fences/wooden", createSet()
                .addGroup(33.3334, toItemStack("cellulose"))
                .build());

        dissolver("forge:fence_gates/wooden", createSet()
                .addGroup(100, toItemStack("cellulose"))
                .build());

        dissolver("forge:chests/wooden", createSet()
                .addGroup(100, toItemStack("cellulose", 2))
                .build());

        dissolver("minecraft:leaves", createSet()
                .addGroup(5, toItemStack("cellulose"))
                .build());

        dissolver("minecraft:saplings", createSet()
                .addGroup(25, toItemStack("cellulose"))
                .build());


        dissolver(Items.STICK, createSet().addGroup(10, toItemStack("cellulose")).build());
        dissolver(Items.HANGING_ROOTS, createSet().addGroup(50, toItemStack("cellulose")).build());
        dissolver(Items.BIG_DRIPLEAF, createSet().addGroup(100, toItemStack("cellulose")).build());
        dissolver(Items.SMALL_DRIPLEAF, createSet().addGroup(50, toItemStack("cellulose")).build());
        dissolver(Items.SPORE_BLOSSOM, createSet().addGroup(66.6, toItemStack("cellulose")).build());
        dissolver(Items.GLOW_LICHEN, createSet().addGroup(50, toItemStack("cellulose")).addGroup(50, toItemStack("phosphorus")).build());

        dissolver(Items.CRAFTING_TABLE, createSet().addGroup(100, toItemStack("cellulose")).build());

        // Sands
        dissolver(Items.SAND, createSet().weighted()
                .addGroup(100, toItemStack("silicon_dioxide", 4))
                .addGroup(1, toItemStack("gold"))
                .build());

        for (Item item : List.of(Items.SANDSTONE, Items.CHISELED_SANDSTONE, Items.CUT_SANDSTONE, Items.SMOOTH_SANDSTONE, Items.SANDSTONE_SLAB, Items.SMOOTH_SANDSTONE_SLAB)) {
            int rolls;
            if (item == Items.SANDSTONE_SLAB || item == Items.SMOOTH_SANDSTONE_SLAB) {
                rolls = 2;
            } else {
                rolls = 4;
            }
            dissolver(item, createSet().weighted().rolls(rolls)
                    .addGroup(100, toItemStack("silicon_dioxide", 4))
                    .addGroup(1, toItemStack("gold"))
                    .build());
        }

        dissolver(Items.RED_SAND, createSet().weighted()
                .addGroup(100, toItemStack("silicon_dioxide", 4))
                .addGroup(10, toItemStack("iron_oxide"))
                .build());

        for (Item item : List.of(Items.RED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.RED_SANDSTONE_SLAB, Items.CHISELED_RED_SANDSTONE, Items.CUT_RED_SANDSTONE_SLAB, Items.SMOOTH_RED_SANDSTONE_SLAB)) {
            int rolls;
            if (item == Items.RED_SANDSTONE_SLAB || item == Items.CUT_RED_SANDSTONE_SLAB || item == Items.SMOOTH_RED_SANDSTONE_SLAB) {
                rolls = 2;
            } else {
                rolls = 4;
            }
            dissolver(item, createSet().weighted().rolls(rolls)
                    .addGroup(100, toItemStack("silicon_dioxide", 4))
                    .addGroup(10, toItemStack("iron_oxide"))
                    .build());
        }

        dissolver(Items.GRAVEL, createSet()
                .addGroup(100, toItemStack("silicon_dioxide")).build());

        dissolver(Items.COBBLESTONE, createSet().weighted()
                .addGroup(700)
                .addGroup(2, toItemStack("aluminum"))
                .addGroup(4, toItemStack("iron"))
                .addGroup(1.5, toItemStack("gold"))
                .addGroup(10, toItemStack("silicon_dioxide"))
                .addGroup(1, toItemStack("dysprosium"))
                .addGroup(1.5, toItemStack("zirconium"))
                .addGroup(1, toItemStack("nickel"))
                .addGroup(1, toItemStack("gallium"))
                .addGroup(1, toItemStack("tungsten"))
                .build());

        dissolver(Items.POINTED_DRIPSTONE, createSet().weighted()
                .addGroup(10)
                .addGroup(10, toItemStack("calcium_carbonate"))
                .addGroup(4, toItemStack("iron"))
                .addGroup(1.5, toItemStack("gold"))
                .addGroup(5, toItemStack("silicon_dioxide"))
                .build());

        dissolver(Items.DRIPSTONE_BLOCK, createSet().weighted().rolls(4)
                .addGroup(10)
                .addGroup(10, toItemStack("calcium_carbonate"))
                .addGroup(4, toItemStack("iron"))
                .addGroup(1.5, toItemStack("gold"))
                .addGroup(5, toItemStack("silicon_dioxide"))
                .build());

        dissolver(Items.COBBLESTONE_SLAB, createSet().weighted()
                .addGroup(1400)
                .addGroup(2, toItemStack("aluminum"))
                .addGroup(4, toItemStack("iron"))
                .addGroup(1.5, toItemStack("gold"))
                .addGroup(10, toItemStack("silicon_dioxide"))
                .addGroup(1, toItemStack("dysprosium"))
                .addGroup(1.5, toItemStack("zirconium"))
                .addGroup(1, toItemStack("nickel"))
                .addGroup(1, toItemStack("gallium"))
                .addGroup(1, toItemStack("tungsten"))
                .build());

        dissolver(Items.MOSSY_COBBLESTONE, createSet().weighted()
                .addGroup(697)
                .addGroup(2, toItemStack("aluminum"))
                .addGroup(4, toItemStack("iron"))
                .addGroup(1.5, toItemStack("gold"))
                .addGroup(10, toItemStack("silicon_dioxide"))
                .addGroup(1, toItemStack("dysprosium"))
                .addGroup(1.5, toItemStack("zirconium"))
                .addGroup(1, toItemStack("nickel"))
                .addGroup(1, toItemStack("gallium"))
                .addGroup(1, toItemStack("tungsten"))
                .addGroup(3, toItemStack("cellulose"))
                .build());

        dissolver(Items.MOSSY_COBBLESTONE_SLAB, createSet().weighted()
                .addGroup(1394)
                .addGroup(2, toItemStack("aluminum"))
                .addGroup(4, toItemStack("iron"))
                .addGroup(1.5, toItemStack("gold"))
                .addGroup(10, toItemStack("silicon_dioxide"))
                .addGroup(1, toItemStack("dysprosium"))
                .addGroup(1.5, toItemStack("zirconium"))
                .addGroup(1, toItemStack("nickel"))
                .addGroup(1, toItemStack("gallium"))
                .addGroup(1, toItemStack("tungsten"))
                .addGroup(3, toItemStack("cellulose"))
                .build());

        dissolver(Items.MAGMA_BLOCK, createSet().weighted().rolls(2)
                .addGroup(10, toItemStack("manganese", 2))
                .addGroup(5, toItemStack("aluminum_oxide"))
                .addGroup(20, toItemStack("magnesium_oxide"))
                .addGroup(2, toItemStack("potassium_chloride"))
                .addGroup(10, toItemStack("silicon_dioxide", 2))
                .addGroup(20, toItemStack("sulfur", 2))
                .addGroup(10, toItemStack("iron_oxide"))
                .addGroup(8, toItemStack("lead", 2))
                .addGroup(4, toItemStack("fluorine"))
                .addGroup(4, toItemStack("bromine"))
                .build());

        for (Item item : List.of(Items.PRISMARINE_SHARD, Items.PRISMARINE, Items.PRISMARINE_BRICKS)) {
            int multiplier = 1;
            if (item == Items.PRISMARINE) {
                multiplier = 4;
            } else if (item == Items.PRISMARINE_BRICKS) {
                multiplier = 9;
            }
            dissolver(item, createSet().addGroup(100,
                    toItemStack("beryl", 4 * multiplier),
                    toItemStack("niobium", 3 * multiplier),
                    toItemStack("selenium", multiplier)).build(), true);
        }

        dissolver(Items.PRISMARINE_CRYSTALS, createSet().addGroup(100,
                toItemStack("silicon_dioxide", 4),
                toItemStack("phosphorus", 3),
                toItemStack("selenium", 1)).build(), true);

        dissolver(Items.DARK_PRISMARINE, createSet().addGroup(100,
                toItemStack("beryl", 32),
                toItemStack("niobium", 24),
                toItemStack("selenium", 8),
                toItemStack("titanium_oxide", 4)).build(), true);

        for (Item item : List.of(Items.BASALT, Items.POLISHED_BASALT, Items.BLACKSTONE,
                Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, Items.SMOOTH_BASALT, Items.POLISHED_BLACKSTONE_BRICKS, Items.POLISHED_BLACKSTONE_SLAB, Items.BLACKSTONE_SLAB, Items.POLISHED_BLACKSTONE_BRICK_SLAB)) {
            double multiplier = 1;
            if (item == Items.BLACKSTONE_SLAB || item == Items.POLISHED_BLACKSTONE_SLAB || item == Items.POLISHED_BLACKSTONE_BRICK_SLAB) {
                multiplier = 0.5;
            }
            dissolver(item, createSet().weighted()
                    .addGroup(49 * multiplier, toItemStack("silicon_dioxide"))
                    .addGroup(5 * multiplier, toItemStack("sodium_oxide"))
                    .addGroup(2 * multiplier, toItemStack("potassium_oxide"))
                    .addGroup(2 * multiplier, toItemStack("titanium_oxide"))
                    .addGroup(7 * multiplier, toItemStack("iron_ii_oxide"))
                    .addGroup(15 * multiplier, toItemStack("aluminum_oxide"))
                    .addGroup(10 * multiplier, toItemStack("calcium_oxide"))
                    .addGroup(10 * multiplier, toItemStack("magnesium_oxide"))
                    .build());
        }

        dissolver(Items.GILDED_BLACKSTONE, createSet()
                .addGroup(29, toItemStack("silicon_dioxide"))
                .addGroup(20, toItemStack("gold", 8))
                .addGroup(5, toItemStack("sodium_oxide"))
                .addGroup(2, toItemStack("potassium_oxide"))
                .addGroup(2, toItemStack("titanium_oxide"))
                .addGroup(7, toItemStack("iron_ii_oxide"))
                .addGroup(15, toItemStack("aluminum_oxide"))
                .addGroup(10, toItemStack("calcium_oxide"))
                .addGroup(10, toItemStack("magnesium_oxide"))
                .build());

        // Ores
        dissolver("forge:ores/coal", createSet()
                .addGroup(100, toItemStack("graphite", 8), toItemStack("sulfur", 8))
                .build());

        dissolver(Items.COAL_BLOCK, createSet().addGroup(100, toItemStack("graphite", 9 * 2)).build());

        dissolver(Items.COAL, createSet().addGroup(100, toItemStack("graphite", 2)).build());

        dissolver(Items.CHARCOAL, createSet().addGroup(100, toItemStack("graphite", 2)).build());

        dissolver("forge:storage_blocks/charcoal", createSet().addGroup(100, toItemStack("graphite", 18)).build(), tagNotEmptyCondition("forge:storage_blocks/charcoal"));

        for (Item item : List.of(Items.EMERALD, Items.EMERALD_BLOCK)) {
            int multiplier = 1;
            if (item == Items.EMERALD_BLOCK) {
                multiplier = 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("beryl", 8 * multiplier),
                            toItemStack("chromium", 8 * multiplier),
                            toItemStack("vanadium", 4 * multiplier))
                    .build());
        }
        dissolver("forge:ores/emerald", createSet()
                .addGroup(100,
                        toItemStack("beryl", 8 * 2),
                        toItemStack("chromium", 8 * 2),
                        toItemStack("vanadium", 4 * 2))
                .build());

        for (Item item : List.of(Items.DIAMOND, Items.DIAMOND_BLOCK)) {
            int multiplier = 1;
            if (item == Items.DIAMOND_BLOCK) {
                multiplier = 9;
            }
            dissolver(item, createSet()
                    .addGroup(100, toItemStack("graphite", 64 * 2 * multiplier)).build());
        }
        dissolver("forge:ores/diamond", createSet()
                .addGroup(100, toItemStack("graphite", 64 * 2 * 2)).build());

        for (Item item : List.of(Items.LAPIS_LAZULI, Items.LAPIS_BLOCK)) {
            int multiplier = 1;
            if (item == Items.LAPIS_BLOCK) multiplier = 9;
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("sodium", 6 * multiplier),
                            toItemStack("mullite", 3 * multiplier),
                            toItemStack("calcium_sulfide", 2 * multiplier),
                            toItemStack("silicon", 3 * multiplier))
                    .build());
        }

        dissolver("forge:ores/lapis", createSet()
                .addGroup(100,
                        toItemStack("sodium", 6 * 9),
                        toItemStack("mullite", 3 * 9),
                        toItemStack("calcium_sulfide", 2 * 9),
                        toItemStack("silicon", 3 * 9))
                .build());

        for (Item item : List.of(Items.QUARTZ, Items.QUARTZ_SLAB, Items.SMOOTH_QUARTZ_SLAB, Items.QUARTZ_BRICKS, Items.QUARTZ_PILLAR, Items.CHISELED_QUARTZ_BLOCK, Items.SMOOTH_QUARTZ)) {
            int multiplier = 4;
            if (item == Items.QUARTZ) {
                multiplier = 1;
            } else if (item == Items.QUARTZ_SLAB || item == Items.SMOOTH_QUARTZ_SLAB) {
                multiplier = 2;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("barium", 16 * multiplier),
                            toItemStack("silicon_dioxide", 32 * multiplier))
                    .build());
        }
        dissolver("forge:storage_blocks/quartz", createSet()
                .addGroup(100, toItemStack("barium", 16 * 4), toItemStack("silicon_dioxide", 32 * 4)) .build());
        dissolver("forge:ores/quartz", createSet()
                .addGroup(100, toItemStack("barium", 16 * 4), toItemStack("silicon_dioxide", 32 * 4)) .build());


        for (Item item : List.of(Items.REDSTONE, Items.REDSTONE_BLOCK)) {
            int multiplier = 1;
            if (item == Items.REDSTONE_BLOCK) {
                multiplier = 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("iron_oxide", multiplier),
                            toItemStack("strontium_carbonate", multiplier))
                    .build());
        }
        dissolver("forge:ores/redstone", createSet()
                .addGroup(100,
                        toItemStack("iron_oxide", 6),
                        toItemStack("strontium_carbonate", 6))
                .build());

        for (Item item : List.of(Items.IRON_INGOT, Items.IRON_NUGGET, Items.IRON_BLOCK, Items.RAW_IRON, Items.RAW_IRON_BLOCK)) {
            int multiplier = 1;
            if (item == Items.IRON_INGOT || item == Items.RAW_IRON) {
                multiplier = 16;
            } else if (item == Items.IRON_BLOCK || item == Items.RAW_IRON_BLOCK) {
                multiplier = 16 * 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("iron", multiplier))
                    .build());
        }
//        dissolver("forge:ores/iron", createSet().addGroup(100, toItemStack("iron", 32)).build());

        for (Item item : List.of(Items.GOLD_INGOT, Items.GOLD_NUGGET, Items.GOLD_BLOCK, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK)) {
            int multiplier = 1;
            if (item == Items.GOLD_INGOT || item == Items.RAW_GOLD) {
                multiplier = 16;
            } else if (item == Items.GOLD_BLOCK || item == Items.RAW_GOLD_BLOCK) {
                multiplier = 16 * 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("gold", multiplier))
                    .build());
        }
//        dissolver("forge:ores/gold", createSet().addGroup(100, toItemStack("gold", 32)).build());

        for (Item item : List.of(Items.COPPER_INGOT, Items.COPPER_BLOCK, Items.RAW_COPPER, Items.RAW_COPPER_BLOCK, Items.CUT_COPPER)) {
            int multiplier = 1;
            if (item == Items.COPPER_INGOT || item == Items.RAW_COPPER) {
                multiplier = 16;
            } else if (item == Items.COPPER_BLOCK || item == Items.RAW_COPPER_BLOCK) {
                multiplier = 16 * 9;
            }
            dissolver(item, createSet()
                    .addGroup(100,
                            toItemStack("copper", multiplier))
                    .build());
        }
//        dissolver("forge:ores/copper", createSet().addGroup(100, toItemStack("copper", 32)).build());

        for (Item item : List.of(Items.EXPOSED_COPPER, Items.EXPOSED_CUT_COPPER, Items.EXPOSED_CUT_COPPER_SLAB, Items.WEATHERED_COPPER, Items.WEATHERED_CUT_COPPER, Items.WEATHERED_CUT_COPPER_SLAB, Items.OXIDIZED_COPPER, Items.OXIDIZED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER_SLAB)) {
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
                    .addGroup(100 - oxide, toItemStack("copper", 9))
                    .addGroup(oxide, toItemStack("copper_i_oxide", 9))
                    .build());
        }

        dissolver(Items.NETHERITE_SCRAP, createSet()
                .addGroup(100, toItemStack("tungsten", 16), toItemStack("carbon", 16)).build());
        dissolver(Items.NETHERITE_INGOT, createSet()
                .addGroup(100, toItemStack("tungsten", 16 * 4), toItemStack("carbon", 16 * 4), toItemStack("gold", 16 * 4)).build());
        dissolver(Items.NETHERITE_BLOCK, createSet()
                .addGroup(100, toItemStack("tungsten", 16 * 4 * 9), toItemStack("carbon", 16 * 4 * 9), toItemStack("gold", 16 * 4 * 9)).build());
        dissolver(Items.ANCIENT_DEBRIS, createSet()
                .addGroup(100, toItemStack("tungsten", 16), toItemStack("carbon", 16)).build());

        dissolver(Items.AMETHYST_SHARD, createSet()
                .addGroup(100, toItemStack("silicon_dioxide", 1), toItemStack("iron", 1)).build(), true);
        dissolver(Items.AMETHYST_BLOCK, createSet()
                .weighted().rolls(4)
                .addGroup(1000, toItemStack("silicon_dioxide", 1), toItemStack("iron", 1))
                .addGroup(50, toItemStack("uranium", 1))
                .addGroup(1, toItemStack("polonium", 1))
                .build());

        // End
        List.of(Items.CHORUS_FLOWER, Items.CHORUS_FRUIT, Items.CHORUS_PLANT, Items.POPPED_CHORUS_FRUIT).forEach(item ->
                dissolver(item, createSet()
                        .addGroup(100, toItemStack("cellulose"))
                        .addGroup(50, toItemStack("lutetium"))
                        .build()));

        List.of(Items.PURPUR_BLOCK, Items.PURPUR_PILLAR).forEach(item ->
                dissolver(item, createSet()
                        .addGroup(100, toItemStack("silicon_dioxide", 4))
                        .addGroup(50, toItemStack("lutetium"))
                        .build()));

        dissolver(Items.PURPUR_SLAB, createSet()
                .addGroup(100, toItemStack("silicon_dioxide", 2))
                .addGroup(25, toItemStack("lutetium"))
                .build());

        for (Item item : List.of(Items.END_STONE, Items.END_STONE_BRICKS)) {
            dissolver(item, createSet().weighted()
                    .addGroup(50, toItemStack("mercury"))
                    .addGroup(5, toItemStack("neodymium"))
                    .addGroup(250, toItemStack("silicon_dioxide"))
                    .addGroup(50, toItemStack("lithium"))
                    .addGroup(2, toItemStack("thorium"))
                    .build());
        }

        dissolver(Items.END_STONE_BRICK_SLAB, createSet().weighted()
                .addGroup(350)
                .addGroup(50, toItemStack("mercury"))
                .addGroup(5, toItemStack("neodymium"))
                .addGroup(250, toItemStack("silicon_dioxide"))
                .addGroup(50, toItemStack("lithium"))
                .addGroup(2, toItemStack("thorium"))
                .build());

        dissolver(Items.ENDER_PEARL, createSet()
                .addGroup(100,
                        toItemStack("silicon", 16),
                        toItemStack("mercury", 16),
                        toItemStack("neodymium", 16))
                .build(), true);

        // Dyes
        dissolver("forge:dyes/white", createSet().addGroup(100, toItemStack("hydroxylapatite")).build());
        dissolver("forge:dyes/orange", createSet().addGroup(100, toItemStack("potassium_dichromate", 4)).build());
        dissolver("forge:dyes/magenta", createSet().addGroup(100, toItemStack("han_purple", 4)).build());
        dissolver("forge:dyes/light_blue", createSet().addGroup(100, toItemStack("cobalt_aluminate", 2), toItemStack("antimony_trioxide", 2)).build());
        dissolver("forge:dyes/yellow", createSet().addGroup(100, toItemStack("lead_iodide", 4)).build());
        dissolver("forge:dyes/lime", createSet().addGroup(100, toItemStack("cadmium_sulfide", 2), toItemStack("chromium_oxide", 2)).build());
        dissolver("forge:dyes/pink", createSet().addGroup(100, toItemStack("arsenic_sulfide", 4)).build());
        dissolver("forge:dyes/gray", createSet().addGroup(100, toItemStack("barium_sulfate", 4)).build());
        dissolver("forge:dyes/light_gray", createSet().addGroup(100, toItemStack("magnesium_sulfate", 4)).build());
        dissolver("forge:dyes/cyan", createSet().addGroup(100, toItemStack("copper_chloride", 4)).build());
        dissolver("forge:dyes/purple", createSet().addGroup(100, toItemStack("potassium_permanganate", 4)).build());
        dissolver("forge:dyes/blue", createSet().addGroup(100, toItemStack("cobalt_aluminate", 4)).build());
        dissolver("forge:dyes/brown", createSet().addGroup(100, toItemStack("cellulose", 4)).build());
        dissolver("forge:dyes/green", createSet().addGroup(100, toItemStack("nickel_chloride", 4)).build());
        dissolver("forge:dyes/red", createSet().addGroup(100, toItemStack("mercury_sulfide", 4)).build());
        dissolver("forge:dyes/black", createSet().addGroup(100, toItemStack("titanium_oxide", 4)).build());

        dissolver("minecraft:wool", createSet().addGroup(100, toItemStack("keratin", 2), toItemStack("triglyceride")).build());
        dissolver("minecraft:carpets", createSet().addGroup((2.0 / 3.0) * 100, toItemStack("keratin", 2), toItemStack("triglyceride")).build());

        dissolver("forge:glass", createSet().addGroup(100, toItemStack("silicon_dioxide", 4)).build());


        // mobs
        dissolver(Items.SLIME_BALL, createSet()
                .addGroup(100,
                        toItemStack("protein", 2),
                        toItemStack("sucrose", 2))
                .build(), true);
        dissolver(Items.SLIME_BLOCK, createSet().addGroup(100, toItemStack("protein", 9*2), toItemStack("sucrose", 9*2)).build(), true);
        dissolver(Items.COBWEB, createSet().addGroup(100, toItemStack("protein", 2)).build(), true);

        for (Item item : List.of(Items.TALL_GRASS, Items.SEAGRASS, Items.GRASS, Items.DEAD_BUSH)) {
            dissolver(item, createSet()
                    .addGroup(25, toItemStack("cellulose"))
                    .build(), true);
        }

        dissolver(Items.FLINT, createSet().addGroup(100, toItemStack("silicon_dioxide", 3)).build());

        dissolver(Items.COCOA_BEANS, createSet()
                .addGroup(100, toItemStack("caffeine"))
                .addGroup(50, toItemStack("cellulose"))
                .build(), true);


        dissolver(Items.APPLE, createSet().addGroup(100,
                        toItemStack("sucrose"),
                        toItemStack("cellulose"))
                .build(), true);

        dissolver("forge:storage_blocks/apple", createSet().addGroup(100,
                        toItemStack("sucrose", 9),
                        toItemStack("cellulose", 9))
                .build(), tagNotEmptyCondition("forge:storage_blocks/apple"));

        dissolver("forge:seeds", createSet().addGroup(100, toItemStack("cellulose")).build());

        dissolver(Items.SPIDER_EYE, createSet().addGroup(100,
                        toItemStack("beta_carotene", 2),
                        toItemStack("chitin", 2))
                .build(), true);

        dissolver(Items.IRON_HORSE_ARMOR, createSet().addGroup(100, toItemStack("iron", 64)).build());
        dissolver(Items.GOLDEN_HORSE_ARMOR, createSet().addGroup(100, toItemStack("gold", 64)).build());
        dissolver(Items.DIAMOND_HORSE_ARMOR, createSet().addGroup(100, toItemStack("graphite", 64 * 8)).build());
        dissolver(Items.ANVIL, createSet().addGroup(100, toItemStack("iron", (144 * 3) + (16 * 4))).build());
        dissolver(Items.IRON_DOOR, createSet().addGroup(100, toItemStack("iron", 32)).build());
        dissolver(Items.IRON_TRAPDOOR, createSet().addGroup(100, toItemStack("iron", 64)).build());

        for (Item item : List.of(Items.SNOW_BLOCK, Items.ICE)) {
            dissolver(item, createSet().addGroup(100, toItemStack("water", 16)).build());
        }

        dissolver(Items.PACKED_ICE, createSet().addGroup(100, toItemStack("water", 16 * 9)).build());
        dissolver(Items.BLUE_ICE, createSet().addGroup(100, toItemStack("water", 16 * 9 * 4)).build());
        dissolver(Items.SNOWBALL, createSet().addGroup(100, toItemStack("water", 4)).build());
        dissolver(Items.SNOW, createSet().addGroup(100, toItemStack("water", 4)).build());

        dissolver("minecraft:music_discs", createSet().addGroup(100,
                        toItemStack("polyvinyl_chloride", 64),
                        toItemStack("lead", 16),
                        toItemStack("cadmium", 16))
                .build());

        dissolver(Items.JUKEBOX, createSet().addGroup(100,
                        toItemStack("graphite", 64 * 2),
                        toItemStack("cellulose", 2))
                .build());

        dissolver(Items.VINE, createSet().addGroup(25, toItemStack("cellulose", 1)).build());

        dissolver(Items.PAPER, createSet().addGroup(100, toItemStack("cellulose")).build());

        dissolver(Items.LILY_PAD, createSet().addGroup(25, toItemStack("cellulose", 1)).build());

        for (Item item : List.of(Items.PUMPKIN, Items.CARVED_PUMPKIN)) {
            dissolver(item, createSet().addGroup(50, toItemStack("cucurbitacin", 1)).build());
        }

        dissolver(Items.BROWN_MUSHROOM, createSet().addGroup(100,
                        toItemStack("phosphoric_acid"),
                        toItemStack("chitin"))
                .build(), true);

        dissolver(Items.RED_MUSHROOM, createSet().addGroup(100,
                        toItemStack("phosphoric_acid"),
                        toItemStack("chitin"))
                .build(), true);

        dissolver(Items.CRIMSON_FUNGUS, createSet().addGroup(100,
                        toItemStack("chitin"),
                        toItemStack("phosphoric_acid"),
                        toItemStack("selenium"))
                .build(), true);


        dissolver(Items.WARPED_FUNGUS, createSet().addGroup(100,
                        toItemStack("chitin"),
                        toItemStack("phosphoric_acid"),
                        toItemStack("mercury"))
                .build(), true);

        dissolver(Items.CRIMSON_ROOTS, createSet().addGroup(100,
                        toItemStack("cellulose"),
                        toItemStack("mercury_sulfide"))
                .build(), true);

        dissolver(Items.WARPED_ROOTS, createSet().addGroup(100,
                        toItemStack("cellulose"),
                        toItemStack("copper_chloride"))
                .build(), true);

        dissolver(Items.NETHER_SPROUTS, createSet().addGroup(100,
                        toItemStack("cellulose"),
                        toItemStack("copper_chloride"))
                .build(), true);

        dissolver(Items.TWISTING_VINES, createSet().addGroup(100,
                        toItemStack("cellulose"),
                        toItemStack("copper_chloride"))
                .build(), true);

        dissolver(Items.WEEPING_VINES, createSet().addGroup(100,
                        toItemStack("cellulose"),
                        toItemStack("mercury_sulfide"))
                .build(), true);

        for (Item item : List.of(Items.SOUL_SAND, Items.SOUL_SOIL)) {
            dissolver(item, createSet().addGroup(100,
                            toItemStack("thulium"),
                            toItemStack("silicon_dioxide", 4))
                    .build(), true);
        }

        dissolver(Items.SUGAR_CANE, createSet().addGroup(100, toItemStack("sucrose")).build());

        dissolver("forge:storage_blocks/sugar_cane", createSet().addGroup(100, toItemStack("sucrose", 9)).build(), tagNotEmptyCondition("forge:storage_blocks/sugar_cane"));

        dissolver(Items.SUGAR, createSet().addGroup(100, toItemStack("sucrose")).build());

        dissolver(Items.GUNPOWDER, createSet().addGroup(100,
                        toItemStack("potassium_nitrate", 2),
                        toItemStack("sulfur", 8),
                        toItemStack("graphite", 2))
                .build(), true);

        dissolver("forge:storage_blocks/gunpowder", createSet().addGroup(100,
                        toItemStack("potassium_nitrate", 18),
                        toItemStack("sulfur", 72),
                        toItemStack("graphite", 18))
                .build(), tagNotEmptyCondition("forge:storage_blocks/gunpowder"));


        dissolver(Items.BLAZE_POWDER, createSet().addGroup(100,
                        toItemStack("germanium", 8),
                        toItemStack("graphite", 2),
                        toItemStack("sulfur", 8))
                .build(), true);

        dissolver(Items.NETHER_WART, createSet().addGroup(100,
                        toItemStack("chitin"),
                        toItemStack("germanium", 4),
                        toItemStack("selenium", 4))
                .build(), true);

        dissolver(Items.NETHER_WART_BLOCK, createSet().addGroup(100,
                        toItemStack("chitin", 9),
                        toItemStack("germanium", 4 * 9),
                        toItemStack("selenium", 4 * 9))
                .build());

        dissolver(Items.WARPED_WART_BLOCK, createSet().addGroup(100,
                        toItemStack("chitin", 9),
                        toItemStack("neodymium", 4),
                        toItemStack("mercury", 4))
                .build());

        dissolver(Items.GLOWSTONE_DUST, createSet().addGroup(100, toItemStack("phosphorus", 4)).build());

        dissolver(Items.GLOWSTONE, createSet().addGroup(100, toItemStack("phosphorus", 16)).build());

        dissolver(Items.IRON_BARS, createSet().addGroup(100, toItemStack("iron", 6)).build());

        dissolver(Items.STRING, createSet().addGroup(50, toItemStack("keratin")).build());

        for (Item item : List.of(Items.WHEAT, Items.HAY_BLOCK)) {
            int rolls = 1;
            if (item == Items.HAY_BLOCK) rolls = 9;
            dissolver(item, createSet()
                    .rolls(rolls)
                    .addGroup(5, toItemStack("starch"))
                    .addGroup(25, toItemStack("cellulose"))
                    .build());
        }

        dissolver(Items.MELON, createSet().rolls(9)
                .addGroup(15, toItemStack("cucurbitacin"))
                .addGroup(30, toItemStack("water", 4), toItemStack("sucrose", 2))
                .build());
        dissolver(Items.MELON_SLICE, createSet()
                .addGroup(15, toItemStack("cucurbitacin"))
                .addGroup(30, toItemStack("water", 4), toItemStack("sucrose", 2))
                .build());

        dissolver(Items.CACTUS, createSet()
                .addGroup(100,
                        toItemStack("cellulose"),
                        toItemStack("sucrose"),
                        toItemStack("nitrate"))
                .build(), true);

        // Terracotta
        for (Item item : List.of(Items.TERRACOTTA, Items.BLACK_GLAZED_TERRACOTTA, Items.BLACK_TERRACOTTA,
                Items.BLUE_GLAZED_TERRACOTTA, Items.BLUE_TERRACOTTA, Items.BROWN_GLAZED_TERRACOTTA, Items.BROWN_TERRACOTTA,
                Items.CYAN_GLAZED_TERRACOTTA, Items.CYAN_TERRACOTTA, Items.GRAY_GLAZED_TERRACOTTA, Items.GRAY_TERRACOTTA,
                Items.GREEN_GLAZED_TERRACOTTA, Items.GREEN_TERRACOTTA, Items.LIGHT_BLUE_GLAZED_TERRACOTTA, Items.LIGHT_BLUE_TERRACOTTA,
                Items.LIGHT_GRAY_GLAZED_TERRACOTTA, Items.LIGHT_GRAY_TERRACOTTA, Items.LIME_GLAZED_TERRACOTTA, Items.LIME_TERRACOTTA,
                Items.MAGENTA_GLAZED_TERRACOTTA, Items.MAGENTA_TERRACOTTA, Items.ORANGE_GLAZED_TERRACOTTA, Items.ORANGE_TERRACOTTA,
                Items.PINK_TERRACOTTA, Items.PINK_GLAZED_TERRACOTTA, Items.YELLOW_TERRACOTTA, Items.YELLOW_GLAZED_TERRACOTTA,
                Items.PURPLE_TERRACOTTA, Items.PURPLE_GLAZED_TERRACOTTA, Items.RED_TERRACOTTA, Items.RED_GLAZED_TERRACOTTA)) {
            dissolver(item, createSet().addGroup(100, toItemStack("kaolinite", 4)).build());
        }

        // Concrete
        for (Item item : List.of(Items.BLACK_CONCRETE_POWDER, Items.BLUE_CONCRETE_POWDER, Items.CYAN_CONCRETE_POWDER,
                Items.BROWN_CONCRETE_POWDER, Items.GRAY_CONCRETE_POWDER, Items.GREEN_CONCRETE_POWDER, Items.LIGHT_BLUE_CONCRETE_POWDER,
                Items.LIGHT_GRAY_CONCRETE_POWDER, Items.LIME_CONCRETE_POWDER, Items.MAGENTA_CONCRETE_POWDER,
                Items.ORANGE_CONCRETE_POWDER, Items.PINK_CONCRETE_POWDER, Items.PURPLE_CONCRETE_POWDER,
                Items.RED_CONCRETE_POWDER, Items.WHITE_CONCRETE_POWDER, Items.YELLOW_CONCRETE_POWDER)) {
            dissolver(item, createSet().addGroup(100, toItemStack("silicon_dioxide", 2)).build());
        }

        for (Item item : List.of(Items.BLACK_CONCRETE, Items.BLUE_CONCRETE, Items.CYAN_CONCRETE,
                Items.BROWN_CONCRETE, Items.GRAY_CONCRETE, Items.GREEN_CONCRETE, Items.LIGHT_BLUE_CONCRETE,
                Items.LIGHT_GRAY_CONCRETE, Items.LIME_CONCRETE, Items.MAGENTA_CONCRETE,
                Items.ORANGE_CONCRETE, Items.PINK_CONCRETE, Items.PURPLE_CONCRETE,
                Items.RED_CONCRETE, Items.WHITE_CONCRETE, Items.YELLOW_CONCRETE)) {
            dissolver(item, createSet().addGroup(100, toItemStack("silicon_dioxide", 2), toItemStack("water", 4)).build());
        }

        for (Item item : List.of(Items.DEAD_BRAIN_CORAL, Items.DEAD_BRAIN_CORAL_BLOCK, Items.DEAD_BRAIN_CORAL_FAN,
                Items.DEAD_BUBBLE_CORAL, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_FAN,
                Items.DEAD_FIRE_CORAL, Items.DEAD_FIRE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_FAN,
                Items.DEAD_HORN_CORAL, Items.DEAD_HORN_CORAL_BLOCK, Items.DEAD_HORN_CORAL_FAN,
                Items.DEAD_TUBE_CORAL, Items.DEAD_TUBE_CORAL_BLOCK, Items.DEAD_TUBE_CORAL_FAN)) {
            int quantity = 4;
            if (item == Items.DEAD_BRAIN_CORAL_BLOCK || item == Items.DEAD_BUBBLE_CORAL_BLOCK || item == Items.DEAD_FIRE_CORAL_BLOCK
                    || item == Items.DEAD_HORN_CORAL_BLOCK || item == Items.DEAD_TUBE_CORAL_BLOCK) {
                quantity = 16;
            }
            dissolver(item, createSet().addGroup(100, toItemStack("calcium_carbonate", quantity)).build());
        }

        for (Item item : List.of(Items.BRAIN_CORAL, Items.BRAIN_CORAL_BLOCK, Items.BRAIN_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.BRAIN_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100,
                            toItemStack("calcium_carbonate", multiplier),
                            toItemStack("arsenic_sulfide", (multiplier / 2)))
                    .build());
        }
        for (Item item : List.of(Items.BUBBLE_CORAL, Items.BUBBLE_CORAL_BLOCK, Items.BUBBLE_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.BUBBLE_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100,
                            toItemStack("calcium_carbonate", multiplier),
                            toItemStack("han_purple", (multiplier / 2)))
                    .build());
        }

        for (Item item : List.of(Items.FIRE_CORAL, Items.FIRE_CORAL_BLOCK, Items.FIRE_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.FIRE_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100, toItemStack("calcium_carbonate", multiplier),
                    toItemStack("cobalt_nitrate", (multiplier / 2))).build());
        }
        for (Item item : List.of(Items.HORN_CORAL, Items.HORN_CORAL_BLOCK, Items.HORN_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.HORN_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100, toItemStack("calcium_carbonate", multiplier),
                            toItemStack("lead_iodide", (multiplier / 2)))
                    .build());
        }
        for (Item item : List.of(Items.TUBE_CORAL, Items.TUBE_CORAL_BLOCK, Items.TUBE_CORAL_FAN)) {
            int multiplier = 4;
            if (item == Items.TUBE_CORAL_BLOCK) multiplier = 16;
            dissolver(item, createSet().addGroup(100, toItemStack("calcium_carbonate", multiplier),
                            toItemStack("cobalt_aluminate", (multiplier / 2)))
                    .build());
        }

        dissolver(Items.KELP, createSet().weighted()
                .addGroup(100, toItemStack("cellulose"))
                .addGroup(17, toItemStack("sodium_carbonate", 2))
                .addGroup(5, toItemStack("potassium_iodide", 2))
                .build());

        dissolver(Items.DRIED_KELP, createSet()
                .addGroup(100, toItemStack("sodium_carbonate"),
                        toItemStack("potassium_iodide"))
                .build());

        dissolver(Items.DRIED_KELP_BLOCK, createSet()
                .addGroup(100, toItemStack("sodium_carbonate", 9),
                        toItemStack("potassium_iodide", 9))
                .build());

        dissolver(Items.SEA_PICKLE, createSet()
                .addGroup(100, toItemStack("cellulose"),
                        toItemStack("cadmium_sulfide", 2),
                        toItemStack("chromium_oxide", 2))
                .build());

        for (Item item : List.of(Items.POTATO, Items.BAKED_POTATO)) {
            dissolver(item, createSet().weighted()
                    .addGroup(10, toItemStack("starch"))
                    .addGroup(25, toItemStack("potassium", 5))
                    .build());
        }

        dissolver("forge:storage_blocks/potato", createSet().weighted().rolls(9)
                .addGroup(10, toItemStack("starch"))
                .addGroup(25, toItemStack("potassium", 5))
                .build(), tagNotEmptyCondition("forge:storage_blocks/potato"));

        for (Item item : List.of(Items.COOKED_PORKCHOP, Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.COOKED_CHICKEN, Items.COOKED_RABBIT)) {
            dissolver(item, createSet().addGroup(100, toItemStack("protein", 4)).build());
        }

        for (Item item : List.of(Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT)) {
            dissolver(item, createSet().addGroup(100, toItemStack("protein", 4)).build());
        }

        for (Item item : List.of(Items.COD, Items.TROPICAL_FISH)) {
            dissolver(item, createSet().addGroup(100,
                            toItemStack("protein", 4),
                            toItemStack("selenium", 2))
                    .build(), true);
        }
        dissolver(Items.COOKED_COD, createSet().addGroup(100,
                        toItemStack("protein", 4),
                        toItemStack("selenium", 2))
                .build());
        dissolver(Items.SALMON, createSet().addGroup(100,
                        toItemStack("protein", 4),
                        toItemStack("selenium", 4))
                .build(), true);
        dissolver(Items.COOKED_SALMON, createSet().addGroup(100,
                        toItemStack("protein", 4),
                        toItemStack("selenium", 4))
                .build());

        dissolver(Items.PUFFERFISH, createSet().addGroup(100,
                        toItemStack("protein", 4),
                        toItemStack("potassium_cyanide", 4))
                .build());

        dissolver(Items.SPONGE, createSet().addGroup(100,
                        toItemStack("kaolinite", 8),
                        toItemStack("calcium_carbonate", 8))
                .build(), true);

        dissolver(Items.WET_SPONGE, createSet().addGroup(100,
                        toItemStack("kaolinite", 8),
                        toItemStack("calcium_carbonate", 8),
                        toItemStack("water", 16))
                .build(), true);

        dissolver(Items.LEATHER, createSet().addGroup(100, toItemStack("protein", 3)).build());
        dissolver(Items.ROTTEN_FLESH, createSet().addGroup(100, toItemStack("protein", 3)).build());
        dissolver(Items.FEATHER, createSet().addGroup(100, toItemStack("keratin", 2)).build());
        dissolver(Items.BONE_MEAL, createSet().addGroup(50, toItemStack("hydroxylapatite")).build());
        dissolver(Items.BONE_BLOCK, createSet().rolls(9).addGroup(50, toItemStack("hydroxylapatite")).build());

        dissolver(Items.EGG, createSet().addGroup(100,
                        toItemStack("calcium_carbonate", 8),
                        toItemStack("protein", 2))
                .build(), true);

        dissolver(Items.CARROT, createSet().addGroup(20, toItemStack("beta_carotene")).build());

        dissolver("forge:storage_blocks/carrot", createSet().rolls(9).addGroup(20, toItemStack("beta_carotene")).build(), tagNotEmptyCondition("forge:storage_blocks/carrot"));

        dissolver(Items.WITHER_SKELETON_SKULL, createSet().addGroup(100,
                        toItemStack("hydroxylapatite", 8),
                        toItemStack("mendelevium", 32))
                .build(), true);

        dissolver(Items.SKELETON_SKULL, createSet().addGroup(100, toItemStack("hydroxylapatite", 8)).build(), true);
        dissolver(Items.BELL, createSet().addGroup(100, toItemStack("gold", 64)).build());

        dissolver(Items.SHROOMLIGHT, createSet().addGroup(100, toItemStack("phosphorus", 16),
                toItemStack("chitin"), toItemStack("phosphoric_acid")).build());

        dissolver(Items.HONEYCOMB, createSet().addGroup(100, toItemStack("sucrose", 3), toItemStack("triglyceride", 1)).build(), true);
        dissolver(Items.HONEY_BOTTLE, createSet().addGroup(100, toItemStack("sucrose", 3)).build());
        dissolver(Items.HONEY_BLOCK, createSet().addGroup(100, toItemStack("sucrose", 12)).build());
        dissolver(Items.HONEYCOMB_BLOCK, createSet().addGroup(100, toItemStack("sucrose", 12), toItemStack("triglyceride", 1)).build());

        dissolver(Items.TURTLE_EGG, createSet().addGroup(100, toItemStack("protein", 4), toItemStack("calcium_carbonate", 8)).build());

        dissolver(Items.SCUTE, createSet().addGroup(100, toItemStack("protein", 2)).build());

        dissolver(Items.GOLDEN_APPLE, createSet().addGroup(100, toItemStack("gold", 8 * 16),
                        toItemStack("cellulose"),
                        toItemStack("sucrose"))
                .build());

        dissolver(Items.BEETROOT, createSet()
                .addGroup(100, toItemStack("sucrose"))
                .addGroup(50, toItemStack("iron_oxide"))
                .build());

        dissolver("forge:storage_blocks/beetroot", createSet().rolls(9)
                .addGroup(100, toItemStack("sucrose"))
                .addGroup(50, toItemStack("iron_oxide"))
                .build(), tagNotEmptyCondition("forge:storage_blocks/beetroot"));

        dissolver(Items.BONE, createSet()
                .addGroup(50, toItemStack("hydroxylapatite", 3))
                .build(), true);

        dissolver(Items.OBSIDIAN, createSet().addGroup(100,
                        toItemStack("magnesium_oxide", 8),
                        toItemStack("potassium_chloride", 8),
                        toItemStack("aluminum_oxide", 8),
                        toItemStack("silicon_dioxide", 24))
                .build(), true);

        dissolver(Items.CRYING_OBSIDIAN, createSet().addGroup(100,
                        toItemStack("magnesium_oxide", 8),
                        toItemStack("potassium_chloride", 8),
                        toItemStack("aluminum_oxide", 8),
                        toItemStack("silicon_dioxide", 24),
                        toItemStack("phosphorus", 8),
                        toItemStack("iridium", 8))
                .build());

        dissolver(Items.TUFF, createSet().weighted().rolls(2)
                .addGroup(5)
                .addGroup(10, toItemStack("silicon_dioxide"))
                .addGroup(2, toItemStack("magnesium_oxide"))
                .addGroup(2, toItemStack("potassium_chloride"))
                .addGroup(2, toItemStack("aluminum_oxide"))
                .build(), true);

        dissolver(Items.BAMBOO, createSet().addGroup(100, toItemStack("cellulose")).build());

        dissolver("forge:storage_blocks/bamboo", createSet().addGroup(100, toItemStack("cellulose", 9)).build(), tagNotEmptyCondition("forge:storage_blocks/bamboo"));



        dissolver(Items.RABBIT_FOOT, createSet()
                .addGroup(100, toItemStack("protein", 2))
                .addGroup(50, toItemStack("keratin", 1))
                .build(), true);

        dissolver(Items.RABBIT_HIDE, createSet().addGroup(100, toItemStack("protein", 2)).build());

        dissolver(Items.SHULKER_SHELL, createSet().addGroup(100, toItemStack("calcium_carbonate", 8), toItemStack("lutetium", 8)).build(), true);

        dissolver(Items.DRAGON_BREATH, createSet().addGroup(100,
                        toItemStack("xenon", 8),
                        toItemStack("radon", 8),
                        toItemStack("oganesson", 8))
                .build());

        dissolver(Items.GHAST_TEAR, createSet().addGroup(100, toItemStack("polonium", 16)).build(), true);
        dissolver(Items.NAUTILUS_SHELL, createSet().addGroup(100, toItemStack("calcium_carbonate", 16)).build());
        dissolver(Items.PHANTOM_MEMBRANE, createSet().addGroup(100, toItemStack("cerium", 8)).build(), true);
        dissolver(Items.SWEET_BERRIES, createSet().addGroup(100, toItemStack("cellulose"), toItemStack("sucrose")).build(), true);
        dissolver(Items.GLOW_BERRIES, createSet().addGroup(100, toItemStack("cellulose"), toItemStack("sucrose"), toItemStack("phosphorus")).build(), true);
        dissolver(Items.CHAIN, createSet().addGroup(100, toItemStack("iron", 18)).build());
        dissolver(Items.INK_SAC, createSet().addGroup(100, toItemStack("titanium_oxide", 4)).build());
        dissolver(Items.GLOW_INK_SAC, createSet().addGroup(100, toItemStack("copper_chloride", 4), toItemStack("phosphorus", 2)).build(), true);
    }
}
