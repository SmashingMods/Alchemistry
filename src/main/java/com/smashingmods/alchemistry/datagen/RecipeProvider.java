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
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {

    public RecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> pConsumer) {
        AtomizerRecipeProvider.register(pConsumer);
        CompactorRecipeProvider.register(pConsumer);
        CombinerRecipeProvider.register(pConsumer);
        DissolverRecipeProvider.register(pConsumer);
        LiquifierRecipeProvider.register(pConsumer);
        FissionRecipeProvider.register(pConsumer);
        FusionRecipeProvider.register(pConsumer);
        generateMachineRecipes(pConsumer);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void generateMachineRecipes(Consumer<FinishedRecipe> pConsumer) {

        Item atomizer = BlockRegistry.ATOMIZER.get().asItem();
        ShapedRecipeBuilder.shaped(atomizer)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('C', Items.CAULDRON)
                .pattern("IPI")
                .pattern("CRC")
                .pattern("IPI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(pConsumer);

        Item liquifier = BlockRegistry.LIQUIFIER.get().asItem();
        ShapedRecipeBuilder.shaped(liquifier)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('S', Items.STONE_PRESSURE_PLATE)
                .define('C', Items.CAULDRON)
                .pattern("IPI")
                .pattern("SRS")
                .pattern("ICI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(pConsumer);

        Item combiner = BlockRegistry.COMBINER.get().asItem();
        ShapedRecipeBuilder.shaped(combiner)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('D', Items.DIAMOND)
                .define('O', Items.OBSIDIAN)
                .pattern("IDI")
                .pattern("ORO")
                .pattern("IPI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(pConsumer);

        Item compactor = BlockRegistry.COMPACTOR.get().asItem();
        ShapedRecipeBuilder.shaped(compactor)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('S', Items.STONE_PRESSURE_PLATE)
                .pattern("IPI")
                .pattern("SRS")
                .pattern("IPI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(pConsumer);

        Item dissolver = BlockRegistry.DISSOLVER.get().asItem();
        ShapedRecipeBuilder.shaped(dissolver)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .define('M', Items.MAGMA_BLOCK)
                .pattern("IPI")
                .pattern("MRM")
                .pattern("IPI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.IRON_INGOT).build()))
                .save(pConsumer);

        Item reactorCasing = BlockRegistry.REACTOR_CASING.get().asItem();
        ShapedRecipeBuilder.shaped(reactorCasing, 4)
                .group("machines")
                .define('O', ItemRegistry.getChemicalItemByNameAndType("osmium", ChemicalItemType.INGOT).get())
                .define('P', ItemRegistry.getChemicalItemByNameAndType("platinum", ChemicalItemType.INGOT).get())
                .define('B', Items.BLAZE_POWDER)
                .pattern("OPO")
                .pattern("PBP")
                .pattern("OPO")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(Items.BLAZE_POWDER).build()))
                .save(pConsumer);

        Item reactorInput = BlockRegistry.REACTOR_INPUT.get().asItem();
        ShapedRecipeBuilder.shaped(reactorInput)
                .group("machines")
                .define('H', Items.HOPPER)
                .define('C', reactorCasing)
                .pattern("H")
                .pattern("C")
                .pattern("H")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);

        Item reactorOutput = BlockRegistry.REACTOR_OUTPUT.get().asItem();
        ShapedRecipeBuilder.shaped(reactorOutput)
                .group("machines")
                .define('D', Items.DROPPER)
                .define('C', reactorCasing)
                .pattern("D")
                .pattern("C")
                .pattern("D")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);

        Item reactorEnergy = BlockRegistry.REACTOR_ENERGY.get().asItem();
        ShapedRecipeBuilder.shaped(reactorEnergy)
                .group("machines")
                .define('D', Items.REDSTONE)
                .define('C', reactorCasing)
                .pattern("D")
                .pattern("C")
                .pattern("D")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);

        Item fissionController = BlockRegistry.FISSION_CONTROLLER.get().asItem();
        ShapedRecipeBuilder.shaped(fissionController)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('C', reactorCasing)
                .define('G', Items.GLASS)
                .define('D', Items.GLOWSTONE_DUST)
                .pattern("ICI")
                .pattern("GDR")
                .pattern("ICI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);

        Item fusionController = BlockRegistry.FUSION_CONTROLLER.get().asItem();
        ShapedRecipeBuilder.shaped(fusionController)
                .group("machines")
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('C', reactorCasing)
                .define('G', Items.GLASS)
                .define('S', Items.NETHER_STAR)
                .pattern("ICI")
                .pattern("GSR")
                .pattern("ICI")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);

        Item fissionCore = BlockRegistry.FISSION_CORE.get().asItem();
        ShapedRecipeBuilder.shaped(fissionCore)
                .group("machines")
                .define('Y', ItemRegistry.getChemicalItemByNameAndType("yttrium", ChemicalItemType.INGOT).get())
                .define('B', Items.BLAZE_ROD)
                .pattern("YBY")
                .pattern("YBY")
                .pattern("YBY")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);

        Item fusionCore = BlockRegistry.FUSION_CORE.get().asItem();
        ShapedRecipeBuilder.shaped(fusionCore)
                .group("machines")
                .define('T', ItemRegistry.getChemicalItemByNameAndType("tungsten", ChemicalItemType.INGOT).get())
                .define('N', Items.NETHERITE_SCRAP)
                .pattern("TNT")
                .pattern("TNT")
                .pattern("TNT")
                .unlockedBy("has_item", inventoryTrigger(ItemPredicate.Builder.item().of(reactorCasing).build()))
                .save(pConsumer);
    }
}