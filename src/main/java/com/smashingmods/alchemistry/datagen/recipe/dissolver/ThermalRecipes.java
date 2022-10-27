package com.smashingmods.alchemistry.datagen.recipe.dissolver;

import com.smashingmods.alchemistry.api.recipe.compatability.ItemTagType;
import com.smashingmods.alchemistry.api.recipe.compatability.ThermalMetalType;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet.Builder.createSet;
import static com.smashingmods.alchemistry.datagen.DatagenUtil.tagNotEmptyCondition;
import static com.smashingmods.alchemistry.datagen.DatagenUtil.toItemStack;

public class ThermalRecipes extends DissolverRecipeProvider {

    public ThermalRecipes(Consumer<FinishedRecipe> pConsumer) {
        super(pConsumer);
    }

    public static void register(Consumer<FinishedRecipe> pConsumer) {
        new ThermalRecipes(pConsumer).register();
    }
    
    protected void register() {
        dissolver("forge:slag", createSet().weighted()
                        .addGroup(20)
                        .addGroup(20, toItemStack("silicon_dioxide"))
                        .addGroup(3, toItemStack("magnesium_oxide"))
                        .addGroup(3, toItemStack("iron_oxide"))
                        .addGroup(2, toItemStack("manganese_oxide"))
                        .addGroup(2, toItemStack("aluminum_oxide"))
                        .addGroup(1, toItemStack("phosphate")).build(),
                tagNotEmptyCondition("forge:slag"));

        dissolver("forge:bitumen", createSet().weighted()
                        .addGroup(5, toItemStack("carbon", 26))
                        .addGroup(5, toItemStack("oxygen"))
                        .addGroup(5, toItemStack("hydrogen", 32))
                        .addGroup(1, toItemStack("sulfur_dioxide"))
                        .addGroup(1, toItemStack("vanadium"))
                        .addGroup(1, toItemStack("nickel")).build(),
                tagNotEmptyCondition("forge:bitumen"));

        dissolver("forge:storage_blocks/bitumen", createSet().weighted().rolls(9)
                        .addGroup(5, toItemStack("carbon", 26))
                        .addGroup(5, toItemStack("oxygen"))
                        .addGroup(5, toItemStack("hydrogen", 32))
                        .addGroup(1, toItemStack("sulfur_dioxide"))
                        .addGroup(1, toItemStack("vanadium"))
                        .addGroup(1, toItemStack("nickel")).build(),
                tagNotEmptyCondition("forge:storage_block/bitumen"));

        dissolver("thermal:rockwool", createSet()
                        .addGroup(100, toItemStack("silicon_dioxide", 2))
                        .addGroup(100, toItemStack("aluminum_oxide")).build(),
                tagNotEmptyCondition("thermal:rockwool"));

        dissolver("forge:gems/apatite", createSet()
                        .addGroup(33, toItemStack("hydroxylapatite", 1)).build(),
                tagNotEmptyCondition("forge:gems/apatite"));

        dissolver("forge:dusts/apatite", createSet()
                        .addGroup(33, toItemStack("hydroxylapatite", 1)).build(),
                tagNotEmptyCondition("forge:dusts/apatite"));

        dissolver("forge:storage_blocks/apatite", createSet()
                        .addGroup(100, toItemStack("hydroxylapatite", 3)).build(),
                tagNotEmptyCondition("forge:storage_blocks/apatite"));

        dissolver("forge:ores/apatite", createSet()
                        .addGroup(100, toItemStack("hydroxylapatite", 2)).build(),
                tagNotEmptyCondition("forge:ores/apatite"));

        dissolver("forge:gems/cinnabar", createSet()
                        .addGroup(100, toItemStack("mercury_sulfide", 1)).build(),
                tagNotEmptyCondition("forge:gems/cinnabar"));

        dissolver("forge:dusts/cinnabar", createSet()
                        .addGroup(100, toItemStack("mercury_sulfide", 1)).build(),
                tagNotEmptyCondition("forge:dusts/cinnabar"));

        dissolver("forge:storage_blocks/cinnabar", createSet()
                        .addGroup(100, toItemStack("mercury_sulfide", 9)).build(),
                tagNotEmptyCondition("forge:storage_blocks/cinnabar"));

        dissolver("forge:ores/cinnabar", createSet()
                        .addGroup(100, toItemStack("mercury_sulfide", 3)).build(),
                tagNotEmptyCondition("forge:ores/cinnabar"));

        dissolver("forge:gems/niter", createSet()
                        .addGroup(50, toItemStack("potassium_nitrate", 1)).build(),
                tagNotEmptyCondition("forge:gems/niter"));

        dissolver("forge:dusts/niter", createSet()
                        .addGroup(50, toItemStack("potassium_nitrate", 1)).build(),
                tagNotEmptyCondition("forge:dusts/niter"));

        dissolver("forge:storage_blocks/niter", createSet()
                        .addGroup(100, toItemStack("potassium_nitrate", 4))
                        .addGroup(50, toItemStack("potassium_nitrate", 1)).build(),
                tagNotEmptyCondition("forge:storage_blocks/niter"));

        dissolver("forge:ores/niter", createSet()
                        .addGroup(100, toItemStack("sulfur", 3)).build(),
                tagNotEmptyCondition("forge:ores/niter"));

        dissolver("forge:gems/sulfur", createSet()
                        .addGroup(50, toItemStack("sulfur", 1)).build(),
                tagNotEmptyCondition("forge:gems/sulfur"));

        dissolver("forge:dusts/sulfur", createSet()
                        .addGroup(50, toItemStack("sulfur", 1)).build(),
                tagNotEmptyCondition("forge:dusts/sulfur"));

        dissolver("forge:storage_blocks/sulfur", createSet()
                        .addGroup(100, toItemStack("sulfur", 4))
                        .addGroup(50, toItemStack("sulfur", 1)).build(),
                tagNotEmptyCondition("forge:storage_blocks/sulfur"));

        dissolver("forge:ores/sulfur", createSet()
                        .addGroup(100, toItemStack("sulfur", 3)).build(),
                tagNotEmptyCondition("forge:ores/sulfur"));

        dissolver("forge:coal_coke", createSet().addGroup(100, toItemStack("graphite", 2)).build(),
                tagNotEmptyCondition("forge:coal_coke"));

        dissolver("forge:dusts/netherite", createSet()
                        .addGroup(100, toItemStack("tungsten", 16 * 4), toItemStack("carbon", 16 * 4), toItemStack("gold", 16 * 4)).build(),
                tagNotEmptyCondition("forge:dusts/netherite"));

        dissolver("forge:plates/netherite", createSet()
                        .addGroup(100, toItemStack("tungsten", 16 * 4), toItemStack("carbon", 16 * 4), toItemStack("gold", 16 * 4)).build(),
                tagNotEmptyCondition("forge:plates/netherite"));

        for (ItemTagType itemType : ItemTagType.values()) {
            int rolls = itemType.equals(ItemTagType.NUGGETS) ? 1 : 16;
            int count = itemType.equals(ItemTagType.STORAGE_BLOCKS) ? 9 : 1;

            for (ThermalMetalType metalType : ThermalMetalType.values()) {
                String itemTag = String.format("forge:%s/%s", itemType.getSerializedName(), metalType.getSerializedName());
                switch (metalType) {

                    case BRONZE -> dissolver(itemTag, createSet().rolls(rolls)
                                    .addGroup(75, toItemStack("copper", count))
                                    .addGroup(25, toItemStack("tin", count)).build(),
                            tagNotEmptyCondition(itemTag));

                    case INVAR -> dissolver(itemTag, createSet().rolls(rolls).weighted()
                                    .addGroup(2, toItemStack("iron", count))
                                    .addGroup(1, toItemStack("nickel", count)).build(),
                            tagNotEmptyCondition(itemTag));

                    case ELECTRUM -> dissolver(itemTag, createSet().rolls(rolls).weighted()
                                    .addGroup(1, toItemStack("gold", count))
                                    .addGroup(1, toItemStack("silver", count)).build(),
                            tagNotEmptyCondition(itemTag));

                    case CONSTANTAN -> dissolver(itemTag, createSet().rolls(rolls).weighted()
                                    .addGroup(1, toItemStack("nickel", count))
                                    .addGroup(1, toItemStack("copper", count)).build(),
                            tagNotEmptyCondition(itemTag));

                    case SIGNALUM -> dissolver(itemTag, createSet().rolls(rolls).weighted()
                                    .addGroup(6, toItemStack("copper", count))
                                    .addGroup(2, toItemStack("silver", count))
                                    .addGroup(1, toItemStack("strontium_carbonate", count))
                                    .addGroup(1, toItemStack("iron_oxide", count)).build(),
                            tagNotEmptyCondition(itemTag));

                    case LUMIUM -> dissolver(itemTag, createSet().rolls(rolls).weighted()
                                    .addGroup(3, toItemStack("tin", count))
                                    .addGroup(1, toItemStack("silver", count))
                                    .addGroup(1, toItemStack("phosphorus", count)).build(),
                            tagNotEmptyCondition(itemTag));

                    case ENDERIUM -> dissolver(itemTag, createSet().rolls(rolls).weighted()
                                    .addGroup(6, toItemStack("lead", count))
                                    .addGroup(2, toItemStack("graphite", count))
                                    .addGroup(1, toItemStack("mercury", 2 * count))
                                    .addGroup(1, toItemStack("neodymium", 2 * count)).build(),
                            tagNotEmptyCondition(itemTag));
                }
            }
        }
    }
}
