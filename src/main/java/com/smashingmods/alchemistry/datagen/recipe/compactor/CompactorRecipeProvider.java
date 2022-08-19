package com.smashingmods.alchemistry.datagen.recipe.compactor;

import com.smashingmods.alchemistry.Alchemistry;
import com.smashingmods.alchemistry.api.item.IngredientStack;
import com.smashingmods.alchemistry.datagen.DatagenUtil;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.api.MetalType;
import com.smashingmods.chemlib.common.items.ChemicalItem;
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
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

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
                output.ifPresent(chemicalItem -> compactor(new ItemStack(element, 16), chemicalItem));
            }
        }

        for (CompoundItem compound : ItemRegistry.getCompounds()) {
            if (compound.getMatterState().equals(MatterState.SOLID)) {
                Optional<ChemicalItem> output = ItemRegistry.getChemicalItemByNameAndType(compound.getChemicalName(), ChemicalItemType.COMPOUND);
                output.ifPresent(chemicalItem -> compactor(new ItemStack(compound, 8), chemicalItem));
            }
        }

        // logs
        List<Item> logs = Stream.of(Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.DARK_OAK_LOG, Items.ACACIA_LOG).toList();
        ItemRegistry.getCompoundByName("cellulose").ifPresent(cellulose -> {
            for (Item log : logs) {
                compactor(cellulose, log);
            }
            compactor(cellulose, Items.BAMBOO);
            compactor(cellulose, new ItemStack(Items.PAPER));
            compactor(cellulose, new ItemStack(Items.LILY_PAD));
        });
        
        // stones
        ItemRegistry.getCompoundByName("silicon_dioxide").ifPresent(siliconDioxide -> {
            List<Item> silicates = Stream.of(Items.GRAVEL, Items.COBBLESTONE, Items.STONE, Items.GRANITE, Items.DIORITE, Items.ANDESITE).toList();
            for (Item silicate : silicates) {
                compactor(siliconDioxide, silicate);
            }
            compactor(new ItemStack(siliconDioxide, 4), Items.SAND);
            compactor(new ItemStack(siliconDioxide, 3), Items.FLINT);
        });

        ItemRegistry.getCompoundByName("calcium_carbonate").ifPresent(carbonate -> {
            compactor(carbonate, new ItemStack(Items.CALCITE));
            compactor(carbonate, Items.POINTED_DRIPSTONE);
        });

        ItemRegistry.getCompoundByName("kaolinite").ifPresent(kaolinite -> {
            compactor(kaolinite, Items.CLAY_BALL);
            compactor(new ItemStack(kaolinite, 4), Items.CLAY);
        });

        // dyes
        ItemRegistry.getCompoundByName("mercury_sulfide").ifPresent(mercurySulfide -> compactor(new ItemStack(mercurySulfide, 4), Items.RED_DYE));
        ItemRegistry.getCompoundByName("arsenic_sulfide").ifPresent(arsenicSulfide -> compactor(new ItemStack(arsenicSulfide, 4), Items.PINK_DYE));
        ItemRegistry.getCompoundByName("nickel_chloride").ifPresent(nickelChloride -> compactor(new ItemStack(nickelChloride, 4), Items.GREEN_DYE));
        ItemRegistry.getCompoundByName("chromium_oxide").ifPresent(chromiumOxide -> compactor(new ItemStack(chromiumOxide, 4), Items.LIME_DYE));
        ItemRegistry.getCompoundByName("potassium_permanganate").ifPresent(potassiumPermanganate -> compactor(new ItemStack(potassiumPermanganate, 4), Items.PURPLE_DYE));
        ItemRegistry.getCompoundByName("lead_iodide").ifPresent(leadIodide -> compactor(new ItemStack(leadIodide, 4), Items.YELLOW_DYE));
        ItemRegistry.getCompoundByName("potassium_dichromate").ifPresent(potassiumDichromate -> compactor(new ItemStack(potassiumDichromate, 4), Items.ORANGE_DYE));
        ItemRegistry.getCompoundByName("titanium_oxide").ifPresent(titaniumOxide -> compactor(new ItemStack(titaniumOxide, 4), Items.BLACK_DYE));
        ItemRegistry.getCompoundByName("barium_sulfate").ifPresent(bariumSulfate -> compactor(new ItemStack(bariumSulfate, 4), Items.GRAY_DYE));
        ItemRegistry.getCompoundByName("han_purple").ifPresent(hanPurple -> compactor(new ItemStack(hanPurple, 4), Items.MAGENTA_DYE));
        ItemRegistry.getCompoundByName("cobalt_aluminate").ifPresent(cobaltAluminate -> compactor(new ItemStack(cobaltAluminate, 4), Items.LIGHT_BLUE_DYE));
        ItemRegistry.getCompoundByName("magnesium_sulfate").ifPresent(magnesiumSulfate -> compactor(new ItemStack(magnesiumSulfate, 4), Items.LIGHT_GRAY_DYE));
        ItemRegistry.getCompoundByName("copper_chloride").ifPresent(copperChloride -> compactor(new ItemStack(copperChloride, 4), Items.CYAN_DYE));
        ItemRegistry.getCompoundByName("hydroxylapatite").ifPresent(hydroxlapatite -> compactor(new ItemStack(hydroxlapatite, 2), new ItemStack(Items.BONE_MEAL, 3)));
        ItemRegistry.getCompoundByName("hydroxylapatite").ifPresent(hydroxlapatite -> compactor(new ItemStack(hydroxlapatite, 6), Items.BONE_BLOCK));

        ItemRegistry.getCompoundByName("sucrose").ifPresent(sucrose -> compactor(sucrose, Items.SUGAR));
        ItemRegistry.getCompoundByName("sucrose").ifPresent(sucrose -> compactor(sucrose, Items.SUGAR_CANE));

        ItemRegistry.getCompoundByName("graphite").ifPresent(graphite -> {
            ItemRegistry.getElementByName("carbon").ifPresent(carbon -> compactor(new ItemStack(carbon, 4), new ItemStack(graphite)));
            compactor(new ItemStack(graphite, 2), new ItemStack(Items.COAL));
            compactor(new ItemStack(graphite, 2), new ItemStack(Items.CHARCOAL));
        });

        ItemRegistry.getCompoundByName("phosphorus").ifPresent(phosphorus -> {
            compactor(new ItemStack(phosphorus, 4), new ItemStack(Items.GLOWSTONE_DUST));
            compactor(new ItemStack(phosphorus, 16), new ItemStack(Items.GLOWSTONE));
        });

        ItemRegistry.getCompoundByName("water").ifPresent(water -> {
            compactor(new ItemStack(water, 4), Items.SNOWBALL);
            compactor(new ItemStack(water, 16), Items.SNOW);
            compactor(new ItemStack(water, 16), Items.ICE);
        });

        ItemRegistry.getCompoundByName("protein").ifPresent(protein -> {
            compactor(new ItemStack(protein, 3), Items.LEATHER);
            compactor(new ItemStack(protein, 3), Items.ROTTEN_FLESH);
            for (Item item : newArrayList(Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT)) {
                compactor(new ItemStack(protein, 4), new ItemStack(item));
            }
        });

        ItemRegistry.getCompoundByName("keratin").ifPresent(keratin -> {
            compactor(new ItemStack(keratin, 2), Items.FEATHER);
            compactor(keratin, new ItemStack(Items.STRING, 2));
        });
    }

    public void compactor(ItemStack pInput, ItemStack pOutput) {
        compactor(new IngredientStack(pInput), pOutput, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.getItem())));
    }

    public void compactor(ItemLike pInput, ItemStack pOutput) {
        compactor(new IngredientStack(pInput), pOutput, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.getItem())));
    }

    public void compactor(ItemLike pInput, ItemLike pOutput) {
        compactor(new IngredientStack(pInput), new ItemStack(pOutput), Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.asItem())));
    }

    public void compactor(ItemStack pInput, ItemLike pOutput) {
        compactor(new IngredientStack(pInput), new ItemStack(pOutput), Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.asItem())));
    }

    private void compactor(String pInputTag, ItemStack pOutput) {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(pInputTag));
        Ingredient ingredient = Ingredient.of(tagKey);
        compactor(ingredient, pOutput);
    }

    private void compactor(String pInputTag, int pCount, ItemStack pOutput) {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(pInputTag));
        compactor(new IngredientStack(Ingredient.of(tagKey), pCount), pOutput, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.getItem())));
    }

    public void compactor(Ingredient pInput, ItemStack pOutput) {
        compactor(new IngredientStack(pInput), pOutput, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.getItem())));
    }

    private void compactor(String pInputTag, ItemLike pItemLike, ICondition pCondition) {
        compactor(pInputTag, new ItemStack(pItemLike), pCondition);
    }

    private void compactor(String pInputTag, ItemStack pOutput, ICondition pCondition) {
        compactor(pInputTag, 1, pOutput, pCondition);
    }

    private void compactor(String pInputTag, int pCount, ItemStack pOutput, ICondition pCondition) {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(pInputTag));
        compactor(new IngredientStack(Ingredient.of(tagKey), pCount), pOutput, pCondition);
    }

    private void compactor(IngredientStack pInput, ItemStack pOutput, ICondition pCondition) {
        ResourceLocation recipeId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(pOutput.getItem()));
        ConditionalRecipe.builder()
                .addCondition(pCondition)
                .addRecipe(CompactorRecipeBuilder.createRecipe(pInput, pOutput, recipeId)
                        .group(String.format("%s:compactor", Alchemistry.MODID))
                        .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(DatagenUtil.getLocation(pOutput, "compactor")))
                        ::save)
                .build(consumer, new ResourceLocation(Alchemistry.MODID, String.format("compactor/%s", recipeId.getPath())));
    }

    public void compactor(IngredientStack pInput, ItemStack pOutput, ResourceLocation pRecipeId) {
        CompactorRecipeBuilder.createRecipe(pInput, pOutput, pRecipeId)
                .group(String.format("%s:compactor", Alchemistry.MODID))
                .unlockedBy("has_the_recipe", RecipeUnlockedTrigger.unlocked(DatagenUtil.getLocation(pOutput, "compactor")))
                .save(consumer);
    }
}
