package al132.alchemistry.misc;

import static com.google.common.collect.Lists.newArrayList;

public class ModRecipes {

    //public static List<DissolverRecipe> dissolverRecipes = new ArrayList<>();
    //public static List<CombinerRecipe> combinerRecipes = new ArrayList<>();
    //public static List<EvaporatorRecipe> evaporatorRecipes = new ArrayList<>();
    //public static List<AtomizerRecipe> atomizerRecipes = new ArrayList<>();
    //public static List<LiquifierRecipe> liquifierRecipes = new ArrayList<>();
    //public static List<FissionRecipe> fissionRecipes = new ArrayList<>();

    //public static List<DissolverTagData> metalTagData = new ArrayList<>();
    //public static List<String> metals = new ArrayList<>();
    //public static final String heathenSpelling = "aluminium";

   /* public static void filterEmptyDissolverRecipes() {
        Ingredient barrier = Ingredient.fromItems(Items.BARRIER);
        dissolverRecipes.removeIf(current -> current.getInput().getMatchingStacks()[0].getItem() == Items.BARRIER);
    }

    public static void init() {
        //metals.add(heathenSpelling);
        Set<Integer> nonMetals = Sets.newHashSet(1, 2, 6, 7, 8, 9, 10, 15, 16, 17, 18, 35, 36, 53, 54, 80, 86);
        metals.addAll(ElementRegistry.elements.values().stream()
                .filter(it -> !nonMetals.contains(it.atomicNumber))
                .map(it -> it.internalName).collect(Collectors.toList()));

        metalTagData.add(new DissolverTagData("ingots", 16, metals));
        metalTagData.add(new DissolverTagData("ores", 32, metals));
        metalTagData.add(new DissolverTagData("dusts", 16, metals));
        metalTagData.add(new DissolverTagData("storage_blocks", 144, metals));
        metalTagData.add(new DissolverTagData("nuggets", 1, metals));
        metalTagData.add(new DissolverTagData("plates", 16, metals));


    }

    public static void initEvaporatorRecipes() {
        //evaporatorRecipes.add(new EvaporatorRecipe(new FluidStack(Fluids.WATER, 125), new ItemStack(Ref.mineralSalt)));
        //evaporatorRecipes.add(new EvaporatorRecipe(new FluidStack(Fluids.LAVA, 1000), new ItemStack(Blocks.MAGMA_BLOCK)));
        //TODO milk
    }
/*
    public static void initDissolverRecipes() {

        dissolver("minecraft:logs")
                .outputs(set()
                        .addGroup(1.0, toStack("cellulose"))
                        .build())
                .build();

        dissolver("minecraft:planks")
                .outputs(set().relative(false).addGroup(25.0, toStack("cellulose")).build())
                .build();

        dissolver("minecraft:leaves")
                .outputs(set().relative(false).addGroup(5, toStack("cellulose")).build())
                .build();

        dissolver("forge:cobblestone")
                .outputs(set()
                        .addGroup(700, ItemStack.EMPTY)
                        .addGroup(2, toStack("aluminum"))
                        .addGroup(4, toStack("iron"))
                        .addGroup(1.5, toStack("gold"))
                        .addGroup(10, toStack("silicon_dioxide"))
                        .addGroup(1, toStack("dysprosium"))
                        .addGroup(1.5, toStack("zirconium"))
                        .addGroup(1, toStack("nickel"))
                        .addGroup(1, toStack("gallium"))
                        .addGroup(1, toStack("tungsten"))
                        .build())
                .build();

        newArrayList(Items.GRANITE, Items.POLISHED_GRANITE).forEach(item -> {
            dissolver(item)
                    .outputs(set()
                            .addGroup(5, toStack("aluminum_oxide"))
                            .addGroup(2, toStack("iron"))
                            .addGroup(2, toStack("potassium_chloride"))
                            .addGroup(10, toStack("silicon_dioxide"))
                            .addGroup(1, toStack("technetium"))
                            .addGroup(1.5, toStack("manganese"))
                            .addGroup(1.5, toStack("radium"))
                            .build())
                    .build();
        });

        newArrayList(Items.DIORITE, Items.POLISHED_DIORITE).forEach(item -> {
            dissolver(item)
                    .outputs(set()
                            .addGroup(4, toStack("aluminum_oxide"))
                            .addGroup(2, toStack("iron"))
                            .addGroup(4, toStack("potassium_chloride"))
                            .addGroup(10, toStack("silicon_dioxide"))
                            .addGroup(1.5, toStack("indium"))
                            .addGroup(2, toStack("manganese"))
                            .addGroup(2, toStack("osmium"))
                            .addGroup(3, toStack("tin"))
                            .build())
                    .build();
        });

        dissolver(Items.MAGMA_BLOCK)
                .outputs(set()
                        .rolls(2)
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
                        .build())
                .build();

        newArrayList(Items.ANDESITE, Items.POLISHED_ANDESITE).forEach(item -> {
            dissolver(item)
                    .outputs(set()
                            .addGroup(4, toStack("aluminum_oxide"))
                            .addGroup(3, toStack("iron"))
                            .addGroup(4, toStack("potassium_chloride"))
                            .addGroup(10, toStack("silicon_dioxide"))
                            .addGroup(2, toStack("platinum"))
                            .addGroup(4, toStack("calcium"))
                            .build())
                    .build();
        });

        dissolver(Items.STONE)
                .outputs(set()
                        .addGroup(20, ItemStack.EMPTY)
                        .addGroup(2, toStack("aluminum"))
                        .addGroup(4, toStack("iron"))
                        .addGroup(1.5, toStack("gold"))
                        .addGroup(20, toStack("silicon_dioxide"))
                        .addGroup(.5, toStack("dysprosium"))
                        .addGroup(1.25, toStack("zirconium"))
                        .addGroup(1, toStack("tungsten"))
                        .addGroup(1, toStack("nickel"))
                        .addGroup(1, toStack("gallium"))
                        .build())
                .build();

        dissolver("forge:chests/wooden")
                .outputs(set().addGroup(1.0, toStack("cellulose", 2)).build())
                .build();

        dissolver(Items.CRAFTING_TABLE)
                .outputs(set()
                        .addGroup(1.0, toStack("cellulose"))
                        .build())
                .build();

        dissolver(Items.COBWEB)
                .outputs(set()
                        .addGroup(1.0, toStack("protein", 2))
                        .build())
                .build();

        dissolver(Items.TALL_GRASS)
                .outputs(set()
                        .relative(false)
                        .addGroup(25, toStack("cellulose"))
                        .build())
                .build();

        dissolver(Items.FLINT)
                .outputs(set()
                        .addGroup(1.0, toStack("silicon_dioxide", 3))
                        .build())
                .build();

        dissolver(Items.COCOA_BEANS)
                .outputs(set()
                        .relative(false)
                        .addGroup(100, toStack("caffeine"))
                        .addGroup(50, toStack("cellulose"))
                        .build())
                .build();

        dissolver(Items.APPLE)
                .outputs(set()
                        .addGroup(1.0,
                                toStack("sucrose"),
                                toStack("cellulose"))
                        .build())
                .build();

        dissolver(Items.COAL_ORE)
                .outputs(set()
                        .addGroup(1.0,
                                toStack("carbon", 32),
                                toStack("sulfur", 8))
                        .build())
                .build();

        dissolver(Items.COAL_BLOCK)
                .outputs(set().addGroup(1.0, toStack("carbon", 9 * 8)).build())
                .build();

        dissolver(Items.COAL)
                .outputs(set().addGroup(1.0, toStack("carbon", 8)).build())
                .build();

        dissolver(Items.CHARCOAL)
                .outputs(set().addGroup(1.0, toStack("carbon", 8)).build())
                .build();

        dissolver("minecraft:wooden_slabs")
                .outputs(set()
                        .relative(false)
                        .addGroup(12.0, toStack("cellulose")).build())
                .build();

        dissolver("forge:slimeballs")
                .outputs(set().addGroup(1,
                        toStack("protein", 2),
                        toStack("sucrose", 2))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.SLIME_BLOCK)
                .outputs(set().addGroup(1,
                        toStack("protein", 2 * 9),
                        toStack("sucrose", 2 * 9))
                        .build())
                .build();

        dissolver(Items.STICK)
                .outputs(set().relative(false).addGroup(10, toStack("cellulose")).build())
                .build();

        dissolver(Items.ENDER_PEARL)
                .outputs(set().addGroup(1,
                        toStack("silicon", 16),
                        toStack("mercury", 16),
                        toStack("neodymium", 16))
                        .build())
                .setReversible(true)
                .build();


        dissolver(Items.WHEAT_SEEDS)
                .outputs(set()
                        .relative(false)
                        .addGroup(10, toStack("cellulose"))
                        .build())
                .build();

        dissolver(Items.NETHERRACK)
                .outputs(set()
                        .addGroup(15, ItemStack.EMPTY)
                        .addGroup(2, toStack("zinc_oxide"))
                        .addGroup(1, toStack("gold"))
                        .addGroup(1, toStack("phosphorus"))
                        .addGroup(3, toStack("sulfur"))
                        .addGroup(1, toStack("germanium"))
                        .addGroup(4, toStack("silicon"))
                        .build())
                .build();

        for (Item item : newArrayList(Items.NETHER_BRICK, Items.NETHER_BRICKS)) {
            int rolls = 1;
            if (item == Items.NETHER_BRICKS) rolls = 4;
            dissolver(item)
                    .outputs(set()
                            .rolls(rolls)
                            .addGroup(5, ItemStack.EMPTY)
                            .addGroup(2, toStack("zinc_oxide"))
                            .addGroup(1, toStack("gold"))
                            .addGroup(1, toStack("phosphorus"))
                            .addGroup(4, toStack("sulfur"))
                            .addGroup(1, toStack("germanium"))
                            .addGroup(4, toStack("silicon"))
                            .build())
                    .build();
        }

        dissolver(Items.SPIDER_EYE)
                .outputs(set()
                        .addGroup(1,
                                toStack("beta_carotene", 2),
                                toStack("protein", 2))
                        .build())
                .build();

        dissolver(Items.IRON_HORSE_ARMOR)
                .outputs(set()
                        .addGroup(1, toStack("iron", 64)).build())
                .build();

        dissolver(Items.GOLDEN_HORSE_ARMOR)
                .outputs(set()
                        .addGroup(1, toStack("gold", 64)).build())
                .build();

        dissolver(Items.DIAMOND_HORSE_ARMOR)
                .outputs(set()
                        .addGroup(1, toStack("carbon", 4 * (64 * 8))).build())
                .build();


        dissolver("minecraft:wool")
                .outputs(set()
                        .addGroup(1, toStack("protein"), toStack("triglyceride")).build())
                .build();

        dissolver("minecraft:carpets")
                .outputs(set()
                        .relative(false)
                        .addGroup((2.0 / 3.0) * 100, toStack("protein"), toStack("triglyceride")).build())
                .build();

        dissolver(Items.ANVIL)
                .outputs(set()
                        .addGroup(1, toStack("iron", (144 * 3) + (16 * 4))).build())
                .build();

        dissolver(Items.IRON_DOOR)
                .outputs(set()
                        .addGroup(1, toStack("iron", 32)).build())
                .build();

        dissolver(Items.IRON_TRAPDOOR)
                .outputs(set()
                        .addGroup(1, toStack("iron", 64)).build())
                .build();
        for (Item item : newArrayList(Items.EMERALD, Items.EMERALD_ORE, Items.EMERALD_BLOCK)) {
            int multiplier = 1;
            boolean reversible = false;
            if (item == Items.EMERALD) reversible = true;
            else if (item == Items.EMERALD_ORE) multiplier = 2;
            else if (item == Items.EMERALD_BLOCK) multiplier = 9;
            dissolver(item)
                    .outputs(set()
                            .addGroup(1,
                                    toStack("beryl", 8 * multiplier),
                                    toStack("chromium", 8 * multiplier),
                                    toStack("vanadium", 4 * multiplier)
                            ).build())
                    .setReversible(reversible)
                    .build();
        }

        for (Item item : newArrayList(Items.DIAMOND, Items.DIAMOND_ORE, Items.DIAMOND_BLOCK)) {
            int multiplier = 1;
            if (item == Items.DIAMOND_ORE) multiplier = 2;
            else if (item == Items.DIAMOND_BLOCK) multiplier = 9;
            dissolver(item).outputs(set()
                    .addGroup(1, toStack("carbon", 64 * 8 * multiplier)).build())
                    .build();
        }

        for (Item item : newArrayList(Items.END_STONE, Items.END_STONE_BRICKS)) {
            dissolver(item)
                    .outputs(set()
                            .addGroup(50, toStack("mercury"))
                            .addGroup(5, toStack("neodymium"))
                            .addGroup(250, toStack("silicon_dioxide"))
                            .addGroup(50, toStack("lithium"))
                            .addGroup(2, toStack("thorium"))
                            .build())
                    .build();
        }

        for (Item item : newArrayList(Items.SNOW_BLOCK, Items.ICE)) {
            dissolver(item)
                    .outputs(set().addGroup(1, toStack("water", 16)).build())
                    .build();
        }

        dissolver("minecraft:music_discs")
                .outputs(set().addGroup(1,
                        toStack("polyvinyl_chloride", 64),
                        toStack("lead", 16),
                        toStack("cadmium", 16))
                        .build())
                .build();

        dissolver(Items.JUKEBOX)
                .outputs(set().addGroup(1,
                        toStack("carbon", 64 * 8),
                        toStack("cellulose", 2))
                        .build())
                .build();

        dissolver(Items.DIRT)
                .outputs(set()
                        .addGroup(30, toStack("water"))
                        .addGroup(50, toStack("silicon_dioxide"))
                        .addGroup(10, toStack("cellulose"))
                        .addGroup(10, toStack("kaolinite"))
                        .build())
                .build();

        dissolver(Items.GRASS_BLOCK)
                .outputs(set()
                        .addGroup(30, toStack("water"))
                        .addGroup(50, toStack("silicon_dioxide"))
                        .addGroup(10, toStack("cellulose"))
                        .addGroup(10, toStack("kaolinite"))
                        .build())
                .build();

        dissolver("forge:glass")
                .outputs(set()
                        .addGroup(1, toStack("silicon_dioxide", 4)).build())
                .build();

        dissolver("minecraft:saplings")
                .outputs(set()
                        .relative(false)
                        .addGroup(25, toStack("cellulose", 1)).build())
                .build();

        dissolver(Items.VINE)
                .outputs(set()
                        .relative(false)
                        .addGroup(25, toStack("cellulose", 1)).build())
                .build();

        dissolver(Items.LILY_PAD)
                .outputs(set()
                        .relative(false)
                        .addGroup(25, toStack("cellulose", 1)).build())
                .build();

        dissolver(Items.PUMPKIN)
                .outputs(set()
                        .relative(false)
                        .addGroup(50, toStack("cucurbitacin", 1)).build())
                .build();

        dissolver(Items.QUARTZ)
                .outputs(set().addGroup(1,
                        toStack("barium", 16),
                        toStack("silicon_dioxide", 32))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.NETHER_QUARTZ_ORE)
                .outputs(set().addGroup(1,
                        toStack("barium", 16 * 2),
                        toStack("silicon_dioxide", 32 * 2))
                        .build())
                .build();

        dissolver("forge:storage_blocks/quartz")
                .outputs(set().addGroup(1,
                        toStack("barium", 16 * 4),
                        toStack("silicon_dioxide", 32 * 4))
                        .build())
                .build();

        dissolver(Items.BROWN_MUSHROOM)
                .outputs(set().addGroup(1,
                        toStack("cellulose"),
                        toStack("psilocybin"))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.RED_MUSHROOM)
                .outputs(set().addGroup(1,
                        toStack("psilocybin"),
                        toStack("cellulose"))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.SOUL_SAND)
                .outputs(set().addGroup(1,
                        toStack("thulium"),
                        toStack("silicon_dioxide", 4))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.SUGAR_CANE)
                .outputs(set().addGroup(1, toStack("sucrose")).build())
                .build();

        dissolver(Items.SAND)
                .outputs(set()
                        .relative(false)
                        .addGroup(100, toStack("silicon_dioxide", 4))
                        .addGroup(1, toStack("gold"))
                        .build())
                .build();

        dissolver(Items.RED_SAND)
                .outputs(set()
                        .relative(false)
                        .addGroup(100, toStack("silicon_dioxide", 4))
                        .addGroup(10, toStack("iron_oxide"))
                        .build())
                .build();

        dissolver(Items.RED_SANDSTONE)
                .outputs(set()
                        .rolls(4)
                        .relative(false)
                        .addGroup(100, toStack("silicon_dioxide", 4))
                        .addGroup(10, toStack("iron_oxide"))
                        .build())
                .build();

        dissolver(Items.SUGAR)
                .outputs(set().addGroup(1, toStack("sucrose")).build())
                .build();

        dissolver(Items.GUNPOWDER)
                .outputs(set().addGroup(1,
                        toStack("potassium_nitrate", 2),
                        toStack("sulfur", 8),
                        toStack("carbon", 8))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.BLAZE_POWDER)
                .outputs(set().addGroup(1,
                        toStack("germanium", 8),
                        toStack("carbon", 8),
                        toStack("sulfur", 8))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.NETHER_WART)
                .outputs(set().addGroup(1,
                        toStack("cellulose"),
                        toStack("germanium", 4),
                        toStack("selenium", 4))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.NETHER_WART_BLOCK)
                .outputs(set().addGroup(1,
                        toStack("cellulose", 9),
                        toStack("germanium", 4 * 9),
                        toStack("selenium", 4 * 9))
                        .build())
                .build();


        dissolver(Items.GLOWSTONE_DUST)
                .outputs(set().addGroup(1, toStack("phosphorus", 4)).build())
                .setReversible(true)
                .build();

        dissolver(Items.GLOWSTONE)
                .outputs(set().addGroup(1, toStack("phosphorus", 16)).build())
                .build();

        dissolver(Items.IRON_BARS)
                .outputs(set().addGroup(1, toStack("iron", 6)).build())
                .build();

        for (Item item : newArrayList(Items.LAPIS_LAZULI, Items.LAPIS_BLOCK, Items.LAPIS_ORE)) {
            int multiplier = 1;
            boolean reversible = false;
            if (item == Items.LAPIS_BLOCK) multiplier = 9;
            else if (item == Items.LAPIS_ORE) multiplier = 4;
            else reversible = true;
            dissolver(item)
                    .outputs(set()
                            .addGroup(1,
                                    toStack("sodium", 6 * multiplier),
                                    toStack("calcium", 2 * multiplier),
                                    toStack("aluminum", 6 * multiplier),
                                    toStack("silicon", 6 * multiplier),
                                    toStack("oxygen", 24 * multiplier),
                                    toStack("sulfur", 2 * multiplier))
                            .build())
                    .setReversible(reversible)
                    .build();
        }

        dissolver(Items.STRING)
                .outputs(set()
                        .relative(false)
                        .addGroup(50, toStack("protein")).build())
                .build();

        dissolver(Ref.condensedMilk)
                .outputs(set()
                        .relative(false)
                        .addGroup(40, toStack("calcium", 4))
                        .addGroup(20, toStack("protein"))
                        .addGroup(20, toStack("sucrose"))
                        .build())
                .build();

        for (Item item : newArrayList(Items.WHEAT, Items.HAY_BLOCK)) {
            int rolls = 1;
            if (item == Items.HAY_BLOCK) rolls = 9;
            dissolver(item)
                    .outputs(set()
                            .relative(false)
                            .rolls(rolls)
                            .addGroup(5, toStack("starch"))
                            .addGroup(25, toStack("cellulose"))
                            .build())
                    .build();
        }

        dissolver(Items.MELON)
                .outputs(set()
                        .relative(false)
                        .addGroup(50, toStack("cucurbitacin"))
                        .addGroup(1, toStack("water", 4), toStack("sucrose", 2))
                        .build())
                .build();

        dissolver(Items.CACTUS)
                .outputs(set()
                        .relative(false)
                        .addGroup(100, toStack("cellulose"))
                        .addGroup(50, toStack("mescaline"))
                        .build())
                .setReversible(true)
                .build();

        //TODO terracotta

        dissolver(Items.GRAVEL)
                .outputs(set().addGroup(1, toStack("silicon_dioxide")).build())
                .build();

        for (Item item : newArrayList(Items.POTATO, Items.BAKED_POTATO)) {
            dissolver(item)
                    .outputs(set()
                            .relative(false)
                            .addGroup(10, toStack("starch"))
                            .addGroup(25, toStack("potassium", 5))
                            .build())
                    .build();
        }

        dissolver(Items.REDSTONE)
                .outputs(set().addGroup(1,
                        toStack("iron_oxide"),
                        toStack("strontium_carbonate"))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.REDSTONE_ORE)
                .outputs(set().addGroup(1,
                        toStack("iron_oxide", 4),
                        toStack("strontium_carbonate", 4))
                        .build())
                .build();

        dissolver(Items.REDSTONE_BLOCK)
                .outputs(set().addGroup(1,
                        toStack("iron_oxide", 9),
                        toStack("strontium_carbonate", 9))
                        .build())
                .build();

        for (Item item : newArrayList(Items.BEEF, Items.COOKED_PORKCHOP, Items.MUTTON, Items.COOKED_PORKCHOP,
                Items.COOKED_BEEF, Items.COOKED_MUTTON, Items.CHICKEN, Items.COOKED_CHICKEN, Items.RABBIT, Items.COOKED_RABBIT)) {
            dissolver(item)
                    .outputs(set().addGroup(1, toStack("protein", 4)).build())
                    .build();
        }

        for (Item item : newArrayList(Items.COD, Items.COOKED_COD, Items.TROPICAL_FISH)) {
            dissolver(item)
                    .outputs(set().addGroup(1,
                            toStack("protein", 4),
                            toStack("selenium", 2))
                            .build())
                    .build();
        }

        for (Item item : newArrayList(Items.SALMON, Items.COOKED_SALMON)) {
            dissolver(item)
                    .outputs(set().addGroup(1,
                            toStack("protein", 4),
                            toStack("selenium", 4))
                            .build())
                    .build();
        }

        dissolver(Items.PUFFERFISH)
                .outputs(set().addGroup(1,
                        toStack("protein", 4),
                        toStack("potassium_cyanide", 4))
                        .build())
                .build();

        dissolver(Items.SPONGE)
                .outputs(set().addGroup(1,
                        toStack("kaolinite", 8),
                        toStack("calcium_carbonate", 8))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.LEATHER)
                .outputs(set().addGroup(1, toStack("protein", 3)).build())
                .build();

        dissolver(Items.ROTTEN_FLESH)
                .outputs(set().addGroup(1, toStack("protein", 3)).build())
                .build();

        dissolver(Items.FEATHER)
                .outputs(set().addGroup(1, toStack("protein", 2)).build())
                .build();

        dissolver(Items.BONE_MEAL)
                .outputs(set().relative(false).addGroup(50, toStack("hydroxylapatite")).build())
                .build();

        dissolver(Items.BONE_BLOCK)
                .outputs(set().rolls(9).relative(false).addGroup(50, toStack("hydroxylapatite")).build())
                .build();

        dissolver(Items.EGG)
                .outputs(set().addGroup(1,
                        toStack("calcium_carbonate", 8),
                        toStack("protein", 2))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Ref.mineralSalt)
                .outputs(set()
                        .addGroup(60, toStack("sodium_chloride"))
                        .addGroup(5, toStack("lithium"))
                        .addGroup(10, toStack("potassium_chloride"))
                        .addGroup(10, toStack("magnesium"))
                        .addGroup(5, toStack("iron"))
                        .addGroup(4, toStack("copper"))
                        .addGroup(2, toStack("zinc"))
                        .build())
                .build();


        dissolver(Items.CARROT)
                .outputs(set()
                        .relative(false)
                        .addGroup(20, toStack("beta_carotene")).build())
                .build();

        dissolver("forge:dyes/red")
                .outputs(set().addGroup(1, toStack("mercury_sulfide", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/pink")
                .outputs(set().addGroup(1, toStack("arsenic_sulfide", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/green")
                .outputs(set().addGroup(1, toStack("nickel_chloride", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/lime")
                .outputs(set().addGroup(1,
                        toStack("cadmium_sulfide", 2),
                        toStack("chromium_oxide", 2))
                        .build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/purple")
                .outputs(set().addGroup(1, toStack("potassium_permanganate", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/yellow")
                .outputs(set().addGroup(1, toStack("lead_iodide", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/orange")
                .outputs(set().addGroup(1, toStack("potassium_dichromate", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/black")
                .outputs(set().addGroup(1, toStack("titanium_oxide", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/gray")
                .outputs(set().addGroup(1, toStack("barium_sulfate", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/magenta")
                .outputs(set().addGroup(1, toStack("han_purple", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/light_blue")
                .outputs(set().addGroup(1,
                        toStack("cobalt_aluminate", 2),
                        toStack("antimony_trioxide", 2))
                        .build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/light_gray")
                .outputs(set().addGroup(1, toStack("magnesium_sulfate", 4)).build())
                .setReversible(true)
                .build();

        dissolver("forge:dyes/cyan")
                .outputs(set().addGroup(1, toStack("copper_chloride", 4)).build())
                .setReversible(true)
                .build();

        dissolver(Items.WITHER_SKELETON_SKULL)
                .outputs(set().addGroup(1,
                        toStack("hydroxylapatite", 8),
                        toStack("mendelevium", 32))
                        .build())
                .build();

        newArrayList(Items.PURPUR_BLOCK, Items.PURPUR_PILLAR).forEach(item -> {
            dissolver(item)
                    .outputs(set()
                            .relative(false)
                            .addGroup(100, toStack("silicon_dioxide", 4))
                            .addGroup(50, toStack("lutetium"))
                            .build())
                    .build();
        });

        dissolver(Items.CLAY_BALL)
                .outputs(set().addGroup(1, toStack("kaolinite", 1)).build())
                .build();

        dissolver(Items.CLAY)
                .outputs(set().addGroup(1, toStack("kaolinite", 4)).build())
                .build();

        dissolver(Items.BEETROOT)
                .outputs(set()
                        .relative(false)
                        .addGroup(100, toStack("sucrose"))
                        .addGroup(50, toStack("iron_oxide"))
                        .build())
                .build();

        dissolver(Items.BONE)
                .outputs(set()
                        .relative(false)
                        .addGroup(50, toStack("hydroxylapatite", 3))
                        .build())
                .setReversible(true)
                .build();

        dissolver(Items.OBSIDIAN)
                .outputs(set()
                        .addGroup(1,
                                toStack("magnesium_oxide", 8),
                                toStack("potassium_chloride", 8),
                                toStack("aluminum_oxide", 8),
                                toStack("silicon_dioxide", 24))
                        .build())
                .setReversible(true)
                .build();

        for (CompoundItem compound : CompoundRegistry.compounds) {
            List<ItemStack> stacks = new ArrayList<>();
            compound.getComponentStacks().forEach(x -> stacks.add(x));
            ProbabilityGroup group = new ProbabilityGroup(stacks, 1.0);
            dissolver(compound)
                    .outputs(set().addGroup(group)//(new ItemStack[0])).build())
                            .build());
        }

        for (DissolverTagData data : metalTagData) {
            for (String element : metals) {
                String resource = "forge:" + data.prefix + "/" + element;
                if (TagUtils.tag(resource) != null && (!(TagUtils.tag(resource).getAllElements().isEmpty()))) {
                    dissolver(resource)
                            .outputs(set().addGroup(1, toStack(element, data.quantity)).build())
                            .build();
                }
            }
        }
    }

    private static ITag<Item> tag(String name) {
        return ItemTags.makeWrapperTag("forge:" + name);
    }


    public static void initCombinerRecipes() {
        combiner(Items.CHARCOAL, newArrayList(null, null, toStack("carbon", 8)));
        combiner(Items.COAL, newArrayList(null, toStack("carbon", 8)));

        combiner(Items.GLOWSTONE, newArrayList(null, toStack("phosphorus", 16)));
        combiner(Items.DIAMOND,
                newArrayList(
                        toStack("carbon", 64), toStack("carbon", 64), toStack("carbon", 64),
                        toStack("carbon", 64), null, toStack("carbon", 64),
                        toStack("carbon", 64), toStack("carbon", 64), toStack("carbon", 64)));

        combiner(Items.SAND, newArrayList(null, null, null, null, null, null, null, null, toStack("silicon_dioxide", 4)));
        combiner(Items.COBBLESTONE, 2, newArrayList(toStack("silicon_dioxide")));
        combiner(Items.STONE, newArrayList(null, toStack("silicon_dioxide")));
        combiner(Items.CLAY, newArrayList(null, toStack("kaolinite", 4)));
        combiner(Items.DIRT, 4, newArrayList(toStack("water"), toStack("cellulose"), toStack("kaolinite")));
        combiner(Items.MYCELIUM, 4, newArrayList(null, null, null, null, null, toStack("psilocybin"),
                toStack("water"), toStack("cellulose"), toStack("kaolinite")));
        combiner(Items.FEATHER, newArrayList(null, null, null, null, null, toStack("protein", 2)));
        combiner(Items.SPIDER_EYE, newArrayList(null, toStack("beta_carotene", 2), toStack("protein", 2)));
        combiner(Items.SPONGE, newArrayList(null, toStack("calcium_carbonate", 8), toStack("kaolinite", 8)));
        combiner(Items.GRASS, 4, newArrayList(null, null, null,
                toStack("water"), toStack("cellulose"), toStack("kaolinite")));
        combiner(Items.GRAVEL, newArrayList(null, null, toStack("silicon_dioxide")));
        combiner(Items.WATER_BUCKET, newArrayList(null, null, null,
                null, toStack("water", 16), null,
                null, Items.BUCKET, null));
        combiner(Items.MILK_BUCKET, newArrayList(null, null, null,
                toStack("protein", 2), toStack("water", 16), toStack("sucrose"),
                null, Items.BUCKET, null));
        combiner(Items.REDSTONE_BLOCK, newArrayList(null, null, null,
                toStack("iron_oxide", 9), toStack("strontium_carbonate", 9)));
        combiner(Items.STRING, 4, newArrayList(null, toStack("protein", 2)));
        combiner(Items.WHITE_WOOL, newArrayList(null, null, null,
                null, null, null,
                toStack("protein"), toStack("triglyceride")));
        combiner(Items.CARROT, newArrayList(null, null, null,
                toStack("cellulose"), toStack("beta_carotene")));
        combiner(Items.SUGAR_CANE, newArrayList(null, null, null,
                toStack("cellulose"), toStack("sucrose")));
        combiner(Items.GRANITE, newArrayList(null, null, null, toStack("silicon_dioxide")));
        combiner(Items.DIORITE, newArrayList(null, null, null, null, toStack("silicon_dioxide")));
        combiner(Items.ANDESITE, newArrayList(null, null, null, null, null, toStack("silicon_dioxide")));
        combiner(Items.FLINT, newArrayList(null, null, null,
                null, null, null,
                null, toStack("silicon_dioxide", 3)));
        combiner(Items.POTATO, newArrayList(toStack("starch"), toStack("potassium", 4)));
        combiner(Items.APPLE, newArrayList(null, toStack("cellulose"), null,
                null, toStack("sucrose")));
        combiner(Items.WHEAT_SEEDS, newArrayList(null, toStack("triglyceride"), null,
                toStack("sucrose")));
        combiner(Items.PUMPKIN_SEEDS, newArrayList(null, toStack("triglyceride"), null,
                null, toStack("sucrose")));
        combiner(Items.MELON_SEEDS, newArrayList(null, toStack("triglyceride"), null,
                null, null, toStack("sucrose")));
        combiner(Items.BEETROOT_SEEDS, newArrayList(null, toStack("triglyceride"), null,
                null, null, null,
                toStack("sucrose"), toStack("iron_oxide")));
        combiner(Items.BEETROOT, newArrayList(null, toStack("sucrose"), toStack("iron_oxide")));

        combiner(Items.OAK_SAPLING, newArrayList(toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.SPRUCE_SAPLING, newArrayList(null, toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.JUNGLE_SAPLING, newArrayList(null, null, toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.ACACIA_SAPLING, newArrayList(null, null, null, toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.DARK_OAK_SAPLING, newArrayList(null, null, null, null, toStack("oxygen"), toStack("cellulose", 2)));
        combiner(Items.BIRCH_SAPLING, newArrayList(null, null, null, null, null, toStack("oxygen"), toStack("cellulose", 2)));

        combiner(Items.OAK_LOG, newArrayList(toStack("cellulose")));
        combiner(Items.SPRUCE_LOG, newArrayList(null, toStack("cellulose")));
        combiner(Items.JUNGLE_LOG, newArrayList(null, null, toStack("cellulose")));
        combiner(Items.ACACIA_LOG, newArrayList(null, null, null, toStack("cellulose")));
        combiner(Items.DARK_OAK_LOG, newArrayList(null, null, null, null, toStack("cellulose")));
        combiner(Items.BIRCH_LOG, newArrayList(null, null, null, null, null, toStack("cellulose")));

        combiner(Items.SNOWBALL, newArrayList(null, null, null,
                null, null, null,
                toStack("water", 4)));

        combiner(Items.SNOW, newArrayList(null, null, null,
                null, null, null,
                null, toStack("water", 16)));

        combiner(Items.ICE, newArrayList(null, null, null,
                null, null, null,
                null, null, toStack("water", 16)));

        combiner(Items.BONE_MEAL, 3, newArrayList(null, null, toStack("hydroxylapatite", 2)));
        combiner(Items.LEATHER, newArrayList(null, null, null,
                null, toStack("protein", 3)));
        combiner(Items.ROTTEN_FLESH, newArrayList(null, null, null,
                null, null, null,
                null, toStack("protein", 3)));
        combiner(Items.NETHER_STAR, newArrayList(toStack("lutetium", 64), toStack("hydrogen", 64), toStack("titanium", 64),
                toStack("hydrogen", 64), toStack("hydrogen", 64), toStack("hydrogen", 64),
                toStack("dysprosium", 64), toStack("hydrogen", 64), toStack("mendelevium", 64)));

        for (DissolverRecipe recipe : dissolverRecipes) {
            if (recipe.reversible) {
                List<Object> outputs = recipe.outputs.toStackList().stream().map(ItemStack::copy).collect(Collectors.toList());
                combinerRecipes.add(new CombinerRecipe(recipe.getInput(), outputs));
            }
        }
        for (CompoundItem compound : CompoundRegistry.compounds) {
            List<Object> inputs = new ArrayList<>();
            for (int i = 0; i < compound.shiftedSlots; i++) {
                inputs.add(ItemStack.EMPTY);
            }
            for (ItemStack component : compound.getComponentStacks()) {
                inputs.add(component.copy());
            }
            combinerRecipes.add(new CombinerRecipe(Ingredient.fromStacks(new ItemStack(compound)), inputs));
        }

        for (String metal : metals) {
            String tag = "forge:ingots/" + metal;
            //if (!tag.getAllElements().isEmpty()) {
            Ingredient ing = TagUtils.tagIngredient(tag);// Ingredient.fromTag(ItemTags.createOptional(new ResourceLocation(tag)));
            combinerRecipes.add(new CombinerRecipe(ing, newArrayList(toStack(metal, 16))));
            //combinerRecipes.add(new CombinerRecipe(new ItemStack(tag.getAllElements().iterator().next()),
            //        newArrayList(toStack(metal, 16))));
            //}
        }
    }


    public static void initAtomizerRecipes() {
        atomizerRecipes.add(new AtomizerRecipe(true, new FluidStack(Fluids.WATER, 500), toStack("water", 8)));
    }

    public static void initLiquifierRecipes() {
        for (AtomizerRecipe recipe : atomizerRecipes) {
            liquifierRecipes.add(new LiquifierRecipe(recipe.output, recipe.input));
        }
    }

    public static void initFissionRecipes() {
        for (int i = 2; i <= 118; i++) {
            int output1 = (i % 2 == 0) ? i / 2 : (i / 2) + 1;
            int output2 = (i % 2 == 0) ? 0 : i / 2;
            if (ElementRegistry.elements.get(output1) != null && (output2 == 0 || ElementRegistry.elements.get(output2) != null)) {
                fissionRecipes.add(new FissionRecipe(i, output1, output2));
            }
        }
    }

    public static DissolverRecipe.BuilderFromTag dissolver(String tag) {
        return new DissolverRecipe.BuilderFromTag().input(tag);
    }

    public static DissolverRecipe.BuilderFromIngredient dissolver(Ingredient input) {
        return new DissolverRecipe.BuilderFromIngredient().input(input);
    }

    public static DissolverRecipe.BuilderFromIngredient dissolver(Item input) {
        return new DissolverRecipe.BuilderFromIngredient().input(input);
    }

    public static DissolverRecipe.BuilderFromIngredient dissolver(ItemStack input) {
        return new DissolverRecipe.BuilderFromIngredient().input(input);
    }


    public static void combiner(Item output, List<Object> inputs) {
        combiner(output, 1, inputs);
    }

    public static void combiner(Item output, int quantity, List<Object> inputs) {
        combinerRecipes.add(new CombinerRecipe(Ingredient.fromStacks(new ItemStack(output, quantity)), inputs));
    }

    public static ProbabilitySet.Builder set() {
        return new ProbabilitySet.Builder();
    }

     */
}
