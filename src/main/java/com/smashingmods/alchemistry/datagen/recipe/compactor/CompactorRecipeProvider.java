package com.smashingmods.alchemistry.datagen.recipe.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.datagen.recipe.atomizer.AtomizerRecipeProvider;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CompactorRecipeProvider {

    private final Consumer<FinishedRecipe> consumer;

    public CompactorRecipeProvider(Consumer<FinishedRecipe> pConsumer) {
        this.consumer = pConsumer;
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new CompactorRecipeProvider(pConsumer).register();
    }

    private void register() {
        
        // dusts
        for (ElementItem element : ItemRegistry.getElements()) {
            if (element.getMetalType().equals(MetalType.METAL) && !element.isArtificial()) {
                Optional<ChemicalItem> output = ItemRegistry.getChemicalItemByNameAndType(element.getChemicalName(), ChemicalItemType.DUST);
                output.ifPresent(chemicalItem -> compactor(new ItemStack(element, 16), new ItemStack(chemicalItem)));
            }
        }

        // logs
        List<ItemStack> logs = Arrays.asList(Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.DARK_OAK_LOG, Items.ACACIA_LOG).stream().map(ItemStack::new).collect(Collectors.toList());
        ItemStack cellulose = new ItemStack(ItemRegistry.getCompoundByName("cellulose").get());
        for (ItemStack log : logs) {
            compactor(cellulose, log);
        }
        compactor(cellulose, new ItemStack(Items.BAMBOO));
        
        // stones
        List<ItemStack> silicates = Arrays.asList(Items.GRAVEL, Items.COBBLESTONE, Items.STONE, Items.GRANITE, Items.DIORITE, Items.ANDESITE).stream().map(ItemStack::new).collect(Collectors.toList());
        Item siliconDioxide = ItemRegistry.getCompoundByName("silicon_dioxide").get();
        for (ItemStack silicate : silicates) {
            compactor(new ItemStack(siliconDioxide), silicate);
        }
        compactor(new ItemStack(siliconDioxide, 4), new ItemStack(Items.SAND));
        compactor(new ItemStack(siliconDioxide, 3), new ItemStack(Items.FLINT));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("kaolinite").get()), new ItemStack(Items.CLAY_BALL));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("kaolinite").get(), 4), new ItemStack(Items.CLAY));

        // dyes
        compactor(new ItemStack(ItemRegistry.getCompoundByName("mercury_sulfide").get(), 4), new ItemStack(Items.RED_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("arsenic_sulfide").get(), 4), new ItemStack(Items.PINK_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("nickel_chloride").get(), 4), new ItemStack(Items.GREEN_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("chromium_oxide").get(), 4), new ItemStack(Items.LIME_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("potassium_permanganate").get(), 4), new ItemStack(Items.PURPLE_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("lead_iodide").get(), 4), new ItemStack(Items.YELLOW_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("potassium_dichromate").get(), 4), new ItemStack(Items.ORANGE_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("titanium_oxide").get(), 4), new ItemStack(Items.BLACK_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("barium_sulfate").get(), 4), new ItemStack(Items.GRAY_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("han_purple").get(), 4), new ItemStack(Items.MAGENTA_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("cobalt_aluminate").get(), 4), new ItemStack(Items.LIGHT_BLUE_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("magnesium_sulfate").get(), 4), new ItemStack(Items.LIGHT_GRAY_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("copper_chloride").get(), 4), new ItemStack(Items.CYAN_DYE));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("hydroxylapatite").get(), 2), new ItemStack(Items.BONE_MEAL, 3));
        
        compactor(new ItemStack(ItemRegistry.getCompoundByName("sucrose").get()), new ItemStack(Items.SUGAR));
        Item graphite = ItemRegistry.getCompoundByName("graphite").get();
        compactor(new ItemStack(ItemRegistry.getElementByName("carbon").get(), 4), new ItemStack(graphite));
        compactor(new ItemStack(graphite, 2), new ItemStack(Items.COAL));
        compactor(new ItemStack(graphite, 2), new ItemStack(Items.CHARCOAL));
        compactor(new ItemStack(ItemRegistry.getElementByName("phosphorus").get(), 4), new ItemStack(Items.GLOWSTONE_DUST));
        compactor(new ItemStack(ItemRegistry.getElementByName("phosphorus").get(), 16), new ItemStack(Items.GLOWSTONE));

        Item water = ItemRegistry.getCompoundByName("water").get();
        compactor(new ItemStack(water, 4), new ItemStack(Items.SNOWBALL));
        compactor(new ItemStack(water, 16), new ItemStack(Items.SNOW));
        compactor(new ItemStack(water, 16), new ItemStack(Items.ICE));

        compactor(new ItemStack(ItemRegistry.getCompoundByName("protein").get(), 3), new ItemStack(Items.LEATHER));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("protein").get(), 3), new ItemStack(Items.ROTTEN_FLESH));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("protein").get(), 2), new ItemStack(Items.FEATHER));
        compactor(new ItemStack(ItemRegistry.getCompoundByName("protein").get(), 1), new ItemStack(Items.STRING, 2));
    }

    public void compactor(ItemStack pInput, ItemStack pResult) {
        CompactorRecipeBuilder.createRecipe(pInput, pResult)
                .group("compactor")
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(getLocation(pResult)))
                .save(consumer);
    }

    private ResourceLocation getLocation(ItemStack pItemStack) {
        Objects.requireNonNull(pItemStack.getItem().getRegistryName());
        return new ResourceLocation(Alchemistry.MODID, String.format("compactor/%s", pItemStack.getItem().getRegistryName().getPath()));
    }
}
