package com.smashingmods.alchemistry.datagen.recipe.combiner;

import com.google.common.collect.Lists;
import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.chemlib.chemistry.CompoundRegistry;
import com.smashingmods.chemlib.chemistry.ElementRegistry;
import com.smashingmods.chemlib.items.CompoundItem;
import com.smashingmods.chemlib.items.ElementItem;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.smashingmods.alchemistry.utils.StackUtils.toStack;


public class CombinerRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public CombinerRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    private void combiner(ItemStack pOutput, List<ItemStack> pInput) {
        CombinerRecipeBuilder.createRecipe(pOutput, pInput).
                group(Alchemistry.MODID + ":combiner")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pOutput)))
                .save(consumer);
    }

    public void register() {

        // saplings
        combiner(new ItemStack(Items.OAK_SAPLING), Lists.newArrayList(toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.SPRUCE_SAPLING), Lists.newArrayList(toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.JUNGLE_SAPLING), Lists.newArrayList(toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.ACACIA_SAPLING), Lists.newArrayList(toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.DARK_OAK_SAPLING), Lists.newArrayList(toStack("oxygen"),
                toStack("cellulose", 2)));

        combiner(new ItemStack(Items.BIRCH_SAPLING), Lists.newArrayList(toStack("oxygen"),
                toStack("cellulose", 2)));

        // logs
        combiner(new ItemStack(Items.OAK_LOG), Lists.newArrayList(toStack("cellulose")));

        combiner(new ItemStack(Items.SPRUCE_LOG), Lists.newArrayList(toStack("cellulose")));

        combiner(new ItemStack(Items.JUNGLE_LOG), Lists.newArrayList(toStack("cellulose")));

        combiner(new ItemStack(Items.ACACIA_LOG), Lists.newArrayList(toStack("cellulose")));

        combiner(new ItemStack(Items.DARK_OAK_LOG), Lists.newArrayList(toStack("cellulose")));

        combiner(new ItemStack(Items.BIRCH_LOG), Lists.newArrayList(toStack("cellulose")));

        combiner(new ItemStack(Items.CACTUS), Lists.newArrayList(toStack("cellulose"), toStack("mescaline")));

        // food
        combiner(new ItemStack(Items.CARROT), Lists.newArrayList(toStack("cellulose"),
                toStack("beta_carotene")));

        combiner(new ItemStack(Items.SUGAR_CANE), Lists.newArrayList(toStack("cellulose"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.SUGAR), Lists.newArrayList(toStack("sucrose")));

        combiner(new ItemStack(Items.EGG), Lists.newArrayList(toStack("calcium_carbonate", 8),
                toStack("protein", 2)));

        combiner(new ItemStack(Items.POTATO), Lists.newArrayList(toStack("starch"),
                toStack("potassium", 4)));

        combiner(new ItemStack(Items.APPLE), Lists.newArrayList(toStack("cellulose"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.WHEAT_SEEDS), Lists.newArrayList(toStack("triglyceride"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.PUMPKIN_SEEDS), Lists.newArrayList(toStack("triglyceride"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.MELON_SEEDS), Lists.newArrayList(toStack("triglyceride"),
                toStack("sucrose")));

        combiner(new ItemStack(Items.BEETROOT_SEEDS), Lists.newArrayList(toStack("triglyceride"),
                toStack("sucrose"),
                toStack("iron_oxide")));

        combiner(new ItemStack(Items.BEETROOT), Lists.newArrayList(toStack("sucrose"),
                toStack("iron_oxide")));

        // mob drops
        combiner(new ItemStack(Items.BONE_MEAL), Lists.newArrayList(toStack("hydroxylapatite", 2)));

        combiner(new ItemStack(Items.LEATHER), Lists.newArrayList(toStack("protein", 3)));

        combiner(new ItemStack(Items.ROTTEN_FLESH), Lists.newArrayList(toStack("protein", 3)));
        combiner(new ItemStack(Items.SLIME_BALL), Lists.newArrayList(toStack("protein",
                2), toStack("sucrose", 2)));

        combiner(new ItemStack(Items.STRING), Lists.newArrayList(
                toStack("protein", 2)));

        combiner(new ItemStack(Items.FEATHER), Lists.newArrayList(
                toStack("protein", 2)));

        combiner(new ItemStack(Items.SPIDER_EYE), Lists.newArrayList(
                toStack("beta_carotene", 2),
                toStack("protein", 2)));

        combiner(new ItemStack(Items.WHITE_WOOL), Lists.newArrayList(
                toStack("protein"),
                toStack("triglyceride")));

//        combiner(new ItemStack(Items.NETHER_STAR), Lists.newArrayList(
//                toStack("lutetium", 64),
//                toStack("hydrogen", 64),
//                toStack("titanium", 64),
//                toStack("hydrogen", 64),
//                toStack("hydrogen", 64),
//                toStack("hydrogen", 64),
//                toStack("dysprosium", 64),
//                toStack("hydrogen", 64),
//                toStack("mendelevium", 64)));

        // gems
//        combiner(new ItemStack(Items.DIAMOND), Lists.newArrayList(
//                toStack("carbon", 64),
//                toStack("carbon", 64),
//                toStack("carbon", 64),
//                toStack("carbon", 64),
//                toStack("carbon", 64),
//                toStack("carbon", 64),
//                toStack("carbon", 64),
//                toStack("carbon", 64)));

//        combiner(new ItemStack(Items.EMERALD), Lists.newArrayList(
//                toStack("beryl", 8),
//                toStack("chromium", 8),
//                toStack("vanadium", 4)));

//        combiner(new ItemStack(Items.LAPIS_LAZULI), Lists.newArrayList(
//                toStack("sodium", 6),
//                toStack("calcium", 2),
//                toStack("aluminum", 6),
//                toStack("silicon", 6),
//                toStack("oxygen", 24),
//                toStack("sulfur", 2)));

        // dyes
        combiner(new ItemStack(Items.RED_DYE), Lists.newArrayList(toStack("mercury_sulfide", 4)));
        combiner(new ItemStack(Items.PINK_DYE), Lists.newArrayList(toStack("arsenic_sulfide", 4)));
        combiner(new ItemStack(Items.GREEN_DYE), Lists.newArrayList(toStack("nickel_chloride", 4)));
        combiner(new ItemStack(Items.LIME_DYE), Lists.newArrayList(toStack("cadmium_sulfide", 2), toStack("chromium_oxide", 2)));
        combiner(new ItemStack(Items.PURPLE_DYE), Lists.newArrayList(toStack("potassium_permanganate", 4)));
        combiner(new ItemStack(Items.YELLOW_DYE), Lists.newArrayList(toStack("lead_iodide", 4)));
        combiner(new ItemStack(Items.ORANGE_DYE), Lists.newArrayList(toStack("potassium_dichromate", 4)));
        combiner(new ItemStack(Items.BLACK_DYE), Lists.newArrayList(toStack("titanium_oxide", 4)));
        combiner(new ItemStack(Items.GRAY_DYE), Lists.newArrayList(toStack("barium_sulfate", 4)));
        combiner(new ItemStack(Items.MAGENTA_DYE), Lists.newArrayList(toStack("han_purple", 4)));
        combiner(new ItemStack(Items.LIGHT_BLUE_DYE), Lists.newArrayList(toStack("cobalt_aluminate", 2), toStack("antimony_trioxide", 2)));
        combiner(new ItemStack(Items.LIGHT_GRAY_DYE), Lists.newArrayList(toStack("magnesium_sulfate", 4)));
        combiner(new ItemStack(Items.CYAN_DYE), Lists.newArrayList(toStack("copper_chloride", 4)));

        combiner(new ItemStack(Items.BAMBOO), Lists.newArrayList(
                toStack("cellulose")));

//        combiner(new ItemStack(Items.NETHER_WART), Lists.newArrayList(
//                toStack("cellulose"),
//                toStack("germanium", 4),
//                toStack("selenium", 4)));

        combiner(new ItemStack(Items.GLOWSTONE), Lists.newArrayList(toStack("phosphorus", 16)));

        combiner(new ItemStack(Items.GLOWSTONE_DUST), Lists.newArrayList(toStack("phosphorus", 4)));

        combiner(new ItemStack(Items.CHARCOAL), Lists.newArrayList(toStack("carbon", 8)));

        combiner(new ItemStack(Items.COAL), Lists.newArrayList(toStack("carbon", 8)));

//        combiner(new ItemStack(Items.DIRT), Lists.newArrayList(toStack("water"),
//                toStack("cellulose"),
//                toStack("kaolinite")));

//        combiner(new ItemStack(Items.GRASS_BLOCK), Lists.newArrayList(toStack("water"),
//                toStack("cellulose"),
//                toStack("kaolinite")));

        combiner(new ItemStack(Items.GRAVEL), Lists.newArrayList(toStack("silicon_dioxide")));

        combiner(new ItemStack(Items.SAND), Lists.newArrayList(toStack("silicon_dioxide", 4)));
        combiner(new ItemStack(Items.CLAY), Lists.newArrayList(toStack("kaolinite", 4)));

        combiner(new ItemStack(Items.FLINT), Lists.newArrayList(toStack("silicon_dioxide", 3)));

        combiner(new ItemStack(Items.COBBLESTONE), Lists.newArrayList(toStack("silicon_dioxide")));

        combiner(new ItemStack(Items.STONE), Lists.newArrayList(toStack("silicon_dioxide")));

        combiner(new ItemStack(Items.GRANITE), Lists.newArrayList(toStack("silicon_dioxide")));

        combiner(new ItemStack(Items.DIORITE), Lists.newArrayList(toStack("silicon_dioxide")));

        combiner(new ItemStack(Items.ANDESITE), Lists.newArrayList(toStack("silicon_dioxide")));

//        combiner(new ItemStack(Items.MYCELIUM), Lists.newArrayList(toStack("psilocybin"),
//                toStack("water"),
//                toStack("cellulose"),
//                toStack("kaolinite")));

        combiner(new ItemStack(Items.SNOWBALL), Lists.newArrayList(toStack("water", 4)));

        combiner(new ItemStack(Items.SNOW), Lists.newArrayList(toStack("water", 16)));

        combiner(new ItemStack(Items.ICE), Lists.newArrayList(toStack("water", 16)));

        combiner(new ItemStack(Items.WATER_BUCKET), Lists.newArrayList(toStack("water", 16),
                new ItemStack(Items.BUCKET)));

//        combiner(new ItemStack(Items.MILK_BUCKET), Lists.newArrayList(toStack("calcium", 4),
//                toStack("protein", 2),
//                toStack("water", 16),
//                toStack("sucrose"),
//                new ItemStack(Items.BUCKET)));

        combiner(new ItemStack(Items.REDSTONE_BLOCK), Lists.newArrayList(toStack("iron_oxide", 9),
                toStack("strontium_carbonate", 9)));

        combiner(new ItemStack(Items.REDSTONE), Lists.newArrayList(toStack("iron_oxide"), toStack("strontium_carbonate")));

        Map<CompoundItem, List<ItemStack>> overrides = new HashMap<>();
        overrides.put(CompoundRegistry.getByName("triglyceride").get(),
                Lists.newArrayList(toStack("oxygen", 2),
                                toStack("hydrogen", 32),
                                toStack("carbon", 18)));

        overrides.put(CompoundRegistry.getByName("cucurbitacin").get(),
                Lists.newArrayList(toStack("hydrogen", 44),
                                toStack("carbon", 32),
                                toStack("oxygen", 8)));

        overrides.put(CompoundRegistry.getByName("acetic_acid").get(),
                Lists.newArrayList(toStack("carbon", 2),
                        toStack("hydrogen", 4),
                        toStack("oxygen", 2)));

        overrides.put(CompoundRegistry.getByName("carbon_monoxide").get(),
                Lists.newArrayList(toStack("oxygen"), toStack("carbon")));

        overrides.put(CompoundRegistry.getByName("carbon_dioxide").get(),
                Lists.newArrayList(toStack("oxygen", 2), toStack("carbon")));

        overrides.put(CompoundRegistry.getByName("carbonate").get(),
                Lists.newArrayList(toStack("oxygen", 3), toStack("carbon")));

        for (CompoundItem compound : CompoundRegistry.compounds) {
            List<ItemStack> inputs = new ArrayList<>();

            if (overrides.containsKey(compound)) {
                inputs = overrides.get(compound).stream().map(itemStack -> {
                    if (itemStack == null) return ItemStack.EMPTY;
                    else return itemStack;
                }).collect(Collectors.toList());
            } else {
                for (int i = 0; i < compound.shiftedSlots; i++) {
                    inputs.add(ItemStack.EMPTY);
                }
                for (ItemStack component : compound.getComponentStacks()) {
                    inputs.add(component.copy());
                }
            }
            combiner(new ItemStack(compound), inputs);
        }

        for (ElementItem element : ElementRegistry.elements.values()) {
            Item ingot = ForgeRegistries.ITEMS.getValue
                    (new ResourceLocation("chemlib", "ingot_" + element.getChemicalName()));
            if (ingot != Items.AIR) {
                combiner(new ItemStack(ingot), Lists.newArrayList(new ItemStack(element, 16)));
            }
        }
        combiner(new ItemStack(Items.IRON_INGOT), Lists.newArrayList(toStack("iron", 16)));
        combiner(new ItemStack(Items.GOLD_INGOT), Lists.newArrayList(toStack("gold", 16)));
    }

    private ResourceLocation getLocation(ItemStack itemStack) {
        Objects.requireNonNull(itemStack.getItem().getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("combiner/%s", itemStack.getItem().getRegistryName().getPath()));
    }
}
