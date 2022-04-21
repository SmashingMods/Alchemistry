package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.smashingmods.chemlib.chemistry.CompoundRegistry;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import com.smashingmods.chemlib.items.CompoundItem;
import com.smashingmods.chemlib.items.ElementItem;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.smashingmods.alchemistry.utils.StackUtils.toStack;

public class CombinerRecipeProvider {

    private Consumer<FinishedRecipe> consumer;

    public CombinerRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    private void combiner(Item output, List<ItemStack> input) {
        combiner(output, 1, input);
    }

    private void combiner(Item output, int outputCount, List<ItemStack> input) {
        CombinerRecipeBuilder.recipe(new ItemStack(output, outputCount),
                input.stream()
                        .map(x -> {
                            if (x == null) return ItemStack.EMPTY;
                            else return x;
                        }).collect(Collectors.toList())).build(consumer);
    }

    public void register(Consumer<FinishedRecipe> pConsumer) {
        combiner(Items.EMERALD, newArrayList(
                toStack("beryl", 8),
                toStack("chromium", 8),
                toStack("vanadium", 4)));
        combiner(Items.LAPIS_LAZULI, newArrayList(
                toStack("sodium", 6),
                toStack("calcium", 2),
                toStack("aluminum", 6),
                toStack("silicon", 6),
                toStack("oxygen", 24),
                toStack("sulfur", 2)));
        combiner(Items.BAMBOO, newArrayList(
                null, null, null,
                null, null, null,
                null, null, toStack("cellulose")));

        combiner(Items.SLIME_BALL, newArrayList(toStack("protein", 2), toStack("sucrose", 2)));
        combiner(Items.NETHER_WART, newArrayList(
                toStack("cellulose"),
                toStack("germanium", 4),
                toStack("selenium", 4)));
        combiner(Items.RED_DYE, newArrayList(toStack("mercury_sulfide", 4)));
        combiner(Items.PINK_DYE, newArrayList(toStack("arsenic_sulfide", 4)));
        combiner(Items.GREEN_DYE, newArrayList(toStack("nickel_chloride", 4)));
        combiner(Items.LIME_DYE, newArrayList(toStack("cadmium_sulfide", 2), toStack("chromium_oxide", 2)));
        combiner(Items.PURPLE_DYE, newArrayList(toStack("potassium_permanganate", 4)));
        combiner(Items.YELLOW_DYE, newArrayList(toStack("lead_iodide", 4)));
        combiner(Items.ORANGE_DYE, newArrayList(toStack("potassium_dichromate", 4)));
        combiner(Items.BLACK_DYE, newArrayList(toStack("titanium_oxide", 4)));
        combiner(Items.GRAY_DYE, newArrayList(toStack("barium_sulfate", 4)));
        combiner(Items.MAGENTA_DYE, newArrayList(toStack("han_purple", 4)));
        combiner(Items.LIGHT_BLUE_DYE, newArrayList(toStack("cobalt_aluminate", 2), toStack("antimony_trioxide", 2)));
        combiner(Items.LIGHT_GRAY_DYE, newArrayList(toStack("magnesium_sulfate", 4)));
        combiner(Items.CYAN_DYE, newArrayList(toStack("copper_chloride", 4)));

        combiner(Items.CHARCOAL, newArrayList(null, null, toStack("carbon", 8)));
        combiner(Items.COAL, newArrayList(null, toStack("carbon", 8)));

        combiner(Items.GLOWSTONE, newArrayList(null, toStack("phosphorus", 16)));
        combiner(Items.GLOWSTONE_DUST, newArrayList(toStack("phosphorus", 4)));

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
        combiner(Items.GRASS_BLOCK, 4, newArrayList(null, null, null,
                toStack("water"), toStack("cellulose"), toStack("kaolinite")));
        combiner(Items.GRAVEL, newArrayList(null, null, toStack("silicon_dioxide")));
        combiner(Items.WATER_BUCKET, newArrayList(null, null, null,
                null, toStack("water", 16), null,
                null, toStack(Items.BUCKET), null));
        combiner(Items.MILK_BUCKET, newArrayList(null, toStack("calcium", 4), null,
                toStack("protein", 2), toStack("water", 16), toStack("sucrose"),
                null, toStack(Items.BUCKET), null));
        combiner(Items.REDSTONE_BLOCK, newArrayList(null, null, null,
                toStack("iron_oxide", 9), toStack("strontium_carbonate", 9)));
        combiner(Items.REDSTONE, newArrayList(toStack("iron_oxide"), toStack("strontium_carbonate")));
        combiner(Items.STRING, 4, newArrayList(null, toStack("protein", 2)));
        combiner(Items.WHITE_WOOL, newArrayList(null, null, null,
                null, null, null,
                toStack("protein"), toStack("triglyceride")));
        combiner(Items.CARROT, newArrayList(null, null, null,
                toStack("cellulose"), toStack("beta_carotene")));
        combiner(Items.SUGAR_CANE, newArrayList(null, null, null,
                toStack("cellulose"), toStack("sucrose")));
        combiner(Items.SUGAR, newArrayList(toStack("sucrose")));
        combiner(Items.EGG, newArrayList(toStack("calcium_carbonate", 8), toStack("protein", 2)));
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
        combiner(Items.CACTUS, newArrayList(toStack("cellulose"), toStack("mescaline")));

        /*
        for (DissolverRecipe recipe : DissolverRegistry.getRecipes(world)) {
            if (recipe.reversible) {
                List<ItemStack> outputs = recipe.outputs.toStackList().stream().map(ItemStack::copy).collect(Collectors.toList());
                combiner(recipe.getIngredients().get(0).getMatchingStacks()[0].getItem(), outputs);//.add(new CombinerRecipe(recipe.getInput(), outputs));
            }
        }*/

        Map<CompoundItem, List<ItemStack>> overrides = new HashMap<>();
        overrides.put(CompoundRegistry.getByName("triglyceride").get(),
                newArrayList(null, null, toStack("oxygen", 2),
                        null, toStack("hydrogen", 32), null,
                        toStack("carbon", 18)));
        overrides.put(CompoundRegistry.getByName("cucurbitacin").get(),
                newArrayList(null, null, null,
                        null, toStack("hydrogen", 44), null,
                        toStack("carbon", 32), null, toStack("oxygen", 8)));
        overrides.put(CompoundRegistry.getByName("acetic_acid").get(),
                newArrayList(toStack("carbon", 2), null, toStack("hydrogen", 4),
                        null, null, toStack("oxygen", 2)));
        overrides.put(CompoundRegistry.getByName("carbon_monoxide").get(),
                newArrayList(null, toStack("oxygen"), null,
                        null, null, null,
                        toStack("carbon")));
        overrides.put(CompoundRegistry.getByName("carbon_dioxide").get(),
                newArrayList(null, toStack("oxygen", 2), null,
                        null, null, null,
                        null, toStack("carbon")));
        overrides.put(CompoundRegistry.getByName("carbonate").get(),
                newArrayList(null, toStack("oxygen", 3), null,
                        null, null, null,
                        null, null, toStack("carbon")));
        for (CompoundItem compound : CompoundRegistry.compounds) {
            List<ItemStack> inputs = new ArrayList<>();

            if (overrides.containsKey(compound)) {
                inputs = overrides.get(compound).stream().map(x -> {
                    if (x == null) return ItemStack.EMPTY;
                    else return x;
                }).collect(Collectors.toList());
            } else {
                for (int i = 0; i < compound.shiftedSlots; i++) {
                    inputs.add(ItemStack.EMPTY);
                }
                for (ItemStack component : compound.getComponentStacks()) {
                    inputs.add(component.copy());
                }
            }
            combiner(compound, inputs);
            //combinerRecipes.add(new CombinerRecipe(Ingredient.fromStacks(new ItemStack(compound)), inputs));
        }

        for (ElementItem element : ElementRegistry.elements.values()) {
            Item ingot = ForgeRegistries.ITEMS.getValue
                    (new ResourceLocation("chemlib", "ingot_" + element.getChemicalName()));
            if (ingot != Items.AIR) {
                combiner(ingot, newArrayList(toStack(element, 16)));
            }
        }
        combiner(Items.IRON_INGOT, newArrayList(toStack("iron", 16)));
        combiner(Items.GOLD_INGOT, newArrayList(toStack("gold", 16)));
    }
}
