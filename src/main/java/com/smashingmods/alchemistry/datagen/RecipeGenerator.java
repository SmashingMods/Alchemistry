package com.smashingmods.alchemistry.datagen;

import com.smashingmods.alchemistry.datagen.recipe.atomizer.AtomizerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.combiner.CombinerRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.compactor.CompactorRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.dissolver.DissolverRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.fission.FissionRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.fusion.FusionRecipeProvider;
import com.smashingmods.alchemistry.datagen.recipe.liquifier.LiquifierRecipeProvider;
import com.smashingmods.alchemistry.registry.BlockRegistry;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(HolderLookup.Provider pRegistries, RecipeOutput pOutput) {
        super(pRegistries, pOutput);
    }

    @Override
    protected void buildRecipes() {
        // Tag-backed ingredients resolve their TagKey through this lookup, which yields a
        // forward-referencing HolderSet.Named without binding the tag's contents at gen-time.
        // Cross-mod tags (ChemLib's c:ingots/<element>, c:dusts/…, vanilla c:… tags) are not
        // bound during datagen, so the static BuiltInRegistries.ITEM cannot resolve them here.
        HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);

        AtomizerRecipeProvider.register(this.output);
        CompactorRecipeProvider.register(this.output, items);
        CombinerRecipeProvider.register(this.output, items);
        DissolverRecipeProvider.register(this.output, items);
        LiquifierRecipeProvider.register(this.output, items);
        FissionRecipeProvider.register(this.output);
        FusionRecipeProvider.register(this.output);
        generateMachineRecipes(items);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void generateMachineRecipes(HolderGetter<Item> items) {
        Item atomizer = BlockRegistry.ATOMIZER.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, atomizer)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('C', Items.CAULDRON)
                .pattern("IPI")
                .pattern("CRC")
                .pattern("IPI")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        Item liquifier = BlockRegistry.LIQUIFIER.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, liquifier)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('S', Items.STONE_PRESSURE_PLATE)
                .define('C', Items.CAULDRON)
                .pattern("IPI")
                .pattern("SRS")
                .pattern("ICI")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        Item combiner = BlockRegistry.COMBINER.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, combiner)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('D', Items.DIAMOND)
                .define('O', Items.OBSIDIAN)
                .pattern("IDI")
                .pattern("ORO")
                .pattern("IPI")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        Item compactor = BlockRegistry.COMPACTOR.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, compactor)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('S', Items.STONE_PRESSURE_PLATE)
                .pattern("IPI")
                .pattern("SRS")
                .pattern("IPI")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        Item dissolver = BlockRegistry.DISSOLVER.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, dissolver)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('M', Items.MAGMA_BLOCK)
                .pattern("IPI")
                .pattern("MRM")
                .pattern("IPI")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(this.output);

        Item reactorCasing = BlockRegistry.REACTOR_CASING.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, reactorCasing, 4)
                .group("machines")
                .define('O', ItemRegistry.getChemicalItemByNameAndType("osmium", ChemicalItemType.INGOT).get())
                .define('P', ItemRegistry.getChemicalItemByNameAndType("platinum", ChemicalItemType.INGOT).get())
                .define('B', Items.BLAZE_POWDER)
                .pattern("OPO")
                .pattern("PBP")
                .pattern("OPO")
                .unlockedBy("has_item", has(Items.BLAZE_POWDER))
                .save(this.output);

        Item reactorInput = BlockRegistry.REACTOR_INPUT.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, reactorInput)
                .group("machines")
                .define('H', Items.HOPPER)
                .define('C', reactorCasing)
                .pattern("H")
                .pattern("C")
                .pattern("H")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);

        Item reactorOutput = BlockRegistry.REACTOR_OUTPUT.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, reactorOutput)
                .group("machines")
                .define('D', Items.DROPPER)
                .define('C', reactorCasing)
                .pattern("D")
                .pattern("C")
                .pattern("D")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);

        Item reactorEnergy = BlockRegistry.REACTOR_ENERGY.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, reactorEnergy)
                .group("machines")
                .define('D', Items.REDSTONE)
                .define('C', reactorCasing)
                .pattern("D")
                .pattern("C")
                .pattern("D")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);

        Item fissionController = BlockRegistry.FISSION_CONTROLLER.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, fissionController)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('C', reactorCasing)
                .define('G', Items.GLASS)
                .define('D', Items.GLOWSTONE_DUST)
                .pattern("ICI")
                .pattern("GDR")
                .pattern("ICI")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);

        Item fusionController = BlockRegistry.FUSION_CONTROLLER.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, fusionController)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('C', reactorCasing)
                .define('G', Items.GLASS)
                .define('S', Items.NETHER_STAR)
                .pattern("ICI")
                .pattern("GSR")
                .pattern("ICI")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);

        Item fissionCore = BlockRegistry.FISSION_CORE.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, fissionCore)
                .group("machines")
                .define('Y', ItemRegistry.getChemicalItemByNameAndType("yttrium", ChemicalItemType.INGOT).get())
                .define('B', Items.BLAZE_ROD)
                .pattern("YBY")
                .pattern("YBY")
                .pattern("YBY")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);

        Item fusionCore = BlockRegistry.FUSION_CORE.get().asItem();
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, fusionCore)
                .group("machines")
                .define('T', ItemRegistry.getChemicalItemByNameAndType("tungsten", ChemicalItemType.INGOT).get())
                .define('N', Items.NETHERITE_SCRAP)
                .pattern("TNT")
                .pattern("TNT")
                .pattern("TNT")
                .unlockedBy("has_item", has(reactorCasing))
                .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
            super(pOutput, pRegistries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider pRegistries, RecipeOutput pOutput) {
            return new RecipeGenerator(pRegistries, pOutput);
        }

        @Override
        public String getName() {
            return "Alchemistry Recipes";
        }
    }
}